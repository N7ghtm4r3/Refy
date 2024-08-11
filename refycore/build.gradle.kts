plugins {
    id("java")
    id("maven-publish")
    kotlin("jvm")
}

group = "com.tecknobit"
version = "1.0.0"

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://repo.clojars.org")
}

dependencies {
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
}