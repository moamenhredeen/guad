plugins {
    alias(libs.plugins.conventionCmpLibrary)
}

kotlin {
    sourceSets {

        androidMain {
            dependencies {
                implementation(compose.preview)
            }
        }

        commonMain {
            dependencies {
                implementation(compose.components.resources)
                implementation(compose.components.uiToolingPreview)
                implementation(projects.core.presentation)
            }
        }
    }
}