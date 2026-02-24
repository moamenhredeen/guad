import app.guad.convention.shared.pathToPackageName
import com.codingfeline.buildkonfig.compiler.FieldSpec
import com.codingfeline.buildkonfig.gradle.BuildKonfigExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class BuildKonfigConventionPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        with(project) {
            with(pluginManager) {
                apply("com.codingfeline.buildkonfig")
            }

            extensions.configure<BuildKonfigExtension> {
                packageName = project.pathToPackageName()
                defaultConfigs {
                    buildConfigField(FieldSpec.Type.STRING, "API_KEY", "http://localhost:8080")
                }
            }
        }
    }

}