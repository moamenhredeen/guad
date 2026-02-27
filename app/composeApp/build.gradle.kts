plugins {
    alias(libs.plugins.conventionCmpLibrary)
    alias(libs.plugins.composeHotReload)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(projects.core.data)
            implementation(projects.core.domain)
            implementation(projects.core.designsystem)
            implementation(projects.core.presentation)

            implementation(projects.feature.auth.domain)
            implementation(projects.feature.auth.presentation)

            implementation(projects.feature.gtd.data)
            implementation(projects.feature.gtd.database)
            implementation(projects.feature.gtd.domain)
            implementation(projects.feature.gtd.presentation)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.jetbrains.compose.viewmodel)
            implementation(libs.jetbrains.lifecycle.compose)

            implementation(libs.kotlin.test)
        }
    }
}
