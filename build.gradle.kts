import org.jetbrains.changelog.Changelog
import org.jetbrains.changelog.markdownToHTML

plugins {
  id("java")
  alias(libs.plugins.kotlin)
  alias(libs.plugins.intelliJPlatform)
  alias(libs.plugins.changelog)
}

group = providers.gradleProperty("pluginGroup").get()

version = providers.gradleProperty("pluginVersion").get()

kotlin {
  jvmToolchain(17)

  target {
    sourceSets.all {
      languageSettings {
        optIn("org.jetbrains.kotlin.analysis.api.permissions.KaAllowAnalysisFromWriteAction")
        optIn("org.jetbrains.kotlin.analysis.api.permissions.KaAllowAnalysisOnEdt")
        optIn(
          "org.jetbrains.kotlin.analysis.api.permissions.KaAllowProhibitedAnalyzeFromWriteAction"
        )
        enableLanguageFeature("ContextReceivers")
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
  intellijPlatform {
    create(providers.gradleProperty("platformType"), providers.gradleProperty("platformVersion"))

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

    // Extract the <!-- Plugin description --> section from README.md and provide for the plugin's
    // manifest
    description =
      providers.fileContents(layout.projectDirectory.file("README.md")).asText.map {
        val start = "<!-- Plugin description -->"
        val end = "<!-- Plugin description end -->"

        with(it.lines()) {
          if (!containsAll(listOf(start, end))) {
            throw GradleException(
              "Plugin description section not found in README.md:\n$start ... $end"
            )
          }
          subList(indexOf(start) + 1, indexOf(end)).joinToString("\n").let(::markdownToHTML)
        }
      }

    val changelog = project.changelog // local variable for configuration cache compatibility
    // Get the latest available change notes from the changelog file
    changeNotes =
      providers.gradleProperty("pluginVersion").map { pluginVersion ->
        with(changelog) {
          renderItem(
            (getOrNull(pluginVersion) ?: getUnreleased())
              .withHeader(false)
              .withEmptySections(false),
            Changelog.OutputType.HTML,
          )
        }
      }

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

changelog {
  groups.empty()
  repositoryUrl = providers.gradleProperty("pluginRepositoryUrl")
}

tasks {
  wrapper { gradleVersion = providers.gradleProperty("gradleVersion").get() }

  publishPlugin { dependsOn(patchChangelog) }
}

apply(from = "sync-files.gradle.kts")
