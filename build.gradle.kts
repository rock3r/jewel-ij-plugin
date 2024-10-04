import org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask

plugins {
    id("java")
    alias(libs.plugins.kotlin)
    alias(libs.plugins.intelliJPlatform)
    alias(libs.plugins.composeDesktop)
}

group = providers.gradleProperty("pluginGroup").get()

version = providers.gradleProperty("pluginVersion").get()

kotlin {
    jvmToolchain(17)

    target {
        sourceSets.all {
            languageSettings {
                optIn(
                    "org.jetbrains.kotlin.analysis.api.permissions.KaAllowAnalysisFromWriteAction"
                )
                optIn("org.jetbrains.kotlin.analysis.api.permissions.KaAllowAnalysisOnEdt")
                optIn("org.jetbrains.kotlin.analysis.api.permissions.KaAllowProhibitedAnalyzeFromWriteAction")
            }
        }
    }
}

repositories {
    mavenLocal()
    google()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    mavenCentral()

    intellijPlatform { defaultRepositories() }
}

dependencies {
    implementation(compose.desktop.currentOs) {
        exclude(group = "org.jetbrains.compose.material")
        exclude(group = "org.jetbrains.kotlinx")
    }

    intellijPlatform {
        create(
            providers.gradleProperty("platformType"),
            providers.gradleProperty("platformVersion"),
        )

        bundledPlugins(providers.gradleProperty("platformBundledPlugins").map { it.split(',') })

        plugins(providers.gradleProperty("platformPlugins").map { it.split(',') })

        instrumentationTools()
        pluginVerifier()
        zipSigner()
    }
}

intellijPlatform {
    pluginConfiguration {
        version = providers.gradleProperty("pluginVersion")

        ideaVersion {
            sinceBuild = providers.gradleProperty("pluginSinceBuild")
            untilBuild = providers.gradleProperty("pluginUntilBuild")
        }
    }

    buildSearchableOptions = true
    autoReload = false

    signing {
        certificateChain = providers.environmentVariable("CERTIFICATE_CHAIN")
        privateKey = providers.environmentVariable("PRIVATE_KEY")
        password = providers.environmentVariable("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")

        channels =
            providers.gradleProperty("pluginVersion").map {
                listOf(it.substringAfter('-', "").substringBefore('.').ifEmpty { "default" })
            }
    }

    pluginVerification { ides { recommended() } }
}

tasks { wrapper { gradleVersion = providers.gradleProperty("gradleVersion").get() } }

intellijPlatformTesting {
    runIde {
        register("runIdeForUiTests") {
            task {
                jvmArgumentProviders += CommandLineArgumentProvider {
                    listOf(
                        "-Drobot-server.port=8082",
                        "-Dide.mac.message.dialogs.as.sheets=false",
                        "-Djb.privacy.policy.text=<!--999.999-->",
                        "-Djb.consents.confirmation.enabled=false",
                    )
                }
            }

            plugins { robotServerPlugin() }
        }
    }
}
