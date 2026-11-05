# Scalar Galaxy Kotlin API

Complete reference of every operation, grouped by resource. See [the README](./README.md) for usage and configuration.

## Contents

- [`Planets`](#planets)
  - [Get all planets](#get-all-planets)
  - [Create a planet](#create-a-planet)
  - [Get a planet](#get-a-planet)
  - [Update a planet](#update-a-planet)
  - [Delete a planet](#delete-a-planet)
  - [Upload an image to a planet](#upload-an-image-to-a-planet)
- [`CelestialBodies`](#celestialbodies)
  - [Create a celestial body](#create-a-celestial-body)
- [`Authentication`](#authentication)
  - [Create a user](#create-a-user)
  - [Get a token](#get-a-token)
  - [Get authenticated user](#get-authenticated-user)

## Setup

```kotlin
import com.scalar.galaxy.client.GalaxyClient
import com.scalar.galaxy.client.okhttp.GalaxyOkHttpClient

val client: GalaxyClient =
    GalaxyOkHttpClient.builder().bearerAuth(System.getenv("BEARER_AUTH")).build()
```

## `Planets`

Everything about planets

### Get all planets

It's easy to say you know them all, but do you really? Retrieve all the planets and check whether you missed one.

| Direction | Type |
| --- | --- |
| Request | [`PlanetListParams`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/planets/PlanetListParams.kt) |
| Response | [`PlanetListResponse`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/planets/PlanetListResponse.kt) |

```kotlin
import com.scalar.galaxy.client.GalaxyClient
import com.scalar.galaxy.client.okhttp.GalaxyOkHttpClient
import com.scalar.galaxy.models.planets.PlanetListParams

val client: GalaxyClient =
    GalaxyOkHttpClient.builder().bearerAuth(System.getenv("BEARER_AUTH")).build()

val params = PlanetListParams.builder().limit(10L).offset(0L).build()
val planet = client.planets().list(params)

println(planet)
```

### Create a planet

Time to play god and create a new planet. What do you think? Ah, don't think too much. What could go wrong anyway?

| Direction | Type |
| --- | --- |
| Request | [`PlanetCreateParams`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/planets/PlanetCreateParams.kt) |
| Response | [`Planet`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/planets/Planet.kt) |

```kotlin
import com.scalar.galaxy.client.GalaxyClient
import com.scalar.galaxy.client.okhttp.GalaxyOkHttpClient
import com.scalar.galaxy.models.planets.Planet
import com.scalar.galaxy.models.planets.PlanetCreateParams

val client: GalaxyClient =
    GalaxyOkHttpClient.builder().bearerAuth(System.getenv("BEARER_AUTH")).build()

val params = PlanetCreateParams.builder().planet(Planet.builder().name("Mars").build()).build()
val planet = client.planets().create(params)

println(planet)
```

### Get a planet

You'll better learn a little bit more about the planets. It might come in handy once space travel is available for everyone.

| Direction | Type |
| --- | --- |
| Request | [`PlanetRetrieveParams`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/planets/PlanetRetrieveParams.kt) |
| Response | [`Planet`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/planets/Planet.kt) |

```kotlin
import com.scalar.galaxy.client.GalaxyClient
import com.scalar.galaxy.client.okhttp.GalaxyOkHttpClient
import com.scalar.galaxy.models.planets.PlanetRetrieveParams

val client: GalaxyClient =
    GalaxyOkHttpClient.builder().bearerAuth(System.getenv("BEARER_AUTH")).build()

val params = PlanetRetrieveParams.builder().planetId(1L).build()
val planet = client.planets().retrieve(params)

println(planet)
```

### Update a planet

Sometimes you make mistakes, that's fine. No worries, you can update all planets.

| Direction | Type |
| --- | --- |
| Request | [`PlanetUpdateParams`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/planets/PlanetUpdateParams.kt) |
| Response | [`Planet`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/planets/Planet.kt) |

```kotlin
import com.scalar.galaxy.client.GalaxyClient
import com.scalar.galaxy.client.okhttp.GalaxyOkHttpClient
import com.scalar.galaxy.models.planets.Planet
import com.scalar.galaxy.models.planets.PlanetUpdateParams

val client: GalaxyClient =
    GalaxyOkHttpClient.builder().bearerAuth(System.getenv("BEARER_AUTH")).build()

val params =
    PlanetUpdateParams.builder().planetId(1L).planet(Planet.builder().name("Mars").build()).build()
val planet = client.planets().update(params)

println(planet)
```

### Delete a planet

This endpoint was used to delete planets. Unfortunately, that caused a lot of trouble for planets with life. So, this endpoint is now deprecated and should not be used anymore.

| Direction | Type |
| --- | --- |
| Request | [`PlanetDeleteParams`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/planets/PlanetDeleteParams.kt) |

```kotlin
import com.scalar.galaxy.client.GalaxyClient
import com.scalar.galaxy.client.okhttp.GalaxyOkHttpClient
import com.scalar.galaxy.models.planets.PlanetDeleteParams

val client: GalaxyClient =
    GalaxyOkHttpClient.builder().bearerAuth(System.getenv("BEARER_AUTH")).build()

val params = PlanetDeleteParams.builder().planetId(1L).build()

client.planets().delete(params)
```

### Upload an image to a planet

Got a crazy good photo of a planet? Share it with the world!

| Direction | Type |
| --- | --- |
| Request | [`PlanetUploadImageParams`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/planets/PlanetUploadImageParams.kt) |
| Response | [`PlanetUploadImageResponse`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/planets/PlanetUploadImageResponse.kt) |

```kotlin
import com.scalar.galaxy.client.GalaxyClient
import com.scalar.galaxy.client.okhttp.GalaxyOkHttpClient
import com.scalar.galaxy.models.planets.PlanetUploadImageParams

val client: GalaxyClient =
    GalaxyOkHttpClient.builder().bearerAuth(System.getenv("BEARER_AUTH")).build()

val params = PlanetUploadImageParams.builder().planetId(1L).build()
val planet = client.planets().uploadImage(params)

println(planet)
```

## `CelestialBodies`

Celestial bodies are the planets and satellites in the Scalar Galaxy.

### Create a celestial body

Stars, moons, comets, the occasional rogue asteroid — if it glows or drifts through the void, you can add it here.

| Direction | Type |
| --- | --- |
| Request | [`CelestialBodyCreateParams`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/celestialBodies/CelestialBodyCreateParams.kt) |
| Response | [`CelestialBody`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/celestialBodies/CelestialBody.kt) |

```kotlin
import com.scalar.galaxy.client.GalaxyClient
import com.scalar.galaxy.client.okhttp.GalaxyOkHttpClient
import com.scalar.galaxy.models.celestialBodies.CelestialBody
import com.scalar.galaxy.models.celestialBodies.CelestialBodyCreateParams
import com.scalar.galaxy.models.planets.Planet

val client: GalaxyClient =
    GalaxyOkHttpClient.builder().bearerAuth(System.getenv("BEARER_AUTH")).build()

val params =
    CelestialBodyCreateParams.builder()
        .celestialBody(CelestialBody.ofPlanet(Planet.builder().name("Mars").build()))
        .build()
val celestialBody = client.celestialBodies().create(params)

println(celestialBody)
```

## `Authentication`

Some endpoints are public, but some require authentication. We provide all the required endpoints to create an account and authorize yourself.

### Create a user

Time to create a user account, eh?

| Direction | Type |
| --- | --- |
| Request | [`AuthenticationCreateUserParams`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/authentication/AuthenticationCreateUserParams.kt) |
| Response | [`User`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/authentication/User.kt) |

```kotlin
import com.scalar.galaxy.client.GalaxyClient
import com.scalar.galaxy.client.okhttp.GalaxyOkHttpClient
import com.scalar.galaxy.models.authentication.AuthenticationCreateUserParams

val client: GalaxyClient =
    GalaxyOkHttpClient.builder().bearerAuth(System.getenv("BEARER_AUTH")).build()

val params =
    AuthenticationCreateUserParams.builder()
        .name("Marc")
        .email("marc@scalar.com")
        .password("i-love-scalar")
        .build()
val authentication = client.authentication().createUser(params)

println(authentication)
```

### Get a token

Yeah, this is the boring security stuff. Just get your super secret token and move on.

| Direction | Type |
| --- | --- |
| Request | [`AuthenticationCreateTokenParams`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/authentication/AuthenticationCreateTokenParams.kt) |
| Response | [`AuthenticationCreateTokenResponse`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/authentication/AuthenticationCreateTokenResponse.kt) |

```kotlin
import com.scalar.galaxy.client.GalaxyClient
import com.scalar.galaxy.client.okhttp.GalaxyOkHttpClient
import com.scalar.galaxy.models.authentication.AuthenticationCreateTokenParams
import com.scalar.galaxy.models.authentication.Credentials

val client: GalaxyClient =
    GalaxyOkHttpClient.builder().bearerAuth(System.getenv("BEARER_AUTH")).build()

val params =
    AuthenticationCreateTokenParams.builder()
        .credentials(
            Credentials.builder().email("marc@scalar.com").password("i-love-scalar").build()
        )
        .build()
val authentication = client.authentication().createToken(params)

println(authentication)
```

### Get authenticated user

Find yourself they say. That's what you can do here.

| Direction | Type |
| --- | --- |
| Request | [`AuthenticationListMeParams`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/authentication/AuthenticationListMeParams.kt) |
| Response | [`User`](./galaxy-kotlin-core/src/main/kotlin/com/scalar/galaxy/models/authentication/User.kt) |

```kotlin
import com.scalar.galaxy.client.GalaxyClient
import com.scalar.galaxy.client.okhttp.GalaxyOkHttpClient
import com.scalar.galaxy.models.authentication.AuthenticationListMeParams

val client: GalaxyClient =
    GalaxyOkHttpClient.builder().bearerAuth(System.getenv("BEARER_AUTH")).build()

val authentication = client.authentication().listMe(AuthenticationListMeParams.none())

println(authentication)
```
