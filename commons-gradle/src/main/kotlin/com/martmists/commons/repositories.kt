package com.martmists.commons

import org.gradle.api.artifacts.dsl.RepositoryHandler
import java.net.URI

fun RepositoryHandler.martmists(snapshots: Boolean = false) {
    maven {
        name = "Martmists ${if (snapshots) "Snapshots" else "Releases"}"
        url = URI.create("https://maven.martmists.com/${if (snapshots) "snapshots" else "releases"}")
    }
}

fun RepositoryHandler.martmistsPublish(username: String, password: String, snapshots: Boolean = false) {
    maven {
        name = "Martmists ${if (snapshots) "Snapshots" else "Releases"}"
        url = URI.create("https://maven.martmists.com/${if (snapshots) "snapshots" else "releases"}")
        credentials {
            this.username = username
            this.password = password
        }
    }
}
