package com.martmists.commons

import org.gradle.kotlin.dsl.PluginDependenciesSpecScope
import org.gradle.plugin.use.PluginDependencySpec

private fun PluginDependencySpec.version(root: Boolean, versionString: String) = if (root) version(versionString) else this

fun PluginDependenciesSpecScope.shadow(root: Boolean = true, versionString: String = "7.1.2") = id("com.github.johnrengelman.shadow").version(root, versionString)
fun PluginDependenciesSpecScope.versions(root: Boolean = true, versionString: String = "0.42.0") = id("com.github.ben-manes.versions").version(root, versionString)
fun PluginDependenciesSpecScope.useVersions(root: Boolean = true, versionString: String = "0.2.18") = id("se.patrikerdes.use-latest-versions").version(root, versionString)
fun PluginDependenciesSpecScope.buildconfig(root: Boolean = true, versionString: String = "3.0.3") = id("com.github.gmazzo.buildconfig").version(root, versionString)
fun PluginDependenciesSpecScope.properties(root: Boolean = true, versionString: String = "1.5.2") = id("net.saliman.properties").version(root, versionString)
fun PluginDependenciesSpecScope.ksp(root: Boolean = true, versionString: String = "1.7.10-1.0.6") = id("com.google.devtools.ksp").version(root, versionString)
