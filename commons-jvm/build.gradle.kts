dependencies {
    subprojects.forEach {
        api(project(":${project.name}:${it.name}"))
    }
}
