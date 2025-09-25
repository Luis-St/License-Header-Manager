package net.luis.lhm

import net.luis.lhm.tasks.AddLicenseHeadersTask
import net.luis.lhm.tasks.CheckLicenseHeadersTask
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Main plugin class for the License Header Manager.<br>
 * All tasks and extensions are registered here.<br>
 *
 * @author Luis-St
 */
class LicenseHeaderManager : Plugin<Project> {
	override fun apply(project: Project) {
		val extension = project.extensions.create("licenseHeaderManager", LicenseHeaderExtension::class.java)
		
		project.tasks.register("addLicenseHeaders", AddLicenseHeadersTask::class.java) { task ->
			task.group = "license"
			task.description = "Adds license headers to source files"
			task.extension = extension
		}
		
		project.tasks.register("checkLicenseHeaders", CheckLicenseHeadersTask::class.java) { task ->
			task.group = "license"
			task.description = "Checks if all source files have proper license headers"
			task.extension = extension
		}
	}
}
