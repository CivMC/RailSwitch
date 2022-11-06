//import net.civmc.civgradle.common.util.civRepo

plugins {
    `java-library`
    `maven-publish`
    id("net.civmc.civgradle") version "2.0.0-SNAPSHOT"
}

// Temporary hack:
// Remove the root build directory
gradle.buildFinished {
	project.buildDir.deleteRecursively()
}

allprojects {
	group = "net.civmc.railswitch"
	version = "2.0.0-SNAPSHOT"
	description = "RailSwitch"
}

subprojects {
	apply(plugin = "net.civmc.civgradle")
	apply(plugin = "java-library")
	apply(plugin = "maven-publish")

	java {
		toolchain {
			languageVersion.set(JavaLanguageVersion.of(17))
		}
	}

	repositories {
		mavenCentral()
		val civ_repos = arrayOf(
			"CivMC/CivModCore",
			"CivMC/NameLayer",
			"CivMC/Citadel",
		)
		for (repo in civ_repos) {
			maven {
				url = uri("https://maven.pkg.github.com/$repo")
				credentials {
					username = System.getenv("GITHUB_ACTOR")
					password = System.getenv("GITHUB_TOKEN")
				}
			}
		}
        /*civRepo("CivMC/CivModCore")
        civRepo("CivMC/NameLayer")
        civRepo("CivMC/Citadel")*/
	}

	publishing {
		repositories {
			maven {
				name = "GitHubPackages"
				url = uri("https://maven.pkg.github.com/CivMC/RailSwitch")
				credentials {
					username = System.getenv("GITHUB_ACTOR")
					password = System.getenv("GITHUB_TOKEN")
				}
			}
		}
		publications {
			register<MavenPublication>("gpr") {
				from(components["java"])
			}
		}
	}
}
