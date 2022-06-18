kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                subprojects.forEach {
                    api(project(":${project.name}:${it.name}"))
                }
            }
        }
    }
}
