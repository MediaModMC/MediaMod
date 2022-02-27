package dev.mediamod.ui

import dev.mediamod.MediaMod
import dev.mediamod.data.Track
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import java.awt.Color

class PlayerComponent : UIComponent() {
    private val currentTrack = BasicState<Track?>(null)
    private val previousTrack: Track? = null

    private val background = UIBlock(Color.darkGray.darker()).constrain {
        x = 5.pixels()
        y = 5.pixels()

        width = 150.pixels()
        height = 50.pixels()
    } childOf this

    private val imageContainer = UIContainer().constrain {
        x = 5.pixels()
        y = CenterConstraint()
        color = Color(0, 0, 0, 0).constraint

        width = 40.pixels()
        height = 40.pixels()
    } childOf background

    private val textContainer = UIContainer().constrain {
        x = SiblingConstraint(5f)
        y = CenterConstraint()

        width = FillConstraint(false)
        height = ChildBasedSizeConstraint()
    } childOf background

    private val trackNameText = UIText("Unknown track") childOf textContainer

    private val artistNameText = UIText("by Unknown artist").constrain {
        color = Color.lightGray.constraint
        y = SiblingConstraint(3f)
    } childOf textContainer

    private val progressBar = ProgressBarComponent().constrain {
        y = SiblingConstraint(5f)

        width = 100.percent() - 5.pixels()
        height = 8.pixels()
    } childOf textContainer

    private var image = UIImage.ofResource("")

    init {
        MediaMod.serviceManager.onTrack {
            Window.enqueueRenderOperation { currentTrack.set(this) }
        }

        currentTrack.onSetValue {
            trackNameText.setText(it?.name ?: "Unknown track")
            artistNameText.setText("by ${it?.artist ?: "Unknown artist"}")

            if (previousTrack?.artwork != it?.artwork) {
                imageContainer.removeChild(image)

                image = it?.let { UIImage.ofURL(it.artwork) } ?: UIImage.ofResource("")
                image.constrain {
                    width = 100.percent()
                    height = 100.percent()
                } childOf imageContainer
            }

            it?.let { progressBar.update(it) }
        }
    }
}