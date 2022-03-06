package dev.mediamod.service.impl.spotify

import dev.mediamod.config.Configuration
import dev.mediamod.data.api.SpotifyTokenResponse
import dev.mediamod.service.Service
import dev.mediamod.service.impl.spotify.api.SpotifyAPI
import dev.mediamod.service.impl.spotify.callback.SpotifyCallbackManager
import dev.mediamod.utils.logger
import dev.mediamod.utils.spotifyClientID
import gg.essential.vigilance.Vigilant
import java.util.*

class SpotifyService : Service() {
    private val api = SpotifyAPI(spotifyClientID)
    private val callbackManager = SpotifyCallbackManager()

    override val displayName = "Spotify"
    override val hasConfiguration = true

    override fun init() {
        callbackManager.init()
        callbackManager.onCallback {
            onSuccess(::login)
            onFailure {
                // TODO: Error handling
                logger.error("Failed to login to Spotify: ", it)
            }
        }
    }

    private fun login(response: SpotifyTokenResponse) {
        logger.info("Successfully logged in to Spotify!")
        Configuration.spotifyAccessToken = response.accessToken
        Configuration.spotifyRefreshToken = response.refreshToken
    }

    override fun pollTrack() = api.getCurrentTrack()

    override fun Vigilant.CategoryPropertyBuilder.configuration() {
        subcategory("Authentication") {
            button(
                name = "Login",
                description = "This will open a new tab in your browser to authenticate with the Spotify API.",
                buttonText = "Login"
            ) {
                val url = api.generateAuthorizationURL(
                    scopes = "user-read-currently-playing user-read-playback-position",
                    redirectURI = "http://localhost:9103/callback",
                    state = UUID.randomUUID().toString()
                )
                val args = arrayOf("bash", "-c", "open \"$url\"")
                Runtime.getRuntime().exec(args)
            }

            text(
                field = Configuration::spotifyAccessToken,
                name = "accessToken",
                hidden = true
            )

            text(
                field = Configuration::spotifyRefreshToken,
                name = "refreshToken",
                hidden = true
            )
        }
    }
}