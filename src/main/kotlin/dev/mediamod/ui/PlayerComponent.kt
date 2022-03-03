package dev.mediamod.ui

import dev.mediamod.MediaMod
import dev.mediamod.data.Track
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*

class PlayerComponent : UIComponent() {
    private var previousTrack: Track? = null

    private val background = UIBlock(MediaMod.themeManager.currentTheme.colors.background.constraint).constrain {
        x = 5.pixels()
        y = 5.pixels()

        width = 150.pixels()
        height = 50.pixels()
    } childOf this

    private val imageContainer = UIContainer().constrain {
        x = 5.pixels()
        y = CenterConstraint()

        width = 40.pixels()
        height = 40.pixels()
    } childOf background

    private val textContainer = UIContainer().constrain {
        x = SiblingConstraint(5f)
        y = CenterConstraint()

        width = FillConstraint(false)
        height = ChildBasedSizeConstraint()
    } childOf background

    private val trackNameText = UIText("Unknown track").constrain {
        color = MediaMod.themeManager.currentTheme.colors.text.constraint
    } childOf textContainer

    private val artistNameText = UIText("by Unknown artist").constrain {
        color = MediaMod.themeManager.currentTheme.colors.text.darker().constraint
        y = SiblingConstraint(3f)
    } childOf textContainer

    private val progressBar = ProgressBarComponent().constrain {
        y = SiblingConstraint(5f)

        width = 100.percent() - 5.pixels()
        height = 8.pixels()
    } childOf textContainer

    private var image = UIImage.ofResource("")

    init {
        MediaMod.serviceManager.onTrack(this::updateInformation)
    }

    private fun updateInformation(track: Track) {
        trackNameText.setText(track.name)
        artistNameText.setText("by ${track.artist}")

        progressBar.update(track)

        if (previousTrack?.artwork != track.artwork) {
            imageContainer.removeChild(image)

            image = UIImage.ofURL(track.artwork)
                .constrain {
                    width = 100.percent()
                    height = 100.percent()
                } childOf imageContainer
        }

        previousTrack = track
    }
}