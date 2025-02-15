
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("maven-publish")
    id("com.android.library") version "8.2.2"
    kotlin("multiplatform")
    kotlin("plugin.serialization") version "2.0.20"
}

group = "com.tecknobit.refycore"
version = "1.0.1"

repositories {
    google()
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            this@jvm.compilerOptions {
                jvmTarget.set(JvmTarget.JVM_18)
            }
        }
    }
    androidTarget {
        publishLibraryVariants("release")
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_18)
        }
    }
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "refy-core"
            isStatic = true
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        binaries.executable()
        browser {
            webpackTask {
                dependencies {
                }
            }
        }
    }

    sourceSets {

        val commonMain by getting {
            dependencies {
                implementation("io.github.n7ghtm4r3:equinox-core:1.0.7")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
            }
        }

    }

    jvmToolchain(18)
}


android {
    namespace = "com.tecknobit.refycore"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.tecknobit.refycore"
                artifactId = "refycore"
                version = "1.0.1"
                from(components["kotlin"])
            }
        }
    }
}

/*dependencies {
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.4")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.3")
    implementation("com.github.N7ghtm4r3:APIManager:2.2.3")
    implementation("com.github.N7ghtm4r3:Equinox:1.0.2")
    implementation("org.json:json:20231013")
    implementation("commons-validator:commons-validator:1.7")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.tecknobit.refycore"
                artifactId = "refycore"
                version = "1.0.0"
                from(components["java"])
            }
        }
    }
}

configurations.all {
    exclude("commons-logging", "commons-logging")
}

kotlin {
    jvmToolchain(18)
}*/