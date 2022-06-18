package com.martmists.commons

import org.gradle.kotlin.dsl.DependencyHandlerScope

// Commons
fun DependencyHandlerScope.commonJVMModule(module: String) = commonJVMModule(module, BuildConfig.version)
fun DependencyHandlerScope.commonJVMModule(module: String, version: String) : String {
    return "${BuildConfig.group}:commons-jvm-$module:$version"
}

fun DependencyHandlerScope.commonMPPModule(module: String) = commonMPPModule(module, BuildConfig.version)
fun DependencyHandlerScope.commonMPPModule(module: String, version: String) : String {
    return "${BuildConfig.group}:commons-mpp-$module:$version"
}

