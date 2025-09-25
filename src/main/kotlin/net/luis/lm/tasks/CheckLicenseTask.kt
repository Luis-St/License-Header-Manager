package net.luis.lm.tasks

import org.gradle.api.GradleException
import org.gradle.api.tasks.TaskAction
import java.io.File
import java.util.regex.Pattern

/**
 * Task to check for license headers in source files.<br>
 * The task reads the specified header file, processes it for variables, and checks if matching source files contain the correct header.<br>
 * If any files are missing the header or have an incorrect header, the task fails with a detailed error message.<br>
 *
 * @author Luis-St
 */
open class CheckLicenseTask : LicenseTask() {
	
	@TaskAction
	open fun checkHeaders() {
		if (!header.exists()) {
			throw GradleException("Header file not found: $header")
		}
		
		val headerPattern = createHeaderPattern(header)
		val filesToCheck = getMatchingFiles()
		val filesWithoutHeader = mutableListOf<File>()
		
		filesToCheck.forEach { file ->
			if (!hasValidHeader(file, headerPattern)) {
				filesWithoutHeader.add(file)
			}
		}
		
		if (filesWithoutHeader.isNotEmpty()) {
			val fileList = filesWithoutHeader.joinToString("\n") { "  - ${it.relativeTo(project.projectDir)}" }
			throw GradleException("The following files are missing license headers:\n$fileList")
		}
		
		println("License header check passed for ${filesToCheck.size} files")
	}
	
	private fun createHeaderPattern(headerFile: File): Pattern {
		var content = headerFile.readText()
		
		variables.forEach { (key, _) ->
			content = content.replace("\${$key}", ".*?")
			content = content.replace("{{$key}}", ".*?")
		}
		
		content = content
			.replace("\\", "\\\\")
			.replace(".", "\\.")
			.replace("^", "\\^")
			.replace("$", "\\$")
			.replace("|", "\\|")
			.replace("?", "\\?")
			.replace("*", "\\*")
			.replace("+", "\\+")
			.replace("(", "\\(")
			.replace(")", "\\)")
			.replace("[", "\\[")
			.replace("]", "\\]")
			.replace("{", "\\{")
			.replace("}", "\\}")
			.replace("\\.\\*\\?", ".*?")
		
		return Pattern.compile(content, Pattern.DOTALL)
	}
	
	private fun hasValidHeader(file: File, headerPattern: Pattern): Boolean {
		val content = file.readText()
		
		val blockCommentPattern = Pattern.compile("^\\s*/\\*(.*?)\\*/", Pattern.DOTALL)
		val matcher = blockCommentPattern.matcher(content)
		
		if (!matcher.find()) {
			return false
		}
		
		val extractedHeader = matcher.group(1)
			.split('\n')
			.joinToString("\n") { line ->
				line.replace(Regex("^\\s*\\*\\s?"), "").trim()
			}
			.trim()
		
		return headerPattern.matcher(extractedHeader).matches()
	}
}
