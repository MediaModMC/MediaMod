package dev.mediamod.gui.component

import dev.mediamod.gui.ColorPalette
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.utils.withAlpha
import java.awt.Color

class UIDialog(title: String) : UIBlock(Color.black.withAlpha(0.65f)) {
    private val container = UIBlock(ColorPalette.background)
        .constrain {
            x = CenterConstraint()
            y = CenterConstraint()
            width = ChildBasedSizeConstraint()
            height = ChildBasedSizeConstraint() + 18.pixels()
        } childOf this

    init {
        onMouseClick { event ->
            if (event.target != this@UIDialog)
                return@onMouseClick

            hide()
        }

        constrain {
            width = 100.percent()
            height = 100.percent()
        }

        UIText(title)
            .constrain {
                x = 10.pixels()
                y = 10.pixels()
                textScale = 1.25f.pixels()
            } childOf container
    }

    fun content(block: () -> UIComponent) = apply {
        block()
            .constrain {
                x = 10.pixels()
                y = SiblingConstraint(5f)
            } childOf container
    }

    override fun afterInitialization() {
        hide(true)
        super.afterInitialization()
    }
}