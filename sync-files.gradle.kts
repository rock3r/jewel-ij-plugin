import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.*
import java.io.File

abstract class VerifyAndCopyTask : DefaultTask() {

  @get:InputFile abstract val csvFile: RegularFileProperty

  @get:InputDirectory abstract val sourceDir: DirectoryProperty

  @TaskAction
  fun verifyAndCopy() {
    var copied = 0
    var skipped = 0
    var missing = 0
    var mismatched = 0

    val csv = csvFile.get().asFile
    val sourcePathBase = sourceDir.get().asFile
    var lineNumber = 1
    csv.forEachLine { line ->
      val parts = line.split(",", limit = 3)
      val destPath = parts[0]
      val sourcePath = parts[1]
      val notes = if (parts.size == 3) parts[2] else null

      // Construct the destination path relative to the project root
      val destFile = project.file(destPath)

      // Construct the source file path using the provided sourceDir
      val sourceFile = File(sourcePathBase, sourcePath)

      when {
        sourceFile.name != destFile.name && notes?.contains("DifferentName") == false -> {
          logger.warn("Warning [$lineNumber]: source file name ${sourceFile.name} is different from ${destFile.name}")
          mismatched++
        }
        !destFile.exists() -> {
          logger.warn("Warning [$lineNumber]: Destination file not found: $destPath (source: $sourcePath)")
          missing++
        }
        !sourceFile.exists() -> {
          logger.warn("Warning [$lineNumber]: Source file not found: ${sourceFile.absolutePath} (destination: $destPath)")
          missing++
        }
        destFile.readText() != sourceFile.readText() -> {
          sourceFile.copyTo(destFile, overwrite = true)
          logger.lifecycle("Synced: $destPath")
          copied++
        }
        else -> {
          logger.lifecycle("Skipped (no changes): $destPath")
          skipped++
        }
      }

      lineNumber++
    }

    logger.lifecycle("")
    logger.lifecycle("Report:")
    logger.lifecycle("  Copied: $copied")
    logger.lifecycle("  Skipped: $skipped")
    logger.lifecycle("  Missing: $missing")
    logger.lifecycle("  Mismatched: $mismatched")

    if (missing > 0) {
      throw GradleException("Task failed with $missing missing files.")
    }

    logger.lifecycle("")
    logger.lifecycle("Please review changes thoroughly before committing them.")
  }
}

tasks.register<VerifyAndCopyTask>("verifyAndCopy") {
  csvFile.set(project.layout.file(project.provider { project.file("files-mapping.csv") }))
  sourceDir.set(project.objects.directoryProperty().value(
    project.provider { project.layout.projectDirectory.dir(sourceDirProperty) }
  ))
}

val sourceDirProperty: String
  get() = project.findProperty("sourceDir") as? String ?: ""
