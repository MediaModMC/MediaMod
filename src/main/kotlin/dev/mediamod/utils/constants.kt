package dev.mediamod.utils

import kotlinx.serialization.json.Json
import org.apache.logging.log4j.LogManager

internal val spotifyClientID = "88ddf756462c4e078933a42f4cdb33e8"

internal val logger = LogManager.getLogger("MediaMod")

internal val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
}