package dev.mediamod.gui.screen

import dev.mediamod.config.Configuration
import dev.mediamod.gui.ColorPalette
import dev.mediamod.gui.component.UIButton
import dev.mediamod.gui.hud.PlayerComponent
import dev.mediamod.gui.style.styled
import dev.mediamod.gui.style.stylesheet
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.WindowScreen
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.pixels
import gg.essential.elementa.utils.withAlpha
import java.awt.Color

class RepositionScreen : WindowScreen(
    version = ElementaVersion.V1,
    restoreCurrentGuiOnClose = true
) {
    private val stylesheet = stylesheet {
        "container" {
            width = 100.percent()
            height = 100.percent()
        }

        "textContainer" {
            y = CenterConstraint()
            width = 100.percent()
            height = ChildBasedMaxSizeConstraint()
        }

        "title" {
            x = CenterConstraint()
            textScale = 1.5f.pixels()
        }

        "subtitle" {
            x = CenterConstraint()
            y = SiblingConstraint(5f)

            color = Color.gray.toConstraint()
            textScale = 0.75f.pixels()
        }

        "player" {
            x = xState.pixels()
            y = yState.pixels()

            width = 150.pixels()
            height = 50.pixels()
        }

        "closeButton" {
            x = CenterConstraint()
            y = 15.pixels(true)
            width = ChildBasedSizeConstraint() + 50.pixels()
            height = 25.pixels()
            color = ColorPalette.secondaryBackground.brighter().constraint
        }
    }

    private val xState = BasicState(Configuration.playerX)
    private val yState = BasicState(Configuration.playerY)
    private val container by UIBlock(ColorPalette.background.withAlpha(0.8f))
        .styled(stylesheet["container"])
        .childOf(window)

    init {
        xState.onSetValue { Configuration.playerX = it }
        yState.onSetValue { Configuration.playerY = it }

        val textContainer = UIContainer()
            .styled(stylesheet["textContainer"])
            .childOf(container)

        UIText("Reposition Player")
            .styled(stylesheet["title"])
            .childOf(textContainer)

        UIText("Drag the player around to change its position. Double click it to reset.")
            .styled(stylesheet["subtitle"])
            .childOf(textContainer)

        var dragOffset = 0f to 0f
        var isDragging = false
        PlayerComponent()
            .styled(stylesheet["player"])
            .onMouseClick { event ->
                if (event.clickCount == 2) {
                    // Reset on double click
                    xState.set(5f)
                    yState.set(5f)

                    return@onMouseClick
                }

                isDragging = true
                dragOffset = event.absoluteX to event.absoluteY
            }
            .onMouseRelease {
                isDragging = false
            }
            .onMouseDrag { mouseX, mouseY, mouseButton ->
                if (mouseButton != 0 || !isDragging)
                    return@onMouseDrag

                val absoluteX = mouseX + getLeft()
                val absoluteY = mouseY + getTop()
                val deltaX = absoluteX - dragOffset.first
                val deltaY = absoluteY - dragOffset.second

                dragOffset = absoluteX to absoluteY

                val newX = getLeft() + deltaX
                val newY = getTop() + deltaY

                if (newX >= 0 && newX <= (this@RepositionScreen.width - this.getWidth())) {
                    xState.set(newX)
                }

                if (newY >= 0 && newY <= (this@RepositionScreen.height - this.getHeight())) {
                    yState.set(newY)
                }
            } childOf container

        UIButton("Close", Color.white)
            .styled(stylesheet["closeButton"])
            .onClick {
                restorePreviousScreen()
            } childOf container
    }

    override fun onScreenClose() {
        super.onScreenClose()
        Configuration.markDirty()
    }
}