package dev.mediamod.gui.component

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.constraint
import gg.essential.elementa.dsl.percent
import java.awt.Color

class UIButton(
    text: String,
    textColor: Color,
    shadow: Boolean = true
) : UIBlock() {
    private var action: (UIComponent.() -> Unit)? = null

    init {
        constrain {
            width = 100.percent()
            height = 100.percent()
        }

        onMouseClick {
            action?.invoke(this)
        }

        UIText(text, shadow)
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