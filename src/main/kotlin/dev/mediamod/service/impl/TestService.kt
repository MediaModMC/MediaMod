package dev.mediamod.service.impl

import dev.mediamod.data.Track
import dev.mediamod.service.Service
import dev.mediamod.utils.logger
import gg.essential.vigilance.Vigilant
import java.net.URL

class TestService : Service() {
    override val displayName = "Test Service"
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
        button("Test", "This is a test!", "Click me!") {
            logger.info("Test")
        }
    }
}