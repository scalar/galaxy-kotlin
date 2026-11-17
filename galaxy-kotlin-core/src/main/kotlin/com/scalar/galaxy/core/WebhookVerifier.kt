package com.scalar.galaxy.core

import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Verifies a Standard Webhooks signature with the JDK alone.
 *
 * A delivery carries an id, a timestamp and a signature header (`webhook-id`, `webhook-timestamp`
 * and `webhook-signature` unless the helper names others), and the signature is a base64 HMAC over
 * `"$id.$timestamp.$payload"`, keyed with the signing secret
 * (https://github.com/standard-webhooks/standard-webhooks). HMAC-SHA1, HMAC-SHA256 and HMAC-SHA512
 * are checked; any other [algorithm] refuses every delivery, since this class cannot check it and
 * accepting one unchecked would present a forged payload as verified.
 *
 * Under an HMAC [algorithm], a secret that is not valid base64, or decodes to no bytes, is a
 * misconfiguration rather than a failed delivery, and throws `IllegalArgumentException`: a
 * non-base64 secret from the constructor, and an empty key from [verify] once a delivery's headers
 * and timestamp have passed their checks. Under any other algorithm the secret is not read at all.
 *
 * @param secret the signing secret, optionally prefixed with `whsec_`.
 * @param algorithm the helper's signature algorithm, spelled as the SDK config spells it
 *   (`hmac-sha256`).
 * @param idHeader the header carrying the delivery's id.
 * @param timestampHeader the header carrying the delivery's Unix timestamp, in seconds.
 * @param signatureHeader the header carrying the delivery's signatures.
 * @param toleranceSeconds how far, in seconds, the timestamp may sit either side of the clock.
 * @param parseUnsafeMethod the helper's method that decodes without verifying, which a refusal for
 *   an algorithm this class cannot check names, or null when the helper has none.
 * @throws IllegalArgumentException if [algorithm] is an HMAC and the secret is not valid base64.
 */
internal class WebhookVerifier(
    secret: String,
    private val algorithm: String = DEFAULT_ALGORITHM,
    private val idHeader: String = ID_HEADER,
    private val timestampHeader: String = TIMESTAMP_HEADER,
    private val signatureHeader: String = SIGNATURE_HEADER,
    private val toleranceSeconds: Long = TOLERANCE_SECONDS,
    private val parseUnsafeMethod: String? = "parseUnsafe",
) {

    // The JDK's name for the configured algorithm, or null for one this class cannot check.
    private val macAlgorithm: String? = JDK_MAC_ALGORITHMS[algorithm]

    // A sender prints the secret as `whsec_` followed by the base64 signing key, and the key is the
    // decoded bytes, not the text. A secret that is not base64 throws `IllegalArgumentException`
    // here rather than a `WebhookVerificationException`: it is a misconfigured client, not a forged
    // delivery. Under an algorithm that is not an HMAC the secret is some other kind of key, so it
    // is left undecoded: every delivery is refused anyway.
    private val key: ByteArray =
        if (macAlgorithm == null) ByteArray(0)
        else Base64.getDecoder().decode(secret.removePrefix(SECRET_PREFIX))

    /**
     * Returns normally if any `v1` signature in [headers] signs [payload], and throws
     * [WebhookVerificationException] saying what failed otherwise.
     *
     * Header names are matched case-insensitively, and a header's first value is the one read. A
     * helper signed with an algorithm this class cannot check refuses every delivery.
     *
     * @param nowSeconds the current Unix time in seconds, which the delivery's timestamp is checked
     *   against.
     * @throws IllegalArgumentException if the secret decoded to an empty key.
     */
    fun verify(
        payload: String,
        headers: Map<String, List<String>>,
        nowSeconds: Long = System.currentTimeMillis() / 1000,
    ) {
        val jdkAlgorithm =
            macAlgorithm
                ?: throw WebhookVerificationException(
                    "Webhook signature algorithm $algorithm is not supported by this SDK, so the " +
                        "delivery cannot be verified; check its signature yourself" +
                        (parseUnsafeMethod?.let { ", then parse it with $it" }
                            ?: " before trusting it"),
                )
        val id = firstHeader(headers, idHeader)
        val signatures = firstHeader(headers, signatureHeader)
        val timestamp = firstHeader(headers, timestampHeader)
        if (id == null || signatures == null || timestamp == null) {
            throw WebhookVerificationException("Missing required headers")
        }

        val signedAt = checkTimestamp(timestamp, nowSeconds)
        val expected = sign(jdkAlgorithm, id, signedAt, payload).toByteArray(Charsets.UTF_8)

        // A sender rotating its secret signs with every live key, so the header is a
        // space-separated list of `<version>,<signature>` entries and any one `v1` match accepts
        // the delivery. Entries of another version, or without a comma, are skipped rather than
        // rejected.
        for (entry in signatures.split(' ')) {
            val parts = entry.split(',')
            if (parts.size < 2 || parts[0] != SIGNATURE_VERSION) {
                continue
            }
            // `MessageDigest.isEqual` takes the same time however many leading bytes match, so the
            // comparison does not tell an attacker how close a forged signature came.
            if (MessageDigest.isEqual(parts[1].toByteArray(Charsets.UTF_8), expected)) {
                return
            }
        }
        throw WebhookVerificationException(
            "None of the given webhook signatures match the expected signature",
        )
    }

    /**
     * Parses the timestamp header and rejects one outside the replay window.
     *
     * The window is [toleranceSeconds] either side of [nowSeconds], five minutes unless the helper
     * names another: a replayed delivery is refused once it is older than that, and one stamped
     * further ahead than ordinary clock skew is refused too, since accepting it would let a
     * captured delivery stay replayable for longer.
     */
    private fun checkTimestamp(header: String, nowSeconds: Long): Long {
        val timestamp =
            try {
                header.toLong()
            } catch (e: NumberFormatException) {
                throw WebhookVerificationException("Invalid signature headers", e)
            }
        if (timestamp < nowSeconds - toleranceSeconds) {
            throw WebhookVerificationException("Webhook timestamp too old")
        }
        if (timestamp > nowSeconds + toleranceSeconds) {
            throw WebhookVerificationException("Webhook timestamp too new")
        }
        return timestamp
    }

    /**
     * The base64 HMAC of `"$id.$timestamp.$payload"`, computed with the JDK algorithm
     * [jdkAlgorithm].
     *
     * The parsed [timestamp] is signed rather than the header's own text, so a sender's
     * `+1614265330` signs as `1614265330`.
     */
    private fun sign(jdkAlgorithm: String, id: String, timestamp: Long, payload: String): String {
        val mac =
            try {
                Mac.getInstance(jdkAlgorithm).apply { init(SecretKeySpec(key, jdkAlgorithm)) }
            } catch (e: GeneralSecurityException) {
                throw WebhookVerificationException("Failed to generate expected signature", e)
            }
        val content = "$id.$timestamp.$payload".toByteArray(Charsets.UTF_8)
        return Base64.getEncoder().encodeToString(mac.doFinal(content))
    }

    /** The first value of the first header named [name], ignoring case, that has one. */
    private fun firstHeader(headers: Map<String, List<String>>, name: String): String? {
        for ((header, values) in headers) {
            if (header.equals(name, ignoreCase = true) && values.isNotEmpty()) {
                return values[0]
            }
        }
        return null
    }

    private companion object {

        const val SECRET_PREFIX = "whsec_"
        const val ID_HEADER = "webhook-id"
        const val TIMESTAMP_HEADER = "webhook-timestamp"
        const val SIGNATURE_HEADER = "webhook-signature"
        const val SIGNATURE_VERSION = "v1"
        const val DEFAULT_ALGORITHM = "hmac-sha256"
        const val TOLERANCE_SECONDS = 5 * 60L

        // The algorithms an HMAC is checked with, by their config spelling. `ed25519` and
        // `rsa-sha256` are absent on purpose: the generator does not ship public-key verification.
        val JDK_MAC_ALGORITHMS: Map<String, String> =
            mapOf(
                "hmac-sha1" to "HmacSHA1",
                "hmac-sha256" to "HmacSHA256",
                "hmac-sha512" to "HmacSHA512",
            )
    }
}

/**
 * Why [WebhookVerifier] rejected a delivery.
 *
 * The generated webhook service never lets this escape: it becomes the cause of the SDK's own
 * webhook exception, so its message is what tells a caller which check failed.
 */
internal class WebhookVerificationException(message: String, cause: Throwable? = null) :
    Exception(message, cause)
