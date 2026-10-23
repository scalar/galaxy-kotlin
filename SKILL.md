---
name: scalar-galaxy-kotlin-sdk
description: "Kotlin SDK for Scalar Galaxy API. Use when writing Kotlin code that calls Scalar Galaxy API with the com.scalar.galaxy package: installing it, constructing and authenticating the client, and calling API operations."
---

# Scalar Galaxy Kotlin SDK

Generated Kotlin client for Scalar Galaxy API, published as `com.scalar.galaxy`. Use the generated client instead of hand-writing HTTP requests.

## Install

Add the dependency to your Gradle build:

```kotlin
implementation("com.scalar.galaxy:galaxy-kotlin:0.2.0") // x-release-please-version
```

Or declare it in your Maven `pom.xml`:

```xml
<dependency>
  <groupId>com.scalar.galaxy</groupId>
  <artifactId>galaxy-kotlin</artifactId>
  <version>0.2.0</version> <!-- x-release-please-version -->
</dependency>
```

Or build the SDK from source, with JDK 21 installed:

```sh
./gradlew :galaxy-kotlin:build
```

## Client setup and authentication

```kotlin
import com.scalar.galaxy.client.GalaxyClient
import com.scalar.galaxy.client.okhttp.GalaxyOkHttpClient

val client: GalaxyClient =
    GalaxyOkHttpClient.builder().bearerAuth(System.getenv("BEARER_AUTH")).build()
```

Provide credentials using the options below. Environment variables are read automatically when the target runtime supports them:

- `bearerAuth` (env: `BEARER_AUTH`) — JWT Bearer token authentication
- `basicAuthUsername` (env: `BASIC_AUTH_USERNAME`) — Basic HTTP authentication
- `basicAuthPassword` (env: `BASIC_AUTH_PASSWORD`) — Basic HTTP authentication
- `apiKeyHeader` (env: `API_KEY_HEADER`) — API key request header
- `apiKeyQuery` (env: `API_KEY_QUERY`) — API key query parameter
- `apiKeyCookie` (env: `API_KEY_COOKIE`) — API key browser cookie
- `oAuth2` (env: `SCALAR_O_AUTH2`) — OAuth 2.0 authentication
- `openIdConnect` (env: `SCALAR_OPEN_ID_CONNECT`) — OpenID Connect Authentication

## Calling operations

```kotlin
import com.scalar.galaxy.client.GalaxyClient
import com.scalar.galaxy.client.okhttp.GalaxyOkHttpClient
import com.scalar.galaxy.models.planets.PlanetListAllDataParams

val client: GalaxyClient =
    GalaxyOkHttpClient.builder().bearerAuth(System.getenv("BEARER_AUTH")).build()

val params = PlanetListAllDataParams.builder().limit(10L).offset(0L).build()
val planet = client.planets().listAllData(params)

println(planet)
```

Method names, parameter shapes, and response types are generated from the API description — do not guess them. Look up the exact call signature in [api.md](./api.md) before writing a call.

## Error handling

Non-success responses throw generated API errors. Error objects expose status, headers, response body, and request metadata where the target runtime supports it.

```kotlin
import com.scalar.galaxy.errors.GalaxyServiceException

try {
    val params = PlanetListAllDataParams.builder().limit(10L).offset(0L).build()
    val planet = client.planets().listAllData(params)
} catch (err: GalaxyServiceException) {
    println("${err.statusCode()} ${err.body()}")
    throw err
}
```

## Requirements

- Java 8 or later

## Reference files

- [README.md](./README.md) — full feature tour: client options, request options, retries and timeouts, logging.
- [api.md](./api.md) — complete catalogue of every operation with request and response types.
