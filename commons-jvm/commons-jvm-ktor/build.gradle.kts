dependencies {
    for (module in listOf(
        "core", "pebble"
    )) {
        api("io.ktor:ktor-server-$module-jvm:2.0.1")
    }
}
