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
			throw GradleException("License header file not found: ${this.header}")
		}
		
		val headerComment = this.createBlockComment(this.readAndProcessHeader(this.header))
		val filesToProcess = this.getMatchingFiles()
		if (filesToProcess.isEmpty()) {
			println("No files found matching the specified patterns, skipping license header update.")
			return
		}
		
		var processedFiles = 0
		
		filesToProcess.forEach { file ->
			if (!this.hasValidHeader(file, headerComment)) {
				this.processFile(file, headerComment)
				processedFiles++;
			}
		}
		
		if (processedFiles == 0) {
			if (filesToProcess.size > 1) {
				println("${filesToProcess.size} files have been checked and all of them contain a valid license header.")
			} else {
				println("The file has been checked and it contains a valid license header.")
			}
		} else {
			println("License headers have been added to $processedFiles ${if (processedFiles > 1) "files" else "file"}")
		}
	}
	
	private fun processFile(file: File, headerComment: String) {
		val currentContent = file.readText()
		val newContent = this.insertOrReplaceHeader(currentContent, headerComment)
		
		file.writeText(newContent)
	}
	
	private fun insertOrReplaceHeader(content: String, headerComment: String): String {
		val existingHeaderPattern = Pattern.compile("^\\s*/\\*.*?\\*/\\s*", Pattern.DOTALL)
		val matcher = existingHeaderPattern.matcher(content)
		
		val contentWithoutHeader = if (matcher.find()) {
			content.substring(matcher.end())
		} else {
			content
		}
		
		return headerComment + contentWithoutHeader
	}
}
