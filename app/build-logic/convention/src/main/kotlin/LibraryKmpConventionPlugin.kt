import app.guad.convention.shared.libs
import app.guad.convention.shared.pathToFrameworkName
import app.guad.convention.shared.pathToPackageName
import app.guad.convention.shared.pathToResourcePrefix
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.androidLibrary
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class LibraryKmpConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.multiplatform")
                apply("org.jetbrains.kotlin.plugin.serialization")
            }

            extensions.configure<LibraryExtension> {
                resourcePrefix = project.pathToResourcePrefix()
                // Required to make debug build of app run in iOS simulator
            }

            extensions.configure<KotlinMultiplatformExtension> {

                androidLibrary {
                    namespace = project.pathToPackageName()
                    compileSdk = libs.findVersion("projectCompileSdkVersion").get().requiredVersion.toInt()
                    minSdk = libs.findVersion("projectMinSdkVersion").get().requiredVersion.toInt()
                    experimentalProperties["android.experimental.kmp.enableAndroidResources"] = "true"
                    compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_17)
                    }
                }

                jvm("desktop") {
                    compilations.all {
                        compileTaskProvider.configure {
                            compilerOptions {
                                jvmTarget.set(JvmTarget.JVM_17)
                            }
                        }
                    }
                }

                val xcfName = project.pathToFrameworkName()

                iosX64 {
                    binaries.framework {
                        baseName = xcfName
                    }
                }

                iosArm64 {
                    binaries.framework {
                        baseName = xcfName
                    }
                }

                iosSimulatorArm64 {
                    binaries.framework {
                        baseName = xcfName
                    }
                }

                compilerOptions {
                    freeCompilerArgs.add("-Xexpect-actual-classes")
                    freeCompilerArgs.add("-opt-in=kotlin.RequiresOptIn")
                    freeCompilerArgs.add("-opt-in=kotlin.time.ExperimentalTime")
                }

            }

            tasks.withType<KotlinCompile>().configureEach {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_17)
                    freeCompilerArgs.add(
                        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
                    )
                }
            }


            dependencies {
                "commonMainImplementation"(libs.findLibrary("kotlinx-serialization-json").get())
                "commonTestImplementation"(libs.findLibrary("kotlin-test").get())
            }
        }

    }
}