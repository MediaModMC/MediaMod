package dev.mediamod.data.api.mediamod

import dev.mediamod.data.serialization.APIResponseSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable(with = APIResponseSerializer::class)
open class APIResponse

@Serializable
data class SpotifyTokenResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    val scope: String
) : APIResponse()

@Serializable
data class ErrorResponse(
    val message: String
) : APIResponse()