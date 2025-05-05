rootProject.name = "Refy"

pluginManagement {
    plugins {
        kotlin("jvm") version "2.1.20"
        kotlin("multiplatform") version "2.1.20"
    }
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

include("core")
include("backend")
