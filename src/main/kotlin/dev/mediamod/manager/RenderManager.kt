package dev.mediamod.manager

import dev.mediamod.config.Configuration
import dev.mediamod.gui.hud.PlayerComponent
import dev.mediamod.gui.screen.RepositionScreen
import dev.mediamod.gui.screen.editor.ThemeEditorScreen
import gg.essential.elementa.ElementaVersion
import gg.essential.elementa.components.Window
import gg.essential.elementa.dsl.constrain
import gg.essential.elementa.dsl.pixels
import gg.essential.universal.UMatrixStack
import gg.essential.universal.UScreen

class RenderManager {
    private val window = Window(ElementaVersion.V1)
    private lateinit var playerComponent: PlayerComponent

    fun onRenderTick(stack: UMatrixStack) {
        if (UScreen.currentScreen is RepositionScreen || UScreen.currentScreen is ThemeEditorScreen)
            return

        if (window.children.isEmpty()) {
            playerComponent = PlayerComponent()
                .constrain {
                    x = Configuration.playerX.pixels()
                    y = Configuration.playerY.pixels()

                    width = 150.pixels()
                    height = 50.pixels()
                }

            window.addChild(playerComponent)
        } else {
            // TODO: State
            playerComponent.setX(Configuration.playerX.pixels())
            playerComponent.setY(Configuration.playerY.pixels())
        }

        window.draw(stack)
    }
}