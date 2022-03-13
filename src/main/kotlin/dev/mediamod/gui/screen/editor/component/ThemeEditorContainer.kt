package dev.mediamod.gui.screen.editor.component

import dev.mediamod.theme.Theme
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.state.BasicState

class ThemeEditorContainer : UIContainer() {
    val theme = BasicState<Theme?>(null)

    private val themeNameState = BasicState("")

    init {
        theme.onSetValue {
            it?.let { themeNameState.set(it.name) }
        }

        UIText()
            .bindText(themeNameState)
            .constrain {
                x = 15.pixels()
                y = 15.pixels()
                textScale = 1.25f.pixels()
            } childOf this
    }
}