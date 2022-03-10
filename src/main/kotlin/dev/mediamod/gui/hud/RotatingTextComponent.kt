package dev.mediamod.gui.hud

import dev.mediamod.config.Configuration
import dev.mediamod.utils.setColorAnimated
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.ConstantColorConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.state.BasicState
import gg.essential.elementa.state.pixels
import gg.essential.universal.UGraphics
import gg.essential.universal.UMatrixStack

class RotatingTextComponent(
    private val state: BasicState<String>,
) : UIComponent() {
    private val firstXPosition = BasicState(0f)

    private val firstText = UIText()
        .constrain {
            x = firstXPosition.pixels()
        } childOf this

    private val secondText = UIText()
        .constrain {
            x = SiblingConstraint(25f)
        } childOf this

    init {
        constrain {
            width = 100.percent()
            height = firstText.getHeight().pixels()
        }

        firstText.bindText(state)
        secondText.bindText(state)
    }

    override fun draw(matrixStack: UMatrixStack) {
        super.draw(matrixStack)

        val text = state.get()
        val textWidth = UGraphics.getStringWidth(text)
        val parentWidth = parent.getWidth()
        val firstCurrentPosition = firstXPosition.get()

        if (textWidth < parentWidth) {
            // If the text's width doesn't exceed the parent, we don't need to rotate it around
            firstXPosition.set(0f)
            secondText.hide(true)
            return
        } else {
            // Time to start rotating
            secondText.unhide()
        }

        // TODO: This needs to be fixed, it doesnt line up perfectly on some songs
        if (secondText.constraints.x.cachedValue <= 50) {
            firstXPosition.set(0f)
            return
        }

        firstXPosition.set(firstCurrentPosition - Configuration.textScrollSpeed)
    }

    internal fun changeColorAnimated(constraint: ConstantColorConstraint) = apply {
        firstText.setColorAnimated(constraint)
        secondText.setColorAnimated(constraint)
    }

    internal fun color(constraint: ConstantColorConstraint) = apply {
        firstText.setColor(constraint)
        secondText.setColor(constraint)
    }
}