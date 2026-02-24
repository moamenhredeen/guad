rootProject.name = "guad"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

includeBuild("build-logic")

// entry point
include(":composeApp")

// core
include(":core:presentation")
include(":core:domain")
include(":core:data")
include(":core:designsystem")

// auth feature
include(":feature:auth:presentation")
include(":feature:auth:domain")
include(":feature:auth:data")

// gtd feature
include(":feature:gtd:presentation")
include(":feature:gtd:domain")
include(":feature:gtd:data")
include(":feature:gtd:database")
