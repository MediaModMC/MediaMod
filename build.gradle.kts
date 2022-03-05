import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    kotlin("plugin.serialization") version "1.6.10"

    id("fabric-loom") version "0.11-SNAPSHOT"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
}

group = "dev.cbyrne"
version = "1.0.0"

repositories {
    maven("https://repo.sk1er.club/repository/maven-public")
    maven("https://maven.gegy.dev")
    maven("https://maven.terraformersmc.com")
}

val shade: Configuration by configurations.creating {
    isTransitive = false
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
    modImplementation(libs.modmenu)

    shade(libs.elementa)
    shade(libs.universalcraft)
    shade(libs.dom4j)
}

tasks {
    "compileKotlin"(KotlinCompile::class) {
        kotlinOptions {
            freeCompilerArgs = listOf("-opt-in=kotlinx.serialization.ExperimentalSerializationApi", "-opt-in=kotlinx.serialization.InternalSerializationApi")
        }
    }

    "shadowJar"(ShadowJar::class) {
        configurations = listOf(shade)
        archiveClassifier.set("dev-shaded")

        relocate("gg.essential.elementa", "dev.mediamod.relocated.elementa")
        relocate("gg.essential.universal", "dev.mediamod.relocated.universal")
    }

    "remapJar"(RemapJarTask::class) {
        dependsOn("shadowJar")

        val shadowJar = named<ShadowJar>("shadowJar").get()
        inputFile.set(shadowJar.archiveFile)
    }
}