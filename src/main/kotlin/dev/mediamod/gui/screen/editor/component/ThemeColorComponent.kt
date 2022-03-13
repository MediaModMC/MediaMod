package dev.mediamod.gui.screen.editor.component

import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.ChildBasedMaxSizeConstraint
import gg.essential.elementa.dsl.childOf
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.vigilance.gui.settings.ColorComponent
import java.awt.Color

class ThemeColorComponent(themeColor: Color, text: String) : UIContainer() {
    init {
        constrain {
            width = 100.percent()
            height = ChildBasedMaxSizeConstraint()
        }

        UIText(text)
            .constrain {
                y = CenterConstraint()
            } childOf this

        ColorComponent(themeColor, false)
            .constrain {
                x = 0.pixels(true)
                y = CenterConstraint()
            } childOf this
    }
}