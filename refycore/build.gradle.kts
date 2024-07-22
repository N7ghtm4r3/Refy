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
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.3")
    implementation("com.github.N7ghtm4r3:APIManager:2.2.3")
    implementation("com.github.N7ghtm4r3:Equinox:1.0.2")
    implementation("org.json:json:20231013")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                groupId = "com.tecknobit.refycore"
                artifactId = "refy-core"
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