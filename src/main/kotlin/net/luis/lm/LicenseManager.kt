package net.luis.lm

import net.luis.lm.tasks.UpdateLicenseTask
import net.luis.lm.tasks.CheckLicenseTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Main plugin class for the License Header Manager.<br>
 * All tasks and extensions are registered here.<br>
 *
 * @author Luis-St
 */
class LicenseManager : Plugin<Project> {
	
	override fun apply(project: Project) {
		val extension = project.extensions.create("licenseManager", LicenseExtension::class.java)
		
		project.tasks.register("updateLicenses", UpdateLicenseTask::class.java) { task ->
			task.group = "license"
			task.description = "Adds license headers to source files"
			task.extension = extension
		}
		
		project.tasks.register("checkLicenses", CheckLicenseTask::class.java) { task ->
			task.group = "license"
			task.description = "Checks if all source files have proper license headers"
			task.extension = extension
		}
	}
}
