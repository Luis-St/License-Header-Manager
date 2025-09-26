package net.luis.lm.tasks

import net.luis.lm.LineEnding
import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.regex.Pattern

/**
 * Task to add license headers to source files.<br>
 * The task reads the specified header file, processes it for variables, and adds or replaces the header in matching source files.<br>
 * The task respects the configured line endings and spacing after the header.<br>
 *
 * @author Luis-St
 */
open class UpdateLicenseTask : LicenseTask() {
	
	@TaskAction
	open fun addHeaders() {
		if (!this.header.exists()) {
			throw GradleException("Header file not found: ${this.header}")
		}
		
		val headerComment = this.createBlockComment(this.readAndProcessHeader(this.header))
		val filesToProcess = this.getMatchingFiles()
		var processedFiles = 0
		
		filesToProcess.forEach { file ->
			if (this.hasValidHeader(file, headerComment)) {
				this.processFile(file, headerComment)
				processedFiles++;
			}
		}
		
		println("License headers added to $processedFiles files")
	}
	
	private fun processFile(file: File, headerComment: String) {
		val currentContent = file.readText()
		val newContent = this.insertOrReplaceHeader(currentContent, headerComment)
		
		val finalContent = when (this.lineEnding) {
			LineEnding.CRLF -> newContent.replace("\n", "\r\n")
			LineEnding.LF -> newContent.replace("\r\n", "\n")
		}
		
		file.writeText(finalContent)
	}
	
	private fun insertOrReplaceHeader(content: String, headerComment: String): String {
		val existingHeaderPattern = Pattern.compile("^\\s*/\\*.*?\\*/\\s*", Pattern.DOTALL)
		val matcher = existingHeaderPattern.matcher(content)
		
		val contentWithoutHeader = if (matcher.find()) {
			content.substring(matcher.end())
		} else {
			content
		}
		
		val spacing = "\n".repeat(0.coerceAtLeast(this.spacingAfterHeader) + 1)
		return headerComment + spacing + contentWithoutHeader
	}
}
