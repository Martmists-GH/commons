package com.martmists.commons

import com.github.gmazzo.gradle.plugins.BuildConfigExtension
import com.github.gmazzo.gradle.plugins.generators.BuildConfigKotlinGenerator
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

open class CommonsPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            setupPlugins()
            setupExtensions()
            setupDependencies()
        }
    }

    private fun Project.setupPlugins() {
        apply(plugin="com.github.gmazzo.buildconfig")
        apply(plugin="org.gradle.maven-publish")

        if (name.startsWith("commons-jvm") || name.startsWith("commons-gradle")) {
            apply(plugin="org.jetbrains.kotlin.jvm")
        } else {
            apply(plugin="org.jetbrains.kotlin.multiplatform")
        }
    }

    private fun Project.setupExtensions() {
        extensions.apply {
            create<ModuleConfigExtension>("moduleConfig")

            configure<BuildConfigExtension> {
                generator.set(BuildConfigKotlinGenerator())
                packageName("com.martmists.commons")
                buildConfigField("String", "VERSION", "\"${rootProject.version}\"")
                buildConfigField("String", "GROUP", "\"${rootProject.group}\"")
            }

            if (name.startsWith("commons-jvm") || name.startsWith("commons-gradle")) {
                configure<KotlinJvmProjectExtension> {

                }
            } else {
                configure<KotlinMultiplatformExtension> {
                    jvm {

                    }
                    js(IR) {
                        browser()
                        nodejs()
                    }
                    linuxX64()
                    linuxArm64()
                    mingwX64()
                    macosX64()
                    macosArm64()
                }
            }
        }

        afterEvaluate {
            with(the<ModuleConfigExtension>()) {
                if (name.startsWith("commons-jvm") || name.startsWith("commons-gradle")) {
                    project.setupJVM()
                } else {
                    project.setupMPP()
                }
            }
        }
    }

    private fun Project.setupDependencies() {
        if (name.startsWith("commons-jvm") || name.startsWith("commons-gradle")) {
            dependencies.apply {
                add("testImplementation", kotlin("test"))
            }
        } else {
            val commonTest by kotlinExtension.sourceSets.getting {
                dependencies {
                    implementation(kotlin("test"))
                }
            }
        }
    }
}
