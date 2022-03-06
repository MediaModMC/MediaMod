package dev.mediamod.data.serialization

import dev.mediamod.data.api.mediamod.APIResponse
import dev.mediamod.data.api.mediamod.ErrorResponse
import dev.mediamod.data.api.mediamod.SpotifyTokenResponse
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.*

object APIResponseSerializer : JsonContentPolymorphicSerializer<APIResponse>(APIResponse::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out APIResponse> {
        val message = element.contentOrNull("message")
        if (message != null)
            return ErrorResponse.serializer()

        val accessToken = element.contentOrNull("access_token")
        if (accessToken != null)
            return SpotifyTokenResponse.serializer()

        return APIResponse.serializer()
    }

    private fun JsonElement.contentOrNull(key: String) =
        jsonObject[key]?.jsonPrimitive?.contentOrNull
}