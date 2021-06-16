/*
 *     MediaMod is a mod for Minecraft which displays information about your current track in-game
 *     Copyright (C) 2021 Conor Byrne
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.mediamod.core.gui.screen.impl.home.panel.impl

import com.mediamod.core.gui.component.UIRoundedButton
import com.mediamod.core.gui.screen.impl.home.panel.MediaModHomeScreenPanel
import com.mediamod.core.theme.MediaModThemeRegistry
import gg.essential.elementa.UIComponent
import gg.essential.elementa.components.*
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.constraints.CramSiblingConstraint
import gg.essential.elementa.constraints.FillConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import java.awt.Color

/**
 * The panel that displays information about the themes available to the user
 *
 * @author Conor Byrne
 */
class ThemesPanel : MediaModHomeScreenPanel("Themes") {
    init {
        UIText("Themes", false)
            .constrain {
                x = 20.pixels()
                y = 20.pixels()
                color = titleColour
                textScale = 2.pixels()
            } childOf this

        val addonsContainer =
            ScrollComponent("You have no themes installed! Open the .minecraft/mediamod/themes directory to add some.")
                .constrain {
                    x = 20.pixels()
                    y = SiblingConstraint(10f)
                    width = 100.percent()
                    height = FillConstraint() - 35.pixels()
                } childOf this

        MediaModThemeRegistry.loadedThemes.forEachIndexed { index, theme ->
            ThemeComponent(
                theme.name ?: "Unnamed theme",
                theme.description ?: "An awesome MediaMod theme!",
                index == 0
            ).constrain {
                x = CramSiblingConstraint(20f)
                y = CramSiblingConstraint(10f)
                height = 25.percent()
                width = 35.percent()
            } childOf addonsContainer
        }
    }

    class ThemeComponent(
        name: String,
        description: String,
        applied: Boolean = false
    ) : UIComponent() {
        private val backgroundColour = Color(64, 64, 64).brighter()
        private val titleColour = Color(198, 198, 198)

        init {
            val backgroundBlock = UIRoundedRectangle(5f)
                .constrain {
                    width = 100.percent()
                    height = 100.percent()
                    color = backgroundColour.toConstraint()
                } childOf this

            val textContainer = UIContainer()
                .constrain {
                    width = 100.percent()
                    height = 65.percent()
                } childOf backgroundBlock

            val buttonContainer = UIContainer()
                .constrain {
                    y = SiblingConstraint(5f)
                    width = 100.percent()
                    height = 35.percent() - 5.pixels()
                } childOf backgroundBlock

            UIText(name, false)
                .constrain {
                    x = 5.pixels()
                    y = 5.pixels()
                    color = titleColour.toConstraint()
                    textScale = 1.25.pixels()
                } childOf textContainer

            if (applied) {
                val rectangle = UIRoundedRectangle(3f)
                    .constrain {
                        x = SiblingConstraint(5f)
                        y = 5.pixels()
                        width = 35.pixels()
                        height = 10.pixels()
                        color = Color(69, 204, 116).toConstraint()
                    } childOf textContainer

                UIText("Applied", false)
                    .constrain {
                        x = CenterConstraint()
                        y = CenterConstraint()
                        textScale = 0.75.pixels()
                    } childOf rectangle
            }

            UIWrappedText(description, false, trimText = true)
                .constrain {
                    x = 5.pixels()
                    y = SiblingConstraint(5f)
                    width = 100.percent() - 10.pixels()
                    height = FillConstraint() - 5.pixels()
                    color = titleColour.darker().toConstraint()
                } childOf textContainer

            UIRoundedButton(Color(69, 204, 116), "Apply", 45, 15) {

            }.constrain {
                x = 5.pixels()
                width = 45.pixels()
                height = 15.pixels()
            } childOf buttonContainer

            UIRoundedButton(Color(92, 160, 236), "Preview", 45, 15) {

            }.constrain {
                x = SiblingConstraint(5f)
                width = 45.pixels()
                height = 15.pixels()
            } childOf buttonContainer

            UIRoundedButton(Color(126, 92, 236), "Delete", 45, 15) {

            }.constrain {
                x = SiblingConstraint(5f)
                width = 45.pixels()
                height = 15.pixels()
            } childOf buttonContainer
        }
    }
}
