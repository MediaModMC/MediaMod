package dev.mediamod.gui.hud

import dev.mediamod.MediaMod
import dev.mediamod.config.Configuration
import dev.mediamod.data.Track
import dev.mediamod.theme.Theme
import dev.mediamod.utils.setColorAnimated
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.effects.ScissorEffect
import gg.essential.elementa.state.BasicState
import java.util.concurrent.CompletableFuture

class PlayerComponent : UIBlock(MediaMod.themeManager.currentTheme.colors.background.constraint) {
    private var previousTrack: Track? = null
    private val trackNameState = BasicState("Unknown track")
    private val artistNameState = BasicState("Unknown artist")

    private val imageContainer = UIContainer().constrain {
        x = 5.pixels()
        y = CenterConstraint()

        width = 40.pixels()
        height = 40.pixels()
    } childOf this

    private val textContainer = UIContainer().constrain {
        x = SiblingConstraint(5f)
        y = CenterConstraint()

        width = FillConstraint(false)
        height = ChildBasedSizeConstraint()
    } childOf this effect ScissorEffect()

    private val trackText = RotatingTextComponent(trackNameState)
        .color(MediaMod.themeManager.currentTheme.colors.text.constraint)
        .childOf(textContainer)

    private val artistText = RotatingTextComponent(artistNameState)
        .constrain {
            y = SiblingConstraint(3f)
        }
        .color(MediaMod.themeManager.currentTheme.colors.text.darker().constraint)
        .childOf(textContainer)

    private var image = UIImage.ofResource("")

    init {
        ProgressBarComponent()
            .constrain {
                y = SiblingConstraint(5f)

                width = 100.percent() - 5.pixels()
                height = 8.pixels()
            } childOf textContainer

        MediaMod.serviceManager.currentTrack.onSetValue {
            it?.let { updateInformation(it) }
        }

        MediaMod.themeManager.onChange {
            MediaMod.serviceManager.currentTrack.get()?.let {
                updateInformation(it, true)
            }

            updateTheme(this)
        }

        Configuration.listen(Configuration::playerFirstFormatString) {
            previousTrack?.let { trackNameState.set(this.formatTrack(it)) }
        }

        Configuration.listen(Configuration::playerSecondFormatString) {
            previousTrack?.let { artistNameState.set(this.formatTrack(it)) }
        }

        MediaMod.themeManager.onUpdate(this::updateTheme)
        MediaMod.serviceManager.currentTrack.get()?.let { updateInformation(it) }
    }

    private fun updateInformation(track: Track, forceUpdate: Boolean = false) {
        trackNameState.set(Configuration.playerFirstFormatString.formatTrack(track))
        artistNameState.set(Configuration.playerSecondFormatString.formatTrack(track))

        if (forceUpdate || previousTrack?.artwork != track.artwork) {
            imageContainer.removeChild(image)

            val future = CompletableFuture.supplyAsync {
                UIImage.get(track.artwork).apply(MediaMod.themeManager::updateTheme)
            }

            image = UIImage(future)
                .constrain {
                    width = 100.percent()
                    height = 100.percent()
                } childOf imageContainer
        }

        previousTrack = track
    }

    private fun updateTheme(theme: Theme) =
        theme.apply {
            setColorAnimated(colors.background.constraint)
            trackText.changeColorAnimated(colors.text.constraint)
            artistText.changeColorAnimated(colors.text.darker().constraint)
        }

    private fun String.formatTrack(track: Track) =
        replace("[track]", track.name).replace("[artist]", track.artist)
}