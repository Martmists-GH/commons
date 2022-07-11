import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    kotlin("jvm") version "1.6.21" apply false
    id("com.martmists.commons") apply false
    id("com.github.ben-manes.versions") version "0.42.0"
}

group = "com.martmists.commons"
version = "1.0.3"

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "com.martmists.commons")

    group = rootProject.group
    version = rootProject.version
    buildDir = file(rootProject.buildDir.absolutePath + "/" + project.name)
}

tasks {
    withType<DependencyUpdatesTask> {
        fun isStable(version: String): Boolean {
            val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
            val regex = "^[0-9,.v-]+(-r)?$".toRegex()
            return stableKeyword || regex.matches(version)
        }

        rejectVersionIf {
            !isStable(candidate.version) && isStable(currentVersion)
        }
    }
}
