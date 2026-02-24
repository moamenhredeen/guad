package app.guad.convention.shared

import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal fun Project.configureAndroidTarget() {
    project.configure<KotlinMultiplatformExtension> {
        androidTarget {
            compilerOptions {
                jvmTarget.set(JvmTarget.JVM_17)
            }
        }
    }
}