package dev.mediamod.data.api.mojang

import kotlinx.serialization.Serializable

@Serializable
data class SessionJoinRequest(
    val accessToken: String,
    val selectedProfile: String,
    val serverId: String
)
