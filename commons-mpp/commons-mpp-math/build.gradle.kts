kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(project(":commons-mpp:commons-mpp-extensions"))
                api(project(":commons-mpp:commons-mpp-functions"))
            }
        }
    }
}
