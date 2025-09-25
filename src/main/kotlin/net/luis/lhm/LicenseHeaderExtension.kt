package net.luis.lhm

/**
 * Extension class for configuring the License Header Manager plugin.<br>
 * Holds configuration options such as the header file path, line endings, spacing, variables, and file inclusion/exclusion patterns.<br>
 *
 * @author Luis-St
 */
open class LicenseHeaderExtension {
	var headerFile: String = "header.txt"
	var lineEnding: LineEnding = LineEnding.LF
	var spacingAfterHeader: Int = 1
	var variables: MutableMap<String, String> = mutableMapOf()
	val includes: MutableList<String> = mutableListOf("**/*.java", "**/*.kt")
	val excludes: MutableList<String> = mutableListOf()
	
	fun include(vararg patterns: String) {
		includes.addAll(patterns)
	}
	
	fun exclude(vararg patterns: String) {
		excludes.addAll(patterns)
	}
	
	fun variable(key: String, value: String) {
		variables[key] = value
	}
	
	fun variable(key: String, value: Any) {
		variables[key] = value.toString()
	}
}

enum class LineEnding(val value: String) {
	LF("\n"),
	CRLF("\r\n")
}
