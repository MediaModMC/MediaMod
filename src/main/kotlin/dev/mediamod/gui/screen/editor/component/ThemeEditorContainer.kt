package dev.mediamod.gui.screen.editor.component

import dev.mediamod.theme.Colors
import dev.mediamod.theme.Theme
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import java.awt.Color

@Suppress("unused")
class ThemeEditorContainer : UIContainer() {
    val theme = BasicState<Theme?>(null)

    private val themeNameState = BasicState("")
    private val themeNameText = UIText()
        .bindText(themeNameState)
        .constrain {
            textScale = 1.5f.pixels()
        } childOf this

    private val colorsContainer by UIContainer()
        .constrain {
            y = SiblingConstraint(7.5f)
            width = 100.percent()
            height = 100.percent()
        } childOf this

    init {
        constrain {
            x = 15.pixels()
            y = 15.pixels()
            width = 100.percent() - 30.pixels()
            height = 100.percent() - 30.pixels()
        }

        theme.onSetValue {
            it?.let {
                themeNameState.set(it.name)
                loadColors(it.colors)
            }
        }
    }

    private fun loadColors(colors: Colors) {
        colorsContainer.children.clear()

        colorComponent(colors.background, "Background")
        colorComponent(colors.progressBar, "Progress Bar")
        colorComponent(colors.progressBarBackground, "Progress Bar Background")
        colorComponent(colors.progressBarText, "Progress Bar Text")
        colorComponent(colors.text, "Text")
    }

    private fun colorComponent(color: Color, name: String) {
        ThemeColorComponent(color, name)
            .constrain {
                y = SiblingConstraint(5f)
            } childOf colorsContainer
    }
}