package net.luis.lm.tasks

import net.luis.lm.LicenseExtension
import net.luis.lm.LineEnding
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
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
	lateinit var extension: LicenseExtension
	
	@get:InputFile
	@get:PathSensitive(PathSensitivity.RELATIVE)
	val header: File
		get() = File(this.project.rootDir, this.extension.header)
	
	@get:Input
	val lineEnding: LineEnding
		get() = this.extension.lineEnding
	
	@get:Input
	val spacingAfterHeader: Int
		get() = this.extension.spacingAfterHeader
	
	@get:Input
	val variables: Map<String, String>
		get() = this.extension.variables.toMap()
	
	@get:Input
	val sourceSets: List<String>
		get() = this.extension.sourceSets.toList()
	
	@get:Input
	val includes: List<String>
		get() = this.extension.includes.toList()
	
	@get:Input
	val excludes: List<String>
		get() = this.extension.excludes.toList()
	
	@Internal
	protected fun getMatchingFiles(): List<File> {
		return this.sourceSets
			.map { File(this.project.projectDir, "src/$it") }
			.filter { it.exists() }
			.flatMap { srcDir ->
				srcDir.walkTopDown()
					.filter { it.isFile }
					.filter { file ->
						val relativePath = file.relativeTo(project.projectDir).path.replace('\\', '/')
						
						val included = if (this.includes.isEmpty()) {
							true
						} else {
							this.includes.any { pattern ->
								this.matchesPattern(relativePath, pattern)
							}
						}
						
						val excluded = this.excludes.any { pattern ->
							this.matchesPattern(relativePath, pattern)
						}
						
						included && !excluded
					}
			}
			.toList()
	}
	
	private fun matchesPattern(path: String, pattern: String): Boolean {
		val regex = pattern.replace(".", "\\.").replace("*", ".*").replace("?", ".")
		return Pattern.matches(regex, path)
	}
	
	protected fun readAndProcessHeader(headerFile: File): String {
		var content = headerFile.readText()
		
		this.variables.forEach { (key, value) ->
			content = content.replace("\${$key}", value)
			content = content.replace("{{$key}}", value)
		}
		
		return content
	}
	
	protected fun createBlockComment(content: String): String {
		val lineEndingChar = when (this.lineEnding) {
			LineEnding.LF -> "\n"
			LineEnding.CRLF -> "\r\n"
		}
		
		val lines = content.trim().split(Regex("\\r?\\n"))
		return buildString {
			append("/*")
			append(lineEndingChar)
			lines.forEach { line ->
				if (line.isBlank()) {
					append(" *")
				} else {
					append(" * $line")
				}
				append(lineEndingChar)
			}
			append(" */")
			
			repeat(this@LicenseTask.spacingAfterHeader) {
				append(lineEndingChar)
			}
		}
	}
	
	protected fun hasValidHeader(file: File, headerComment: String): Boolean {
		val content = file.readText()
		
		val normalizedContent = content.replace(Regex("\\r?\\n"), "\n")
		val normalizedHeader = headerComment.replace(Regex("\\r?\\n"), "\n")
		
		return normalizedContent.startsWith(normalizedHeader)
	}
}
