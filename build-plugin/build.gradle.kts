import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
    kotlin("jvm") version "1.6.21"
    id("com.github.gmazzo.buildconfig") version "3.0.3"
}

group = "com.martmists"

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

gradlePlugin {
    plugins {
        create("commons") {
            id = "com.martmists.commons"
            displayName = "Commons Utility Plugin"
            implementationClass = "com.martmists.commons.CommonsPlugin"
        }
    }
}
