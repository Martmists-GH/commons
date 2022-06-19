import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.github.gmazzo.buildconfig")
}

repositories {
    mavenCentral()
    gradlePluginPortal()
}

dependencies {
    implementation(gradleKotlinDsl())
    implementation(kotlin("gradle-plugin"))
}

tasks {
    withType<KotlinCompile> {
        dependsOn("generateBuildConfig")
    }
}
