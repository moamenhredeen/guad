import app.guad.convention.shared.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class LibraryCmpConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("app.guad.convention.library.kmp")
                apply("org.jetbrains.kotlin.plugin.compose")
                apply("org.jetbrains.compose")
            }


            dependencies {
                "commonMainImplementation"(libs.findLibrary("compose-ui").get())
                "commonMainImplementation"(libs.findLibrary("compose-foundation").get())
                "commonMainImplementation"(libs.findLibrary("compose-material3").get())
                "commonMainImplementation"(libs.findLibrary("compose-material-icons-core").get())
                "commonMainImplementation"(libs.findLibrary("compose-components-resources").get())
                "commonMainImplementation"(libs.findLibrary("compose-uiToolingPreview").get())
                "androidRuntimeClasspath"(libs.findLibrary("compose-uiTooling").get())
            }
        }
    }
}