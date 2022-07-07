rootProject.name = "commons"

includeBuild("build-plugin")

val jvmModules = listOf(
    // Load order important
    "logging",   // required by database

    // Load order not important
    "config",
    "database",
    "ktor",
)
val mppModules = listOf(
    // Load order important
    "extensions",  // required by datastructures and math
    "functions",   // required by math

    // Load order not important
    "datastructures",
    "math",
)

jvmModules.forEach {
    include(":commons-jvm:commons-jvm-$it")
}
mppModules.forEach {
    include(":commons-mpp:commons-mpp-$it")
}

include(":commons-gradle")
include(":commons-jvm")
include(":commons-mpp")
