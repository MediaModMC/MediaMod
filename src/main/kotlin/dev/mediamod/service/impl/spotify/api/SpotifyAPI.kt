package dev.mediamod.service.impl.spotify.api

import dev.mediamod.config.Configuration
import dev.mediamod.data.Track
import dev.mediamod.data.api.spotify.SpotifyCurrentTrackResponse
import dev.mediamod.utils.get
import dev.mediamod.utils.json
import kotlinx.serialization.decodeFromString
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

    fun getCurrentTrack(): Track? {
        // TODO: Check if refresh token is out of date
        val accessToken = Configuration.spotifyAccessToken
        val result = get(
            "https://$apiBaseURL/me/player/currently-playing",
            mapOf("Authorization" to "Bearer $accessToken")
        )

        // TODO: Error handling
        return try {
            val response: SpotifyCurrentTrackResponse = json.decodeFromString(result)
            Track(
                name = response.item.name,
                artist = response.item.artists.first().name,
                artwork = URL(response.item.album.images.first().url),
                elapsed = response.progressMs,
                duration = response.item.durationMS,
                paused = !response.isPlaying
            )
        } catch (e: Error) {
            null
        }
    }
}