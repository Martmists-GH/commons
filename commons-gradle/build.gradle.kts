import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    `maven-publish`
    kotlin("jvm")
    id("com.github.gmazzo.buildconfig")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleKotlinDsl())
    implementation(kotlin("gradle-plugin"))
    implementation("com.github.gmazzo:gradle-buildconfig-plugin:3.0.3")
}

tasks {
    withType<KotlinCompile> {
        dependsOn("generateBuildConfig")
    }
}

buildConfig {
    useKotlinOutput()
    packageName("com.martmists.commons")

    buildConfigField("String", "group", "\"${project.group}\"")
    buildConfigField("String", "version", "\"${project.version}\"")
}

gradlePlugin {
    plugins {
        create("commons") {
            id = "com.martmists.commons"
            displayName = "Commons Utility Plugin"
            implementationClass = "com.martmists.commons.CommonsPlugin"
        }
    }
}

publishing {
    repositories {
        maven {
            name = "Host"
            url = uri("https://maven.martmists.com/releases")
            credentials {
                username = "admin"
                password = project.ext["mavenToken"]!! as String
            }
        }
    }

    publications {
        create<MavenPublication>("jvm") {
            groupId = project.group as String
            artifactId = project.name
            version = project.version as String

            from(components["java"])
        }
    }
}
