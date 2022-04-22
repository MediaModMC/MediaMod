package dev.mediamod.manager

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.extensions.jsonBody
import com.github.kittinunf.fuel.serialization.responseObject
import com.github.kittinunf.result.Result
import dev.mediamod.MediaMod
import dev.mediamod.data.api.mediamod.APIResponse
import dev.mediamod.theme.Theme
import dev.mediamod.utils.json
import dev.mediamod.utils.hex
import kotlinx.serialization.encodeToString
import dev.mediamod.data.api.mediamod.PublishThemeRequest
import gg.essential.universal.UMinecraft

class APIManager {
    companion object {
        private const val baseURL = "http://localhost:3001"
    }

    fun exchangeCode(code: String) =
        Fuel.post("$baseURL/api/v1/spotify/auth")
            .jsonBody(json.encodeToString(mapOf("code" to code)))
            .responseObject<APIResponse>(json)
            .third

    fun refreshAccessToken(refreshToken: String) =
        Fuel.post("$baseURL/api/v1/spotify/refresh")
            .jsonBody(json.encodeToString(mapOf("refresh_token" to refreshToken)))
            .responseObject<APIResponse>(json)
            .third

    fun publishTheme(theme: Theme.LoadedTheme): Result<APIResponse, FuelError> {
        val sharedSecret = MediaMod.sessionManager.joinServer()
            ?: error("Failed to contact Session Server!")

        val body = PublishThemeRequest(
            UMinecraft.getMinecraft().session.username,
            UMinecraft.getMinecraft().session.uuid,
            sharedSecret.hex(),
            theme
        )

        return Fuel.post("$baseURL/api/v1/themes/publish")
            .jsonBody(json.encodeToString(body))
            .responseObject<APIResponse>(json)
            .third
    }
}