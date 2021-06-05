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

package com.mediamod.core.gui.screen.impl.home.panel

import com.mediamod.core.gui.screen.impl.home.MediaModHomeScreen
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.percent
import gg.essential.elementa.dsl.pixels
import gg.essential.elementa.dsl.toConstraint
import java.awt.Color

/**
 * A panel to be displayed on the [MediaModHomeScreen]
 *
 * @author Conor Byrne & Nora
 */
abstract class MediaModHomeScreenPanel(val title: String) : UIContainer() {
    protected val titleColour = Color(198, 198, 198).toConstraint()
    var isSelected: Boolean = false

    init {
        constrain {
            x = 0.pixels()
            y = 0.pixels()
            height = 100.percent()
            width = 100.percent()
        }
    }
}
