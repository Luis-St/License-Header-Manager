package net.luis.lm

/**
 * Extension class for configuring the License Header Manager plugin.<br>
 * Holds configuration options such as the header file path, line endings, spacing, variables, and file inclusion/exclusion patterns.<br>
 *
 * @author Luis-St
 */
open class LicenseExtension {
	
	open var header: String = "header.txt"
	open var lineEnding: LineEnding = LineEnding.LF
	open var spacingAfterHeader: Int = 1
	open var variables: MutableMap<String, String> = mutableMapOf()
	open var sourceSets: List<String> = listOf("main")
	open val includes: MutableList<String> = mutableListOf()
	open val excludes: MutableList<String> = mutableListOf()
	
	open fun include(vararg patterns: String) {
		this.includes.addAll(patterns)
	}
	
	open fun exclude(vararg patterns: String) {
		this.excludes.addAll(patterns)
	}
	
	open fun variable(key: String, value: String) {
		this.variables[key] = value
	}
	
	open fun variable(key: String, value: Any) {
		this.variables[key] = value.toString()
	}
}
