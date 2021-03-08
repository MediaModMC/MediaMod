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

package com.mediamod.gui

import club.sk1er.elementa.WindowScreen
import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIContainer
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.constraints.animation.Animations
import club.sk1er.elementa.dsl.*
import com.mediamod.gui.panel.MediaModHomeScreenPanel
import com.mediamod.gui.panel.impl.AddonsPanel
import com.mediamod.gui.panel.impl.HomePanel
import net.minecraft.client.Minecraft
import java.awt.Color
import java.lang.Exception

class MediaModHomeScreen(private var previousGuiScale: Int = 0) : WindowScreen() {
    private val backgroundColour = Color(64, 64, 64)
    private val selectedColour = Color(179, 179, 179)
    private val unselectedColour = Color(113, 113, 113)
    private val panels = mutableListOf<MediaModHomeScreenPanel>()

    private val leftContainer = UIContainer()
        .constrain {
            width = 30.percent()
            height = 100.percent()
        } childOf window

    private val rightContainer = UIContainer()
        .constrain {
            x = 30.percent()
            width = 70.percent()
            height = 100.percent()
        } childOf window

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
                textScale = 2.25.pixels()
            } childOf leftBlock

        addPanel(HomePanel())
        addPanel(AddonsPanel())

        // Force the GUI Scale to normal
        Minecraft.getMinecraft().gameSettings.guiScale = 3
    }

    override fun onScreenClose() {
        super.onScreenClose()

        // Reset the GUI scale
        Minecraft.getMinecraft().gameSettings.guiScale = previousGuiScale
    }

    private fun addPanel(panel: MediaModHomeScreenPanel) {
        val firstPanel = panels.isEmpty()

        panel.isSelected = firstPanel
        panels.add(panel)

        UIText(panel.title, false)
            .onMouseClick {
                panel.isSelected = true
                panels.forEach { if (it.title != panel.title) it.isSelected = false }

                updateRightPanel()
            }
            .onMouseEnter {
                animate {
                    val color = if (panel.isSelected) selectedColour else unselectedColour
                    setColorAnimation(Animations.IN_OUT_SIN, 0.25f, color.darker().toConstraint())
                }
            }
            .onMouseLeave {
                animate {
                    setColorAnimation(
                        Animations.IN_OUT_SIN,
                        0.25f,
                        (if (panel.isSelected) selectedColour else unselectedColour).toConstraint()
                    )
                }
            }
            .constrain {
                x = 10.pixels()
                y = SiblingConstraint(if (firstPanel) 20f else 5f)
                textScale = 2.pixels()
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

        rightBlock.clearChildren()
        rightBlock.addChild(provider)
    }
}
