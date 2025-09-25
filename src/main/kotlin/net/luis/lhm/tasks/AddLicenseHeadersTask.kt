package net.luis.lhm.tasks

import net.luis.lhm.LineEnding
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
class AddLicenseHeadersTask : LicenseTask() {
	
	@TaskAction
	fun addHeaders() {
		if (!headerFile.exists()) {
			throw GradleException("Header file not found: $headerFile")
		}
		
		val headerContent = readAndProcessHeader(headerFile)
		val filesToProcess = getMatchingFiles()
		
		filesToProcess.forEach { file ->
			processFile(file, headerContent)
		}
		
		println("License headers added to ${filesToProcess.size} files")
	}
	
	private fun processFile(file: File, headerContent: String) {
		val currentContent = file.readText()
		val commentedHeader = createBlockComment(headerContent)
		val newContent = insertOrReplaceHeader(currentContent, commentedHeader)
		
		val finalContent = when (lineEnding) {
			LineEnding.CRLF -> newContent.replace("\n", "\r\n")
			LineEnding.LF -> newContent.replace("\r\n", "\n")
		}
		
		file.writeText(finalContent)
	}
	
	private fun insertOrReplaceHeader(content: String, header: String): String {
		val existingHeaderPattern = Pattern.compile("^\\s*/\\*.*?\\*/\\s*", Pattern.DOTALL)
		val matcher = existingHeaderPattern.matcher(content)
		
		val contentWithoutHeader = if (matcher.find()) {
			content.substring(matcher.end())
		} else {
			content
		}
		
		val spacing = "\n".repeat(spacingAfterHeader)
		return header + spacing + contentWithoutHeader
	}
}
