plugins {
    alias(libs.plugins.conventionCmpFeature)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(projects.feature.gtd.domain)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}