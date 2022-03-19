package dev.mediamod.data.api.spotify

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpotifyCurrentTrackResponse(
    val item: Item? = null,
    @SerialName("progress_ms")
    val progressMs: Long = 0L,
    @SerialName("is_playing")
    val isPlaying: Boolean = false
) : SpotifyAPIResponse()

@Serializable
data class Item(
    val album: Album,
    val artists: List<Artist>,
    val name: String,
    @SerialName("duration_ms")
    val durationMS: Long,
)

@Serializable
data class Album(
    val images: List<Image>,
    val name: String,
)

@Serializable
data class Artist(
    val name: String,
)

@Serializable
data class Image(
    val height: Long,
    val url: String,
    val width: Long
)