@file:Suppress("MemberVisibilityCanBePrivate")

package dev.mediamod.service.impl.spotify.api

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.result.Result
import dev.mediamod.MediaMod
import dev.mediamod.config.Configuration
import dev.mediamod.data.api.mediamod.ErrorResponse
import dev.mediamod.data.api.mediamod.SpotifyTokenResponse
import dev.mediamod.data.api.spotify.SpotifyCurrentTrackResponse
import dev.mediamod.utils.json
import dev.mediamod.utils.logger
import kotlinx.serialization.decodeFromString
import org.apache.http.client.utils.URIBuilder
import java.net.URI

class SpotifyAPI(
    private val clientID: String
) {
    companion object {
        private const val authBaseURL = "accounts.spotify.com"
        private const val apiBaseURL = "api.spotify.com/v1"
    }

    fun generateAuthorizationURI(scopes: String, redirectURI: String, state: String): URI =
        URIBuilder().apply {
            scheme = "https"
            host = authBaseURL
            path = "/authorize"
            addParameter("response_type", "code")
            addParameter("client_id", clientID)
            addParameter("scope", scopes)
            addParameter("redirect_uri", redirectURI)
            addParameter("state", state)
        }.build()

    fun getCurrentTrack(): SpotifyCurrentTrackResponse? {
        val accessToken = Configuration.spotifyAccessToken

        val (_, response, result) = Fuel
            .get("https://$apiBaseURL/me/player/currently-playing")
            .authentication()
            .bearer(accessToken)
            .responseString()

        return when (result) {
            is Result.Success -> {
                if (response.statusCode == 204)
                    return null

                return json.decodeFromString(result.get())
            }
            is Result.Failure -> {
                if (response.statusCode == 400 || response.statusCode == 401) {
                    refreshAccessToken(Configuration.spotifyRefreshToken)
                } else {
                    logger.error("Error occurred when getting the current track: ", result.error)
                }
                null
            }
        }
    }

    fun refreshAccessToken(refreshToken: String) =
        when (val result = MediaMod.apiManager.refreshAccessToken(refreshToken)) {
            is Result.Success -> {
                when (val response = result.get()) {
                    is ErrorResponse -> logger.error("Error occurred when refreshing access token: ${response.message}")
                    is SpotifyTokenResponse -> {
                        Configuration.spotifyAccessToken = response.accessToken
                        Configuration.spotifyRefreshToken = response.refreshToken
                        Configuration.markDirty()

                        logger.info("Successfully refreshed access token!")
                    }
                    else -> logger.error("Error occurred when refreshing access token: ${result.get()}")
                }
            }
            is Result.Failure -> {
                // TODO: Let's find a way to make this less spammy when the API isn't available
                logger.error("Error occurred when refreshing access token! (API not accessible)")
            }
        }
}