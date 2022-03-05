package dev.mediamod.service.impl.spotify

import dev.mediamod.data.Track
import dev.mediamod.service.Service
import dev.mediamod.utils.logger
import gg.essential.vigilance.Vigilant
import java.net.URL

class SpotifyService : Service() {
    override val displayName = "Spotify"
    override val hasConfiguration = true

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
                logger.info("Test")
            }
        }
    }
}