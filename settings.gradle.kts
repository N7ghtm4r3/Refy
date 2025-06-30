rootProject.name = "Refy"

pluginManagement {
    plugins {
        kotlin("jvm") version "2.2.0"
        kotlin("multiplatform") version "2.2.0"
    }
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

include("core")
include("backend")
