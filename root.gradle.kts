plugins {
    kotlin("jvm") version "1.6.10" apply false
    kotlin("plugin.serialization") version "1.6.10" apply false
    id("com.github.johnrengelman.shadow") version "7.1.2" apply false
    id("com.replaymod.preprocess") version "0ab22d2"
    id("gg.essential.loom") version "0.10.0.1" apply false
}

configurations.register("compileClasspath")

preprocess {
    "1.18.1-fabric"(11801, "yarn") {
        "1.18.1-forge"(11801, "srg") {
            "1.12.2"(11202, "srg", file("versions/1.18.1-1.12.2.txt")) {
                "1.8.9"(10809, "srg", file("versions/1.12.2-1.8.9.txt"))
            }
        }
    }
}