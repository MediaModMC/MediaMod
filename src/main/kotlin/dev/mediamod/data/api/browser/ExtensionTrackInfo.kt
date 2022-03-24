package dev.mediamod.data.api.browser

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExtensionTrackInfo(
    val title: String,
    val artist: String,
    @SerialName("album_art")
    val albumArt: String,
    val timestamps: Timestamps,
    val paused: Boolean
) {
    @Serializable
    data class Timestamps(
        val duration: Long,
        val elapsed: Long
    )
}