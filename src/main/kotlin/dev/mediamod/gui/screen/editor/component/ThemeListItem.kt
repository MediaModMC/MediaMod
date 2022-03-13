package dev.mediamod.gui.screen.editor.component

import dev.mediamod.theme.Theme
import dev.mediamod.utils.setColorAnimated
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.ColorConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.constraint
import gg.essential.elementa.state.BasicState
import gg.essential.universal.ChatColor
import java.awt.Color

class ThemeListItem(
    private val theme: Theme,
    private val selectedColor: ColorConstraint = Color.white.constraint,
    private val unselectedColor: ColorConstraint = Color.white.darker().constraint
) : UIContainer() {
    private var action: (Theme.() -> Unit)? = null

    private val textState = BasicState(theme.name)
    private val text = UIText()
        .bindText(textState)
        .constrain {
            color = unselectedColor
        } childOf this

    init {
        constrain {
            height = ChildBasedMaxSizeConstraint()
        }

        onMouseClick {
            action?.invoke(theme)
            select()
        }
    }

    fun select() {
        textState.set("${ChatColor.BOLD}${theme.name}")
        text.setColorAnimated(selectedColor, 0.1f)
    }

    fun unselect() {
        textState.set(theme.name)
        text.setColorAnimated(unselectedColor, 0.1f)
    }

    fun onClick(block: Theme.() -> Unit) = apply { this.action = block }
}