package com.mediamod.forge.bindings.impl.screen

import club.sk1er.elementa.WindowScreen
import com.mediamod.core.bindings.screen.IWindowScreen
import net.minecraft.client.Minecraft

class IWindowScreenProvider : IWindowScreen, WindowScreen() {
    override fun onResize(mcIn: Minecraft?, w: Int, h: Int) {
        super<WindowScreen>.onResize(mcIn, w, h)
        this.onResize(w, h)
    }

    override fun onScreenClose() {
        super.onScreenClose()
        this.onClose()
    }
}