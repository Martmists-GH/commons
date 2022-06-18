package com.martmists.commons

import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.create

open class ModuleConfigExtension {
    var isRelease: Boolean = System.getenv("DEPLOY_TYPE") == "snapshot"
    var username: String = "github-actions"
    var token: String = System.getenv("DEPLOY_KEY") ?: "NO_TOKEN"
    var host: String = System.getenv("GITHUB_TARGET_REPO") ?: "https://maven.martmists.com/releases"
    var version: String? = null

    fun Project.setupJVM() {
        setupCommon()

        configure<PublishingExtension> {
            publications {
                create<MavenPublication>("jvm") {
                    groupId = project.group as String
                    artifactId = project.name
                    version = project.version as String

                    from(components.getByName("java"))
                }
            }
        }
    }

    fun Project.setupMPP() {
        setupCommon()
    }

    private fun Project.setupCommon() {
        version = if (!isRelease) {
            this@ModuleConfigExtension.version ?: System.getenv("GITHUB_SHA") ?: version
        } else {
            this@ModuleConfigExtension.version ?: version
        }

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
        }
    }
}