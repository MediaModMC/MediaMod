package dev.mediamod.data.serialization

import dev.mediamod.data.api.mediamod.APIResponse
import dev.mediamod.data.api.mediamod.ErrorResponse
import dev.mediamod.data.api.mediamod.PublishThemeResponse
import dev.mediamod.data.api.mediamod.SpotifyTokenResponse
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.*

object APIResponseSerializer : JsonContentPolymorphicSerializer<APIResponse>(APIResponse::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out APIResponse> {
        val ok = element.booleanOrNull("ok")
        if (ok == false)
            return ErrorResponse.serializer()

        val accessToken = element.contentOrNull("access_token")
        if (accessToken != null)
            return SpotifyTokenResponse.serializer()

        val themeId = element.contentOrNull("theme_id")
        if (themeId != null)
            return PublishThemeResponse.serializer()

        return APIResponse.serializer()
    }

    private fun JsonElement.contentOrNull(key: String) =
        jsonObject[key]?.jsonPrimitive?.contentOrNull

    private fun JsonElement.booleanOrNull(key: String) =
        jsonObject[key]?.jsonPrimitive?.booleanOrNull
}