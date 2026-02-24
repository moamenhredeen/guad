plugins {
    alias(libs.plugins.conventionKmpLibrary)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(projects.core.domain)
            }
        }
    }
}