import app.guad.convention.shared.configureAndroidCompose
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class ApplicationComposeConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            with(pluginManager) {
                apply("app.guad.convention.application.android")
                apply("org.jetbrains.kotlin.plugin.compose")
            }

            val extension = extensions.getByType(ApplicationExtension::class)
            configureAndroidCompose(extension)
        }
    }
}