package dev.mediamod.manager

import dev.mediamod.ui.PlayerComponent
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.components.Window
import gg.essential.universal.UMatrixStack
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback

class RenderManager {
    private val window = Window(ElementaVersion.V1)
    private lateinit var playerComponent: PlayerComponent

    fun init() {
        HudRenderCallback.EVENT.register(HudRenderCallback { matrixStack, _ ->
            if (window.children.isEmpty()) {
                playerComponent = PlayerComponent()
                window.addChild(playerComponent)
            }

            window.draw(UMatrixStack(matrixStack))
        })
    }
}