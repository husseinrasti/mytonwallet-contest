pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MyTonWalletContest"
include(":app")
include(":core:dagger-hilt")
include(":core:datastore")
include(":core:security")
include(":component:ui")
include(":component:theme")
include(":component:navigation")

include(":feature:create-wallet:ui")
include(":feature:create-wallet:data")
include(":feature:create-wallet:domain")
include(":feature:auth:ui")
include(":feature:auth:data")
include(":feature:auth:domain")
include(":feature:wallet:data")
include(":feature:wallet:domain")
include(":feature:wallet:ui")

fun renameBuildFileName(name: String, project: ProjectDescriptor) {
    if (project.children.isEmpty()) {
        println("$name.gradle.kts")
        project.buildFileName = "$name.gradle.kts"
    } else {
        project.children.forEach { subProject ->
            renameBuildFileName("$name-${subProject.name}", subProject)
        }
    }
}

rootProject.children.forEach { project ->
    renameBuildFileName(project.name, project)
}

