package dev.mediamod.data.api.spotify

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpotifyCurrentTrackResponse(
    val timestamp: Long,
    val context: Context,

    @SerialName("progress_ms")
    val progressMs: Long,

    val item: Item,

    @SerialName("currently_playing_type")
    val currentlyPlayingType: String,

    val actions: Actions,

    @SerialName("is_playing")
    val isPlaying: Boolean
) : SpotifyAPIResponse()

@Serializable
data class Actions(
    val disallows: Disallows
)

@Serializable
data class Disallows(
    val resuming: Boolean? = null,
    val pausing: Boolean? = null
)

@Serializable
data class Context(
    @SerialName("external_urls")
    val externalUrls: ExternalUrls,

    val href: String,
    val type: String,
    val uri: String
)

@Serializable
data class ExternalUrls(
    val spotify: String
)

@Serializable
data class Item(
    val album: Album,
    val artists: List<Artist>,

    @SerialName("available_markets")
    val availableMarkets: List<String>,

    @SerialName("disc_number")
    val discNumber: Long,

    @SerialName("duration_ms")
    val durationMS: Long,

    val explicit: Boolean,

    @SerialName("external_ids")
    val externalIDS: ExternalIDS,

    @SerialName("external_urls")
    val externalUrls: ExternalUrls,

    val href: String,
    val id: String,

    @SerialName("is_local")
    val isLocal: Boolean,

    val name: String,
    val popularity: Long,

    @SerialName("preview_url")
    val previewURL: String,

    @SerialName("track_number")
    val trackNumber: Long,

    val type: String,
    val uri: String
)

@Serializable
data class Album(
    @SerialName("album_type")
    val albumType: String,

    val artists: List<Artist>,

    @SerialName("available_markets")
    val availableMarkets: List<String>,

    @SerialName("external_urls")
    val externalUrls: ExternalUrls,

    val href: String,
    val id: String,
    val images: List<Image>,
    val name: String,

    @SerialName("release_date")
    val releaseDate: String,

    @SerialName("release_date_precision")
    val releaseDatePrecision: String,

    @SerialName("total_tracks")
    val totalTracks: Long,

    val type: String,
    val uri: String
)

@Serializable
data class Artist(
    @SerialName("external_urls")
    val externalUrls: ExternalUrls,

    val href: String,
    val id: String,
    val name: String,
    val type: String,
    val uri: String
)

@Serializable
data class Image(
    val height: Long,
    val url: String,
    val width: Long
)

@Serializable
data class ExternalIDS(
    val isrc: String
)
