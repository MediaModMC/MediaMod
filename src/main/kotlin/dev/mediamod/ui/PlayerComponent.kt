package dev.mediamod.ui

import dev.mediamod.MediaMod
import dev.mediamod.data.Track
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.components.Window
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import java.awt.Color

class PlayerComponent : UIComponent() {
    private val currentTrack = BasicState<Track?>(null)

    private val background = UIRoundedRectangle(5f)
        .constrain {
            x = 5.pixels()
            y = 5.pixels()
            color = Color.darkGray.constraint

            width = 150.pixels()
            height = 50.pixels()
        } childOf this

    private val imageContainer = UIRoundedRectangle(2.5f)
        .constrain {
            x = 5.pixels()
            y = CenterConstraint()
            color = Color(0, 0, 0, 0).constraint

            width = 40.pixels()
            height = 40.pixels()
        } childOf background

    private val trackNameText = UIText("Unknown Track")
        .constrain {
            x = SiblingConstraint(5f)
            y = CenterConstraint()
        } childOf background

    private var image = UIImage.ofResource("")

    init {
        MediaMod.serviceManager.onTrack {
            Window.enqueueRenderOperation { currentTrack.set(this) }
        }

        currentTrack.onSetValue {
            imageContainer.removeChild(image)

            trackNameText.setText(it?.name ?: "Unknown track")

            image = it?.let { UIImage.ofURL(it.artwork) } ?: UIImage.ofResource("")
            image
                .constrain {
                    width = 100.percent()
                    height = 100.percent()
                }
                .childOf(imageContainer)
        }
    }
}