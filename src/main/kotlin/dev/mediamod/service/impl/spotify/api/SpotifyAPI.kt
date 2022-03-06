package dev.mediamod.service.impl.spotify.api

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.serialization.responseObject
import com.github.kittinunf.result.Result
import dev.mediamod.config.Configuration
import dev.mediamod.data.api.spotify.SpotifyAPIResponse
import dev.mediamod.data.api.spotify.SpotifyCurrentTrackResponse
import dev.mediamod.utils.logger
import org.apache.http.client.utils.URIBuilder
import java.net.URL

class SpotifyAPI(
    private val clientID: String
) {
    companion object {
        private const val authBaseURL = "accounts.spotify.com"
        private const val apiBaseURL = "api.spotify.com/v1"
    }

    fun generateAuthorizationURL(scopes: String, redirectURI: String, state: String): URL =
        URIBuilder().apply {
            scheme = "https"
            host = authBaseURL
            path = "/authorize"
            addParameter("response_type", "code")
            addParameter("client_id", clientID)
            addParameter("scope", scopes)
            addParameter("redirect_uri", redirectURI)
            addParameter("state", state)
        }.build().toURL()

    fun getCurrentTrack(): SpotifyCurrentTrackResponse? {
        val accessToken = Configuration.spotifyAccessToken

        val (_, response, result) = Fuel
            .get("https://$apiBaseURL/me/player/currently-playing")
            .authentication()
            .bearer(accessToken)
            .responseObject<SpotifyAPIResponse>()

        return when (result) {
            is Result.Success -> {
                result.get() as SpotifyCurrentTrackResponse
            }
            is Result.Failure -> {
                if (response.statusCode == 400) {
                    // TODO: Let's try refreshing our access token
                } else {
                    logger.error("Error occurred when getting the current track: ", result.error.message)
                }
                null
            }
        }
    }
}