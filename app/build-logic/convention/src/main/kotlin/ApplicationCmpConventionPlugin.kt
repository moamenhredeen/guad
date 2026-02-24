import app.guad.convention.shared.configureAndroidTarget
import app.guad.convention.shared.configureIosTargets
import app.guad.convention.shared.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class ApplicationCmpConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            with(pluginManager) {
                apply("app.guad.convention.application.compose")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.compose")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            configureAndroidTarget()
            configureIosTargets()

            dependencies {
                "debugImplementation"(libs.findLibrary("androidx-compose-ui-tooling").get())
            }
        }
    }
}