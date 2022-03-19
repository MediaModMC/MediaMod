package dev.mediamod.gui.screen.editor.component

import dev.mediamod.gui.style.styled
import dev.mediamod.gui.style.stylesheet
import dev.mediamod.theme.Theme
import dev.mediamod.utils.setColorAnimated
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIImage
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.ColorConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import gg.essential.universal.ChatColor
import gg.essential.universal.USound
import java.awt.Color

class ThemeListItem(
    val theme: Theme,
    private val selectedColor: ColorConstraint = Color.white.constraint,
    private val unselectedColor: ColorConstraint = Color.white.darker().constraint
) : UIContainer() {
    private var action: (Theme.() -> Unit)? = null
    private val textState = BasicState(theme.name)

    private val stylesheet = stylesheet {
        "this" {
            height = ChildBasedMaxSizeConstraint()
        }

        "lockImage" {
            y = CenterConstraint() - 0.5f.pixels()
            width = 8.pixels()
            height = 8.pixels()
        }

        "text" {
            x = SiblingConstraint(3f)
            y = CenterConstraint()
            color = unselectedColor
        }
    }

    private val lockImage by UIImage.ofResource("/assets/mediamod/textures/icon/lock.png")
        .styled(stylesheet["lockImage"])
        .childOf(this)

    private val text by UIText()
        .bindText(textState)
        .styled(stylesheet["text"])
        .childOf(this)

    init {
        styled(stylesheet["this"])

        if (theme !is Theme.InbuiltTheme) {
            lockImage.hide()
            text.setX(11.pixels())
        }

        onMouseClick {
            USound.playButtonPress()
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