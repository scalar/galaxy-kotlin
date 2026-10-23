rootProject.name = "galaxy-kotlin-root"

val projectNames =
    rootDir
        .listFiles()
        ?.asSequence()
        .orEmpty()
        .filter { file ->
            file.isDirectory &&
                file.name != "buildSrc" &&
                file.name.startsWith("galaxy") &&
                file.listFiles()?.asSequence().orEmpty().any { it.name == "build.gradle.kts" }
        }
        .map { it.name }
        .toList()

println("projects: $projectNames")

projectNames.forEach { include(it) }
