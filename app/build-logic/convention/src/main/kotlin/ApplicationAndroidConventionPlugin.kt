import app.guad.convention.shared.libs
import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class ApplicationAndroidConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            with(pluginManager) {
                apply("com.android.application")
            }

            extensions.configure<ApplicationExtension> {
                namespace = "app.guad"
                compileSdk = libs.findVersion("projectCompileSdkVersion").get().requiredVersion.toInt()
                defaultConfig {
                    minSdk = libs.findVersion("projectMinSdkVersion").get().requiredVersion.toInt()
                    applicationId = libs.findVersion("projectApplicationId").get().requiredVersion
                    targetSdk = libs.findVersion("projectTargetSdkVersion").get().requiredVersion.toInt()
                    versionCode = libs.findVersion("projectVersionCode").get().toString().toInt()
                    versionName = libs.findVersion("projectVersionName").get().toString()
                }
                packaging {
                    resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                    }
                }
                buildTypes {
                    getByName("release") {
                        isMinifyEnabled = false
                    }
                }

                compileOptions {
                    sourceCompatibility = JavaVersion.VERSION_11
                    targetCompatibility = JavaVersion.VERSION_11
                }
            }
        }
    }
}