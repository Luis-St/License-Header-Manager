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
		if (!this.header.exists()) {
			throw GradleException("Header file not found: ${this.header}")
		}
		
		val headerComment = this.createBlockComment(this.readAndProcessHeader(this.header))
		val filesToCheck = this.getMatchingFiles()
		val filesWithoutHeader = mutableListOf<File>()
		
		filesToCheck.forEach { file ->
			if (!this.hasValidHeader(file, headerComment)) {
				filesWithoutHeader.add(file)
			}
		}
		
		if (filesWithoutHeader.isNotEmpty()) {
			val fileList = filesWithoutHeader.joinToString("\n") { "  - ${it.relativeTo(project.projectDir)}" }
			throw GradleException("The following files are missing license headers:\n$fileList")
		}
		
		println("License header check passed for ${filesToCheck.size} files")
	}
}
