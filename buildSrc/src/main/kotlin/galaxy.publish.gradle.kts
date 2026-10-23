import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm

plugins {
    id("com.vanniktech.maven.publish")
    id("org.jetbrains.dokka")
}

dokka {
    dokkaGeneratorIsolation.set(
        ProcessIsolation {
            maxHeapSize.set("8g")
        }
    )
}

mavenPublishing {
    configure(
        KotlinJvm(
            javadocJar = JavadocJar.Dokka("dokkaGeneratePublicationHtml"),
            sourcesJar = true,
        )
    )
    publishToMavenCentral(automaticRelease = true)
    signAllPublications()
    coordinates(project.group.toString(), project.name, project.version.toString())
    pom {
        name.set("Scalar Galaxy SDK")
        description.set(
            "The Scalar Galaxy is an example OpenAPI document to test OpenAPI tools and libraries. It's a fictional universe with fictional planets and fictional data."
        )
        url.set("https://github.com/scalar/galaxy-kotlin")
        licenses {
            license {
                name.set("Apache-2.0")
                url.set("https://github.com/scalar/galaxy-kotlin/blob/HEAD/LICENSE")
            }
        }
        developers {
            developer {
                id.set("scalarGalaxy")
                name.set("Scalar Galaxy")
                url.set("https://github.com/scalar/galaxy-kotlin")
            }
        }
        scm {
            url.set("https://github.com/scalar/galaxy-kotlin")
            connection.set("scm:git:https://github.com/scalar/galaxy-kotlin.git")
            developerConnection.set("scm:git:ssh://git@github.com/scalar/galaxy-kotlin.git")
        }
    }
}

tasks.withType<Zip>().configureEach {
    isZip64 = true
}

tasks.named<org.gradle.jvm.tasks.Jar>("dokkaJavadocJar") {
    exclude("dokka-configuration.json")
}
