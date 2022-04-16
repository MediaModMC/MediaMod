package dev.mediamod.data.api.mediamod

import dev.mediamod.theme.Theme
import kotlinx.serialization.Serializable

@Serializable
data class PublishThemeRequest(
    val username: String,
    val sharedSecret: String,
    val theme: Theme.LoadedTheme
)
