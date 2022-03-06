package dev.mediamod.service.impl.spotify

import dev.mediamod.data.Track
import dev.mediamod.service.Service
import dev.mediamod.service.impl.spotify.api.SpotifyAPI
import dev.mediamod.service.impl.spotify.callback.SpotifyCallbackManager
import dev.mediamod.utils.spotifyClientID
import gg.essential.vigilance.Vigilant
import java.net.URL
import java.util.*

class SpotifyService : Service() {
    private val api = SpotifyAPI(spotifyClientID)
    private val callbackManager = SpotifyCallbackManager()

    override val displayName = "Spotify"
    override val hasConfiguration = true

    override fun init() = callbackManager.init()

    override fun pollTrack() = Track(
        "daisy",
        "still_bloom",
        URL("https://lite-images-i.scdn.co/image/ab67616d0000b273bc10f89a8eb0fc4262848992"),
        0,
        30000,
        false
    )

    override fun Vigilant.CategoryPropertyBuilder.configuration() {
        subcategory("Authentication") {
            button(
                "Login",
                "This will open a new tab in your browser to authenticate with the Spotify API.",
                "Login"
            ) {
                // TODO: Replace with UDesktop when PR #14 is merged
                val url = api.generateAuthorizationURL(
                    scopes = "user-read-private user-read-email",
                    redirectURI = "http://localhost:9103/callback",
                    state = UUID.randomUUID().toString()
                )
                val args = arrayOf("bash", "-c", "open \"$url\"")
                Runtime.getRuntime().exec(args)
            }
        }
    }
}