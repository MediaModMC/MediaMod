package dev.mediamod.ui

import dev.mediamod.MediaMod
import dev.mediamod.data.Track
import dev.mediamod.theme.Theme
import dev.mediamod.utils.setColorAnimated
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import java.util.concurrent.CompletableFuture

class PlayerComponent : UIBlock(MediaMod.themeManager.currentTheme.colors.background.constraint) {
    private var previousTrack: Track? = null

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
    } childOf this

    private val trackNameText = BasicState("Unknown track")
    private val trackText = UIText("Unknown track")
        .constrain {
            color = MediaMod.themeManager.currentTheme.colors.text.constraint
        }
        .childOf(textContainer)
        .bindText(trackNameText)

    private val artistNameText = BasicState("Unknown artist")
    private val artistText = UIText("Unknown artist")
        .constrain {
            color = MediaMod.themeManager.currentTheme.colors.text.darker().constraint
            y = SiblingConstraint(3f)
        }
        .childOf(textContainer)
        .bindText(artistNameText)

    private var image = UIImage.ofResource("")

    init {
        constrain {
            x = 5.pixels()
            y = 5.pixels()

            width = 150.pixels()
            height = 50.pixels()
        }

        ProgressBarComponent().constrain {
            y = SiblingConstraint(5f)

            width = 100.percent() - 5.pixels()
            height = 8.pixels()
        } childOf textContainer

        MediaMod.serviceManager.currentTrack.onSetValue {
            it?.let { updateInformation(it) }
            previousTrack = it
        }

        MediaMod.themeManager.onChange {
            MediaMod.serviceManager.currentTrack.get()?.let {
                updateInformation(it, true)
            }

            updateTheme(this)
        }

        MediaMod.themeManager.onUpdate(this::updateTheme)
    }

    private fun updateInformation(track: Track, forceUpdate: Boolean = false) {
        trackNameText.set(track.name)
        artistNameText.set("by ${track.artist}")

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
    }

    private fun updateTheme(theme: Theme) =
        theme.apply {
            setColorAnimated(colors.background.constraint)
            trackText.setColorAnimated(colors.text.constraint)
            artistText.setColorAnimated(colors.text.darker().constraint)
        }
}