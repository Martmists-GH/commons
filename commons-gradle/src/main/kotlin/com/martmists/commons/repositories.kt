package com.martmists.commons

import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.net.URI

fun RepositoryHandler.martmists(snapshots: Boolean = false) {
    maven {
        it.name = "Martmists ${if (snapshots) "Snapshots" else "Releases"}"
        it.url = URI.create("https://maven.martmists.com/${if (snapshots) "snapshots" else "releases"}")
    }
}

fun RepositoryHandler.martmistsPublish(username: String, password: String, snapshots: Boolean = false) {
    maven {
        it.name = "Martmists ${if (snapshots) "Snapshots" else "Releases"}"
        it.url = URI.create("https://maven.martmists.com/${if (snapshots) "snapshots" else "releases"}")
        it.credentials {
            it.username = username
            it.password = password
        }
    }
}
