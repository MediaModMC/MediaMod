package dev.mediamod.manager

import dev.mediamod.data.api.APIResponse
import dev.mediamod.data.api.ErrorResponse
import dev.mediamod.data.api.SpotifyTokenResponse
import dev.mediamod.utils.json
import dev.mediamod.utils.post
import kotlinx.serialization.decodeFromString

class APIManager {
    companion object {
        private const val baseURL = "http://localhost:8080"
    }

    fun exchangeCode(code: String): Result<SpotifyTokenResponse> {
        return try {
            val response = post("$baseURL/api/v1/spotify/auth", mapOf("code" to code))
            val result: APIResponse = json.decodeFromString(response)

            when (result) {
                is SpotifyTokenResponse -> Result.success(result)
                is ErrorResponse -> Result.failure(Error(result.message))
                else -> Result.failure(Error("Unknown error"))
            }
        } catch (e: Error) {
            Result.failure(e)
        }
    }
}