import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import dev.architectury.pack200.java.Pack200Adapter
import net.fabricmc.loom.task.RemapJarTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("gg.essential.loom")
    id("dev.architectury.architectury-pack200")
    id("com.replaymod.preprocess")
    id("com.github.johnrengelman.shadow")
    id("maven-publish")
}

group = "dev.mediamod"
version = "1.0.0"

val mcVersion: Int by extra
val mcPlatform = project.properties["loom.platform"] ?: "fabric"
val minecraftVersion: String by project
val mappingsVersion: String by project
val forgeVersion: String by project
val loaderVersion: String by project

val elementaVersion: String by project
val universalCraftVersion: String by project
val vigilanceVersion: String by project
val modMenuVersion: String by project
val fuelVersion: String by project
val resultVersion: String by project
val fabricLanguageKotlinVersion: String by project
val fabricApiVersion: String by project
val kotlinxSerializationVersion: String by project
val essentialVersion: String by project
val essentialLoaderVersion: String by project
val mixinVersion: String by project
val nightconfigVersion: String by project
val dom4jVersion: String by project
val javaWebsocketVersion: String by project
val kotlinxCoroutinesVersion: String by project
val devauthModuleName =
    if (mcPlatform == "fabric") "fabric" else (if (mcVersion <= 11202) "forge-legacy" else "forge-latest")
val devauthVersion: String by project
val toastsVersion: String by project
val slf4jVersion: String by project
val kotlinForForgeVersion: String by project

preprocess {
    vars.put("MC", mcVersion)
    vars.put("FABRIC", if (mcPlatform == "fabric") 1 else 0)
}

loom {
    runConfigs {
        named("client") {
            ideConfigGenerated(true)
        }
    }

    launchConfigs {
        getByName("client") {
            property("mixin.debug.verbose", "true")
            property("mixin.debug.export", "true")
            property("mixin.dumpTargetOnFailure", "true")

            if (mcPlatform == "forge" && mcVersion <= 112020)
                arg("--tweakClass", "gg.essential.loader.stage0.EssentialSetupTweaker")
        }
    }

    if (mcPlatform == "forge") {
        forge {
            mixinConfigs.set(mutableListOf("mediamod.mixins.json"))
            pack200Provider.set(Pack200Adapter())
        }
    }
}

repositories {
    maven("https://repo.sk1er.club/repository/maven-public")
    maven("https://maven.gegy.dev")
    maven("https://maven.terraformersmc.com")
    maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven("https://jitpack.io")
    maven("https://thedarkcolour.github.io/KotlinForForge")
}

val shade by configurations.creating { isTransitive = false }

val shadeImplementation by configurations.creating { isTransitive = false }
configurations.implementation.get().extendsFrom(shadeImplementation)

val customApi by configurations.creating { isTransitive = false }
if (mcPlatform == "forge") {
    configurations.api.get().extendsFrom(customApi)
} else {
    configurations.modApi.get().extendsFrom(customApi)
}

dependencies {
    minecraft("com.mojang:minecraft:$minecraftVersion")

    if (mcPlatform == "forge") {
        if (mcVersion <= 11202) {
            mappings("de.oceanlabs.mcp:$mappingsVersion")

            shade("org.slf4j:slf4j-api:$slf4jVersion")

            shade("gg.essential:loader-launchwrapper:$essentialLoaderVersion")
            modRuntimeOnly("gg.essential:loader-launchwrapper:$essentialLoaderVersion")

            modCompileOnly("gg.essential:essential-$minecraftVersion-forge:$essentialVersion")
            compileOnly("org.spongepowered:mixin:$mixinVersion")
        } else {
            mappings(loom.officialMojangMappings())
            modImplementation("thedarkcolour:kotlinforforge:$kotlinForForgeVersion")
        }

        "forge"("net.minecraftforge:forge:$forgeVersion")
        shade("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
        shade("org.jetbrains.kotlinx:kotlinx-serialization-core:$kotlinxSerializationVersion")
        shade("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:$kotlinxSerializationVersion")
        shade("org.jetbrains.kotlinx:kotlinx-serialization-core-jvm:$kotlinxSerializationVersion")
    } else {
        mappings("net.fabricmc:yarn:$mappingsVersion:v2")

        modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
        modImplementation("net.fabricmc:fabric-language-kotlin:$fabricLanguageKotlinVersion")
        modImplementation("com.terraformersmc:modmenu:$modMenuVersion")

        modImplementation("com.github.cbyrneee:Toasts:$toastsVersion")
        shade("com.github.cbyrneee:Toasts:$toastsVersion")

        shade("gg.essential:elementa-${minecraftVersion}-${mcPlatform}:${elementaVersion}")
        shade("org.dom4j:dom4j:$dom4jVersion")

        shade("gg.essential:vigilance-${minecraftVersion}-${mcPlatform}:${vigilanceVersion}")
        shade("com.electronwill.night-config:core:$nightconfigVersion")
        shade("com.electronwill.night-config:toml:$nightconfigVersion")

        shade("gg.essential:universalcraft-${minecraftVersion}-${mcPlatform}:${universalCraftVersion}")
    }

    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:$kotlinxSerializationVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")

    customApi("gg.essential:elementa-${minecraftVersion}-${mcPlatform}:${elementaVersion}")
    customApi("gg.essential:universalcraft-${minecraftVersion}-${mcPlatform}:${universalCraftVersion}")
    customApi("gg.essential:vigilance-${minecraftVersion}-${mcPlatform}:${vigilanceVersion}")

    shadeImplementation("com.github.kittinunf.fuel:fuel:${fuelVersion}")
    shadeImplementation("com.github.kittinunf.fuel:fuel-kotlinx-serialization:${fuelVersion}")
    shadeImplementation("com.github.kittinunf.fuel:fuel-coroutines:${fuelVersion}")
    shadeImplementation("com.github.kittinunf.result:result:${resultVersion}")
    shadeImplementation("org.java-websocket:Java-WebSocket:${javaWebsocketVersion}")

    modRuntimeOnly("me.djtheredstoner:DevAuth-${devauthModuleName}:${devauthVersion}")
}

tasks {
    "shadowJar"(ShadowJar::class) {
        archiveClassifier.set("-dev-shaded")
        configurations = listOf(shade, shadeImplementation)
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        if (mcPlatform == "forge") {
            manifest {
                attributes(
                    mapOf(
                        "FMLCorePluginContainsFMLMod" to true,
                        "ForceLoadAsMod" to true,
                        "MixinConfigs" to "mediamod.mixins.json",
                        "ModSide" to "CLIENT",
                        "TweakClass" to "gg.essential.loader.stage0.EssentialSetupTweaker",
                        "TweakOrder" to "0"
                    )
                )
            }
        } else {
            relocate("gg.essential.elementa", "dev.mediamod.relocated.elementa")
            relocate("gg.essential.universal", "dev.mediamod.relocated.universal")
            relocate("gg.essential.vigilance", "dev.mediamod.relocated.vigilance")

            if (mcVersion <= 11202) {
                relocate("org.slf4j", "dev.mediamod.relocated.slf4j")
            }
        }
    }

    "remapJar"(RemapJarTask::class) {
        val shadowJar = named<ShadowJar>("shadowJar").get()
        dependsOn("shadowJar")

        archiveVersion.set("")
        archiveBaseName.set("MediaMod-${project.version}-$minecraftVersion-$mcPlatform")
        input.set(shadowJar.archiveFile)
    }

    "processResources"(ProcessResources::class) {
        inputs.property("version", project.version)
        inputs.property("mcversion", minecraftVersion)
        filesMatching(listOf("mcmod.info", "META-INF/mods.toml", "fabric.mod.json")) {
            expand(mapOf("version" to project.version, "mcversion" to minecraftVersion))
        }
    }

    "compileKotlin"(KotlinCompile::class) {
        kotlinOptions {
            freeCompilerArgs = listOf(
                "-opt-in=kotlinx.serialization.ExperimentalSerializationApi",
                "-opt-in=kotlinx.serialization.InternalSerializationApi"
            )
        }
    }

    "compileJava"(JavaCompile::class) {
        val javaVersion = when {
            mcVersion < 11600 -> 8
            else -> 17
        }
        options.release.set(javaVersion)
    }

    val copyArchives by registering(Copy::class) {
        from(remapJar.get().archiveFile)
        into(rootProject.buildDir.resolve("distributions"))
    }

    assemble.get().dependsOn(copyArchives)
}