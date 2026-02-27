import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    `kotlin-dsl`
}

group = "app.guad.buildlogic.convention"

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    implementation(libs.kotlin.serialization.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.ksp.gradlePlugin)
    compileOnly(libs.androidx.room.gradle.plugin)
    compileOnly(libs.jetbrains.compose.gradlePlugin)
    implementation(libs.buildkonfig.gradlePlugin)
    implementation(libs.buildkonfig.compiler)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

tasks {
    validatePlugins {
        enableStricterValidation = true
        failOnWarning = true
    }
}

gradlePlugin {
    plugins {
        register("kmpLibrary") {
            id = "app.guad.convention.library.kmp"
            implementationClass = "LibraryKmpConventionPlugin"
        }
        register("cmpLibrary") {
            id = "app.guad.convention.library.cmp"
            implementationClass = "LibraryCmpConventionPlugin"
        }
        register("featurePresentationLibrary") {
            id = "app.guad.convention.library.feature-presentation"
            implementationClass = "LibraryFeaturePresentationConventionPlugin"
        }

        register("buildkonfig") {
            id = "app.guad.convention.buildkonfig"
            implementationClass = "BuildKonfigConventionPlugin"
        }
        register("room") {
            id = "app.guad.convention.room"
            implementationClass = "RoomConventionPlugin"
        }
    }
}