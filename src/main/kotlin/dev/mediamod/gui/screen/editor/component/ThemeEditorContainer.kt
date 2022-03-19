package dev.mediamod.gui.screen.editor.component

import dev.mediamod.theme.Colors
import dev.mediamod.theme.Theme
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import java.awt.Color
import kotlin.reflect.KMutableProperty

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
            } ?: run {
                themeNameState.set("")
                colorsContainer.children.clear()
            }
        }
    }

    private fun loadColors(colors: Colors) {
        colorsContainer.children.clear()

        fun complete() = theme.get()?.let {
            if (it !is Theme.LoadedTheme) return@let
            it.colors = colors

            // TODO: Write to file
        }

        colorComponent(colors::background, "Background", ::complete)
        colorComponent(colors::progressBar, "Progress Bar", ::complete)
        colorComponent(colors::progressBarBackground, "Progress Bar Background", ::complete)
        colorComponent(colors::progressBarText, "Progress Bar Text", ::complete)
        colorComponent(colors::text, "Text", ::complete)
    }

    private fun colorComponent(color: KMutableProperty<Color>, name: String, block: () -> Unit) {
        ThemeColorComponent(color.getter.call(), name, theme.get() is Theme.InbuiltTheme)
            .constrain {
                y = SiblingConstraint(5f)
            }.onChange {
                color.setter.call(it)
                block()
            } childOf colorsContainer
    }
}