package com.martmists.commons

import org.gradle.kotlin.dsl.DependencyHandlerScope

// Commons
fun DependencyHandlerScope.commonJVMModule(module: String) = commonJVMModule(module, BuildConfig.VERSION)
fun DependencyHandlerScope.commonJVMModule(module: String, version: String) : String {
    return "${BuildConfig.GROUP}:commons-jvm-$module:$version"
}

fun DependencyHandlerScope.commonMPPModule(module: String) = commonMPPModule(module, BuildConfig.VERSION)
fun DependencyHandlerScope.commonMPPModule(module: String, version: String) : String {
    return "${BuildConfig.GROUP}:commons-mpp-$module:$version"
}

