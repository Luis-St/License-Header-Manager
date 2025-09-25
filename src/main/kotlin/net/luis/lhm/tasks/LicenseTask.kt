package net.luis.lhm.tasks

import net.luis.lhm.LicenseHeaderExtension
import net.luis.lhm.LineEnding
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import java.io.File
import java.util.regex.Pattern

/**
 * Abstract base class for license tasks.<br>
 * Provides common functionality for file matching and header processing.<br>
 *
 * @author Luis-St
 */
abstract class LicenseTask : DefaultTask() {
	
	@get:Internal
	lateinit var extension: LicenseHeaderExtension
	
	@get:InputFiles
	@get:PathSensitive(PathSensitivity.RELATIVE)
	val inputFiles: FileCollection
		get() = project.files(getMatchingFiles())
	
	@get:InputFile
	@get:PathSensitive(PathSensitivity.RELATIVE)
	val headerFile: File
		get() = File(project.rootDir, extension.headerFile)
	
	@get:Input
	val lineEnding: LineEnding
		get() = extension.lineEnding
	
	@get:Input
	val spacingAfterHeader: Int
		get() = extension.spacingAfterHeader
	
	@get:Input
	val variables: Map<String, String>
		get() = extension.variables.toMap()
	
	@get:Input
	val includes: List<String>
		get() = extension.includes.toList()
	
	@get:Input
	val excludes: List<String>
		get() = extension.excludes.toList()
	
	protected fun getMatchingFiles(): List<File> {
		val srcDir = File(project.projectDir, "src")
		if (!srcDir.exists()) {
			return emptyList()
		}
		
		return srcDir.walkTopDown()
			.filter { it.isFile }
			.filter { file ->
				val relativePath = file.relativeTo(project.projectDir).path.replace('\\', '/')
				
				val included = extension.includes.any { pattern ->
					matchesPattern(relativePath, pattern)
				}
				
				val excluded = extension.excludes.any { pattern ->
					matchesPattern(relativePath, pattern)
				}
				
				included && !excluded
			}
			.toList()
	}
	
	protected fun matchesPattern(path: String, pattern: String): Boolean {
		val regex = pattern
			.replace(".", "\\.")
			.replace("*", ".*")
			.replace("?", ".")
		return Pattern.matches(regex, path)
	}
	
	protected fun readAndProcessHeader(headerFile: File): String {
		var content = headerFile.readText()
		
		extension.variables.forEach { (key, value) ->
			content = content.replace("\${$key}", value)
			content = content.replace("{{$key}}", value)
		}
		
		return content
	}
	
	protected fun createBlockComment(content: String): String {
		val lines = content.split('\n')
		return buildString {
			appendLine("/*")
			lines.forEach { line ->
				appendLine(" * $line")
			}
			append(" */")
		}
	}
}
