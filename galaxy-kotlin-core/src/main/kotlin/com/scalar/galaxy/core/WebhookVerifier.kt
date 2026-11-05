package com.scalar.galaxy.core

import java.security.GeneralSecurityException
import java.security.MessageDigest
import java.util.Base64
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * Verifies a Standard Webhooks signature with the JDK alone.
 *
 * A delivery carries `webhook-id`, `webhook-timestamp` and `webhook-signature` headers, and the
 * signature is a base64 HMAC-SHA256 over `"$id.$timestamp.$payload"`, keyed with the signing secret
 * (https://github.com/standard-webhooks/standard-webhooks).
 *
 * A secret that is not valid base64, or decodes to no bytes, is a misconfiguration rather than a
 * failed delivery, and throws `IllegalArgumentException`: a non-base64 secret from the constructor,
 * and an empty key from [verify] once a delivery's headers and timestamp have passed their checks.
 *
 * @param secret the signing secret, optionally prefixed with `whsec_`.
 * @throws IllegalArgumentException if the secret is not valid base64.
 */
internal class WebhookVerifier(secret: String) {

    // A sender prints the secret as `whsec_` followed by the base64 signing key, and the key is the
    // decoded bytes, not the text. A secret that is not base64 throws `IllegalArgumentException`
    // here rather than a `WebhookVerificationException`: it is a misconfigured client, not a forged
    // delivery.
    private val key: ByteArray = Base64.getDecoder().decode(secret.removePrefix(SECRET_PREFIX))

    /**
     * Returns normally if any `v1` signature in [headers] signs [payload], and throws
     * [WebhookVerificationException] saying what failed otherwise.
     *
     * Header names are matched case-insensitively, and a header's first value is the one read.
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
        val id = firstHeader(headers, ID_HEADER)
        val signatures = firstHeader(headers, SIGNATURE_HEADER)
        val timestampHeader = firstHeader(headers, TIMESTAMP_HEADER)
        if (id == null || signatures == null || timestampHeader == null) {
            throw WebhookVerificationException("Missing required headers")
        }

        val timestamp = checkTimestamp(timestampHeader, nowSeconds)
        val expected = sign(id, timestamp, payload).toByteArray(Charsets.UTF_8)

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
            "None of the given webhook signatures match the expected signature"
        )
    }

    /**
     * Parses the timestamp header and rejects one outside the replay window.
     *
     * The window is five minutes either side of [nowSeconds]: a replayed delivery is refused once
     * it is older than that, and one stamped further ahead than ordinary clock skew is refused too,
     * since accepting it would let a captured delivery stay replayable for longer.
     */
    private fun checkTimestamp(header: String, nowSeconds: Long): Long {
        val timestamp =
            try {
                header.toLong()
            } catch (e: NumberFormatException) {
                throw WebhookVerificationException("Invalid signature headers", e)
            }
        if (timestamp < nowSeconds - TOLERANCE_SECONDS) {
            throw WebhookVerificationException("Webhook timestamp too old")
        }
        if (timestamp > nowSeconds + TOLERANCE_SECONDS) {
            throw WebhookVerificationException("Webhook timestamp too new")
        }
        return timestamp
    }

    /**
     * The base64 HMAC-SHA256 of `"$id.$timestamp.$payload"`.
     *
     * The parsed [timestamp] is signed rather than the header's own text, so a sender's
     * `+1614265330` signs as `1614265330`.
     */
    private fun sign(id: String, timestamp: Long, payload: String): String {
        val mac =
            try {
                Mac.getInstance(HMAC_SHA256).apply { init(SecretKeySpec(key, HMAC_SHA256)) }
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
        const val HMAC_SHA256 = "HmacSHA256"
        const val TOLERANCE_SECONDS = 5 * 60L
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
