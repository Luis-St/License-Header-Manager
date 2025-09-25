import java.util.Properties

plugins {
	kotlin("jvm") version "2.2.10"
	id("java-gradle-plugin")
	id("maven-publish")
}

var mavenUsername: String? = null
var mavenPassword: String? = null
val file = rootProject.file("./../credentials.properties")
if (file.exists()) {
	val properties = Properties()
	file.inputStream().use { properties.load(it) }
	
	mavenUsername = properties.getProperty("username")
	mavenPassword = properties.getProperty("password")
} else {
	throw GradleException("No credentials.properties file found.")
}

repositories {
	mavenCentral()
	gradlePluginPortal()
}

dependencies {
	implementation(gradleApi())
	implementation(localGroovy())
}

gradlePlugin {
	plugins {
		register("licenseHeaderManager") {
			id = "net.luis.lhm"
			implementationClass = "net.luis.lhm.LicenseHeaderManager"
			displayName = "License Header Manager Plugin"
			description = "A plugin to manage license headers in source files"
		}
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
			
			groupId = "net.luis"
			artifactId = "LHM"
			version = "1.0.0"
		}
	}
	repositories {
		if (mavenUsername != null && mavenPassword != null) {
			maven {
				url = uri("https://maven.luis-st.net/libraries/")
				credentials {
					username = mavenUsername
					password = mavenPassword
				}
			}
		} else {
			System.err.println("No credentials provided. Publishing to maven.luis-st.net not possible.")
		}
	}
}
