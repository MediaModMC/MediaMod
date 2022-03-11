package dev.mediamod.service.impl.spotify

import com.github.kittinunf.result.Result
import dev.mediamod.config.Configuration
import dev.mediamod.data.Track
import dev.mediamod.data.api.mediamod.ErrorResponse
import dev.mediamod.data.api.mediamod.SpotifyTokenResponse
import dev.mediamod.service.Service
import dev.mediamod.service.impl.spotify.api.SpotifyAPI
import dev.mediamod.service.impl.spotify.callback.SpotifyCallbackManager
import dev.mediamod.utils.logger
import dev.mediamod.utils.spotifyClientID
import gg.essential.universal.UDesktop
import gg.essential.vigilance.Vigilant
import java.net.URL
import java.util.*

class SpotifyService : Service() {
    private val api = SpotifyAPI(spotifyClientID)
    private val callbackManager = SpotifyCallbackManager()

    override val displayName = "Spotify"
    override val hasConfiguration = true

    override fun init() {
        callbackManager.init()
        callbackManager.onCallback {
            when (this) {
                is Result.Success -> when (val value = this.get()) {
                    is SpotifyTokenResponse -> login(value)
                    is ErrorResponse -> loginError(value.message)
                }
                is Result.Failure -> loginError(error.message ?: error.response.responseMessage)
            }
        }
    }

    private fun loginError(error: String) {
        logger.error("Failed to log in to spotify: ", error)
    }

    private fun login(response: SpotifyTokenResponse) {
        Configuration.spotifyAccessToken = response.accessToken
        Configuration.spotifyRefreshToken = response.refreshToken
        Configuration.markDirty()

        logger.info("Successfully logged in to Spotify!")
    }

    override fun pollTrack(): Track? {
        val response = api.getCurrentTrack() ?: return null
        return Track(
            name = response.item.name,
            artist = response.item.artists.joinToString(", ") { it.name },
            artwork = URL(response.item.album.images.first().url),
            elapsed = response.progressMs,
            duration = response.item.durationMS,
            paused = !response.isPlaying
        )
    }

    override fun Vigilant.CategoryPropertyBuilder.configuration() {
        subcategory("Authentication") {
            button(
                name = "Login",
                description = "This will open a new tab in your browser to authenticate with the Spotify API.",
                buttonText = "Login"
            ) {
                val uri = api.generateAuthorizationURI(
                    scopes = "user-read-currently-playing user-read-playback-position",
                    redirectURI = "http://localhost:9103/callback",
                    state = UUID.randomUUID().toString()
                )

                logger.info("Opening $uri")
                UDesktop.browse(uri)
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