package com.martmists.commons

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.withType

open class ModuleConfigExtension {
    var isRelease: Boolean = System.getenv("DEPLOY_TYPE") == "snapshot"
    var username: String = "github-actions"
    var token: String = System.getenv("DEPLOY_KEY") ?: "NO_TOKEN"
    var host: String = System.getenv("GITHUB_TARGET_REPO") ?: "https://maven.martmists.com/releases"
    var version: String? = null

    private fun Project.getPublishVersion(): String {
        return if (!isRelease) {
            this@ModuleConfigExtension.version ?: System.getenv("GITHUB_SHA") ?: this.version as String
        } else {
            this@ModuleConfigExtension.version ?: this.version as String
        }
    }

    fun Project.setupJVM() {
        setupCommon()

        configure<PublishingExtension> {
            publications {
                create<MavenPublication>("jvm") {
                    groupId = project.group as String
                    artifactId = project.name
                    version = getPublishVersion()

                    from(components.getByName("java"))
                }
            }
        }
    }

    fun Project.setupMPP() {
        setupCommon()
    }

    private fun Project.setupCommon() {
        configure<PublishingExtension> {
            repositories {
                maven {
                    name = "Host"
                    url = uri(host)
                    credentials {
                        username = this@ModuleConfigExtension.username
                        password = this@ModuleConfigExtension.token
                    }
                }
            }

            publications {
                withType<MavenPublication>() {
                    version = getPublishVersion()
                }
            }
        }
    }
}
