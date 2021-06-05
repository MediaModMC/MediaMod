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

package com.mediamod.core.gui.screen.impl.home

import com.mediamod.core.gui.screen.IWindowScreen
import com.mediamod.core.gui.screen.impl.home.panel.MediaModHomeScreenPanel
import com.mediamod.core.gui.screen.impl.home.panel.impl.AddonsPanel
import com.mediamod.core.gui.screen.impl.home.panel.impl.HomePanel
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.constraints.animation.Animations
import gg.essential.elementa.dsl.*
import gg.essential.elementa.transitions.SlideToTransition
import java.awt.Color

/**
 * The main screen for the MediaMod GUI
 * Different panels are added via a [MediaModHomeScreenPanel]
 *
 * @see AddonsPanel
 * @see HomePanel
 * @author Conor Byrne & Nora
 */
class MediaModHomeScreen : IWindowScreen(3) {
    private val backgroundColour = Color(64, 64, 64)
    private val selectedColour = Color(179, 179, 179)
    private val unselectedColour = Color(113, 113, 113)
    private val panels = mutableListOf<MediaModHomeScreenPanel>()
    private var transitioning = false

    private val leftContainer = UIContainer()
        .constrain {
            width = 25.percent()
            height = 100.percent()
        } childOf this

    private val rightContainer = UIContainer()
        .constrain {
            x = 25.percent()
            width = 75.percent()
            height = 100.percent()
        } childOf this

    private val leftBlock = UIBlock(backgroundColour.darker())
        .constrain {
            width = 100.percent()
            height = 100.percent()
        } childOf leftContainer

    private val rightBlock = UIBlock(backgroundColour)
        .constrain {
            width = 100.percent()
            height = 100.percent()
        } childOf rightContainer

    init {
        UIText("MediaMod", false)
            .constrain {
                x = 10.pixels()
                y = 20.pixels()
                textScale = 2.pixels()
            } childOf leftBlock
    }

    override fun afterInitialization() {
        super.afterInitialization()

        addPanel(HomePanel())
        addPanel(AddonsPanel())
    }

    private fun addPanel(panel: MediaModHomeScreenPanel) {
        val firstPanel = panels.isEmpty()

        panel.isSelected = firstPanel
        panels.add(panel)

        UIText(panel.title, false)
            .onMouseClick {
                if (!transitioning) {
                    panel.isSelected = true
                    panels.forEach { if (it.title != panel.title) it.isSelected = false }

                    updateRightPanel()
                }
            }
            .onMouseEnter {
                if (!panel.isSelected) {
                    animate {
                        setColorAnimation(Animations.IN_OUT_SIN, 0.25f, unselectedColour.darker().toConstraint())
                    }
                }
            }
            .onMouseLeave {
                if (!panel.isSelected) {
                    animate {
                        setColorAnimation(Animations.IN_OUT_SIN, 0.25f, unselectedColour.toConstraint())
                    }
                }
            }
            .constrain {
                x = 10.pixels()
                y = SiblingConstraint(if (firstPanel) 20f else 5f)
                textScale = 1.75.pixels()
                color = if (panel.isSelected) selectedColour.toConstraint() else unselectedColour.toConstraint()
            } childOf leftBlock

        if (firstPanel) updateRightPanel()
    }

    private fun updateRightPanel() {
        val provider = panels.firstOrNull { it.isSelected } ?: throw Exception("Failed to find panel")
        provider.constrain {
            width = 100.percent()
            height = 100.percent()
        }

        leftBlock.childrenOfType(UIText::class.java).forEach { text ->
            val panel = panels.firstOrNull { it.title == text.getText() } ?: return@forEach

            text.animate {
                setColorAnimation(
                    Animations.IN_OUT_SIN,
                    0.25f,
                    (if (panel.isSelected) selectedColour else unselectedColour).toConstraint()
                )
            }
        }

        val old = rightBlock.childrenOfType<MediaModHomeScreenPanel>().firstOrNull { true }
        if (old != null) {
            if (old == provider) return
            transitioning = true

            val oldId = panels.indexOf(old)
            val newId = panels.indexOf(provider)
            val slidingUp = oldId > newId

            provider childOf rightBlock
            provider.constrain { y = 0.pixels(!slidingUp, true) }
            slideTransition(slidingUp).transition(old) {
                old.constrain { y = 0.pixels(!slidingUp, true) }
                rightBlock.removeChild(old)
            }
            slideTransition(slidingUp).transition(provider) {
                provider.constrain { y = 0.pixels() }
                transitioning = false
            }
        } else rightBlock.addChild(provider)
    }

    private fun slideTransition(slidingUpwards: Boolean) =
        if (slidingUpwards) SlideToTransition.Bottom() else SlideToTransition.Top()
}
