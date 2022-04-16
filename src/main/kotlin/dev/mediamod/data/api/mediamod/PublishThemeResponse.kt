package dev.mediamod.data.api.mediamod

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PublishThemeResponse(
    @SerialName("theme_id")
    val themeID: String
) : APIResponse()
