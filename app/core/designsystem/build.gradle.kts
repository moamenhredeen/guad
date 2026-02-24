plugins {
    alias(libs.plugins.conventionCmpLibrary)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(compose.components.resources)
            }
        }
    }
}