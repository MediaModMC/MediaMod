package dev.mediamod.gui.screen.editor.component

import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.ColorConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.constraint
import gg.essential.universal.USound
import java.awt.Color

class CustomThemeListItem(
    text: String,
    private val color: ColorConstraint = Color.white.darker().constraint
) : UIContainer() {
    private var action: (UIComponent.() -> Unit)? = null

    init {
        UIText(text)
            .constrain {
                y = CenterConstraint()
                color = this@CustomThemeListItem.color
            } childOf this

        constrain {
            height = ChildBasedMaxSizeConstraint()
        }

        onMouseClick {
            USound.playButtonPress()
            action?.invoke(this)
        }
    }

    fun onClick(block: UIComponent.() -> Unit) = apply { this.action = block }
}