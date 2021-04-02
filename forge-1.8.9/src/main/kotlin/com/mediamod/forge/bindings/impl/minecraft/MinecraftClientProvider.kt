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

package com.mediamod.forge.bindings.impl.minecraft

import club.sk1er.elementa.WindowScreen
import club.sk1er.elementa.dsl.childOf
import club.sk1er.elementa.dsl.constrain
import club.sk1er.elementa.dsl.percent
import com.mediamod.core.bindings.minecraft.MinecraftClient
import com.mediamod.core.bindings.screen.IWindowScreen
import net.minecraft.client.Minecraft
import java.io.File

class MinecraftClientProvider : MinecraftClient {
    override val mcDataDir: File = Minecraft.getMinecraft().mcDataDir

    override fun openScreen(screen: IWindowScreen?) {
        if (screen == null) return Minecraft.getMinecraft().displayGuiScreen(null)

        Minecraft.getMinecraft().displayGuiScreen(object : WindowScreen() {
            init {
                screen.constrain {
                    width = 100.percent()
                    height = 100.percent()
                } childOf window
            }

            override fun onResize(mcIn: Minecraft?, w: Int, h: Int) {
                super.onResize(mcIn, w, h)
                screen.onResize(w, h)
            }

            override fun onScreenClose() {
                super.onScreenClose()
                screen.onClose()
            }
        })
    }
}
