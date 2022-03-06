package dev.mediamod.data.api.spotify

import dev.mediamod.data.serialization.SpotifyAPIResponseSerializer
import kotlinx.serialization.Serializable

@Serializable(with = SpotifyAPIResponseSerializer::class)
open class SpotifyAPIResponse