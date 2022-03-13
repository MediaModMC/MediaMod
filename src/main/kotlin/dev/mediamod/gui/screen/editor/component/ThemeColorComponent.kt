package dev.mediamod.gui.screen.editor.component

import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.constraints.ConstantColorConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.state.BasicState
import gg.essential.vigilance.gui.settings.ColorComponent
import java.awt.Color

class ThemeColorComponent(themeColor: Color, text: String) : UIContainer() {
    private val colorState = BasicState(themeColor)

    init {
        constrain {
            width = 100.percent()
            height = ChildBasedMaxSizeConstraint()
        }

        UIBlock()
            .constrain {
                color = ConstantColorConstraint().bindColor(colorState)
                y = CenterConstraint()
                width = 8.pixels()
                height = 8.pixels()
            } childOf this

        UIText(text, false)
            .constrain {
                x = SiblingConstraint(5f)
                y = CenterConstraint()
            } childOf this

        val colorPicker = ColorComponent(themeColor, false)
            .constrain {
                x = 0.pixels(true)
                y = CenterConstraint()
            } childOf this

        colorPicker.onValueChange {
            if (it !is Color) return@onValueChange
            colorState.set(it)
        }
    }
}