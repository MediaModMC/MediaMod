plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"

    id("fabric-loom") version "0.11-SNAPSHOT"
    id("maven-publish")
}

group = "dev.cbyrne"
version = "1.0.0"

repositories {
    maven("https://repo.sk1er.club/repository/maven-public")
    maven("https://maven.gegy.dev")
}

dependencies {
    minecraft(libs.minecraft)
    mappings(libs.yarn) {
        artifact { classifier = "v2" }
    }

    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
    modImplementation(libs.fabric.kotlin)
    modImplementation(libs.elementa)
    modRuntimeOnly(libs.databreaker)

    include(libs.elementa)
}