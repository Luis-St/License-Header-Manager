package net.luis.lm

/**
 * Extension class for configuring the License Header Manager plugin.<br>
 * Holds configuration options such as the header file path, line endings, spacing, variables, and file inclusion/exclusion patterns.<br>
 *
 * @author Luis-St
 */
open class LicenseExtension {
	
	open var headerFile: String = "header.txt"
	open var lineEnding: LineEnding = LineEnding.LF
	open var spacingAfterHeader: Int = 1
	open var variables: MutableMap<String, String> = mutableMapOf()
	open val includes: MutableList<String> = mutableListOf()
	open val excludes: MutableList<String> = mutableListOf()
	
	open fun include(vararg patterns: String) {
		includes.addAll(patterns)
	}
	
	open fun exclude(vararg patterns: String) {
		excludes.addAll(patterns)
	}
	
	open fun variable(key: String, value: String) {
		variables[key] = value
	}
	
	open fun variable(key: String, value: Any) {
		variables[key] = value.toString()
	}
}
