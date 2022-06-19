# Commons

A list of common utilities I find myself using quite often.

## Usage

```kotlin
// build.gradle.kts
import com.martmists.commons.*

buildscript {
    repositories {
        mavenCentral()  // required for some dependencies of the gradle module
        maven("https://maven.martmists.com/releases")
    }
    dependencies {
        classpath("com.martmists.commons:commons-gradle:1.0.1")
    }
}

repositories {
    martmists()  // releases
    martmists(snapshots=true)  // snapshots
}

// For Kotlin/JVM projects
dependencies {
    commonJVMModule("logging")
    commonMPPModule("functions")
}

// For Kotlin/Multiplatform projects
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                commonMPPModule("math")
            }
        }
    }
}
```
