package dev.mediamod.utils

import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager

internal val logger = LogManager.getLogger("MediaMod")

internal val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
}