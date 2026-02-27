plugins {
    alias(libs.plugins.conventionCmpLibrary)
}


kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.material3.adaptive)
            }
        }
    }
}
