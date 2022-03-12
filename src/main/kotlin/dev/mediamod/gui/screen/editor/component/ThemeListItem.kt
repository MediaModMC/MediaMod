package dev.mediamod.gui.screen.editor.component

import dev.mediamod.theme.Theme
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.constraint
import java.awt.Color

class ThemeListItem(theme: Theme) : UIContainer() {
    private var action: (Theme.() -> Unit)? = null

    init {
        constrain {
            height = ChildBasedMaxSizeConstraint()
        }

        UIText(theme.name)
            .constrain {
                color = Color.white.darker().constraint
            } childOf this

        onMouseClick {
            action?.invoke(theme)
        }
    }

    fun onClick(block: Theme.() -> Unit) = apply { this.action = block }
}