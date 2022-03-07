package dev.mediamod.manager

import dev.mediamod.ui.PlayerComponent
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.components.Window
import gg.essential.universal.UMatrixStack

class RenderManager {
    private val window = Window(ElementaVersion.V1)
    private lateinit var playerComponent: PlayerComponent

    fun onRenderTick(stack: UMatrixStack) {
        if (window.children.isEmpty()) {
            playerComponent = PlayerComponent()
            window.addChild(playerComponent)
        }

        window.draw(stack)
    }
}