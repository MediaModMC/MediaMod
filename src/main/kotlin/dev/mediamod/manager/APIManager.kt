package dev.mediamod.manager

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.serialization.responseObject
import dev.mediamod.data.api.mediamod.APIResponse
import dev.mediamod.utils.json
import kotlinx.serialization.encodeToString

class APIManager {
    companion object {
        private const val baseURL = "http://localhost:8080"
    }

    fun exchangeCode(code: String) =
        Fuel.post("$baseURL/api/v1/spotify/auth")
            .jsonBody(json.encodeToString(mapOf("code" to code)))
            .responseObject<APIResponse>()
            .third

    fun refreshAccessToken(refreshToken: String) =
        Fuel.post("$baseURL/api/v1/spotify/refresh")
            .jsonBody(json.encodeToString(mapOf("refresh_token" to refreshToken)))
            .responseObject<APIResponse>()
            .third
}