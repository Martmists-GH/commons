import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.gmazzo.buildconfig")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    compileOnly(gradleKotlinDsl())
    compileOnly(kotlin("gradle-plugin"))
}

tasks {
    withType<KotlinCompile> {
        dependsOn("generateBuildConfig")
    }
}
