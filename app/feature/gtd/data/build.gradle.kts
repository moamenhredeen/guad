plugins {
    alias(libs.plugins.conventionKmpLibrary)
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.kotlin.stdlib)
                implementation(projects.core.data)
                implementation(projects.feature.gtd.domain)
                implementation(projects.feature.gtd.database)
            }
        }
    }
}