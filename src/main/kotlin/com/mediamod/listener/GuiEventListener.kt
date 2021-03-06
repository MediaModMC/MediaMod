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

package com.mediamod.listener

import com.mediamod.ui.RenderUtils
import net.minecraftforge.client.event.RenderGameOverlayEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import java.awt.Color

/**
 * Listens to all events related to GUIs like [RenderGameOverlayEvent]
 *
 * @author Conor Byrne (dreamhopping)
 */
object GuiEventListener {
    @SubscribeEvent
    fun onRenderGameOverlay(event: RenderGameOverlayEvent) {
        if (event.type == RenderGameOverlayEvent.ElementType.HOTBAR)
            RenderUtils.renderRectangle(5, 5, 100, 50, Color.RED)
    }
}
