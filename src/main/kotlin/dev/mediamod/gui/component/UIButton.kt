package dev.mediamod.gui.component

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIRoundedRectangle
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.constraint
import java.awt.Color

class UIButton(
    text: String,
    textColor: Color,
) : UIBlock() {
    private var action: (UIComponent.() -> Unit)? = null

    init {
        onMouseClick {
            action?.invoke(this)
        }

        constrain {
            width = FillConstraint()
            height = FillConstraint()
        }

        UIText(text)
            .constrain {
                x = CenterConstraint()
                y = CenterConstraint()
                color = textColor.constraint
            } childOf this
    }

    fun onClick(block: UIComponent.() -> Unit) = apply {
        this.action = block
    }
}