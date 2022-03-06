package dev.mediamod.data.serialization

import dev.mediamod.data.api.spotify.SpotifyAPIResponse
import dev.mediamod.data.api.spotify.SpotifyCurrentTrackResponse
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.*

object SpotifyAPIResponseSerializer : JsonContentPolymorphicSerializer<SpotifyAPIResponse>(SpotifyAPIResponse::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out SpotifyAPIResponse> {
        val type = element.contentOrNull("currently_playing_type")
        if (type != null)
            return SpotifyCurrentTrackResponse.serializer()

        return SpotifyAPIResponse.serializer()
    }

    private fun JsonElement.contentOrNull(key: String) =
        jsonObject[key]?.jsonPrimitive?.contentOrNull
}