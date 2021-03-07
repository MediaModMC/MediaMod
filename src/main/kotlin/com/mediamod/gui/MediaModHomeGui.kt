package com.mediamod.gui

import club.sk1er.elementa.WindowScreen
import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.components.UIContainer
import club.sk1er.elementa.components.UIText
import club.sk1er.elementa.components.UIWrappedText
import club.sk1er.elementa.components.inspector.Inspector
import club.sk1er.elementa.constraints.CenterConstraint
import club.sk1er.elementa.constraints.SiblingConstraint
import club.sk1er.elementa.dsl.*
import club.sk1er.mods.core.universal.UDesktop
import com.mediamod.gui.component.UIRoundedButton
import net.minecraft.client.Minecraft
import java.awt.Color
import java.net.URI

class MediaModHomeGui : WindowScreen() {
    private var previousGuiScale: Int = 0
    private val blockColour = Color(64, 64, 64)

    private val descriptionColour = Color(142, 142, 142)
    private val descriptionText = """
MediaMod has changed since 1.0!

- New addons system
- Improved reliability and performance
- Better player customisation
- and more...
Some other text here, yea yea yea yea yea"""

    init {
        val leftBlock = UIBlock(blockColour)
            .constrain {
                x = 0.pixels()
                y = 0.pixels()
                width = 50.percent()
                height = 100.percent()
            } childOf window

        val textContainer = UIContainer().constrain {
            x = 0.pixels()
            y = 0.pixels()
            width = 100.percent()
            height = 55.percent()
        } childOf leftBlock

        UIText("Welcome to MediaMod 2.0")
            .constrain {
                x = 10.pixels()
                y = 10.pixels()
                textScale = 1.5.pixels()
            } childOf textContainer

        UIWrappedText(
            descriptionText,
            false,
        ).constrain {
            x = 10.pixels()
            y = SiblingConstraint() + 10.pixels()
            width = 100.percent()
            color = descriptionColour.toConstraint()
        } childOf textContainer

        val bottomContainer = UIContainer()
            .constrain {
                x = 0.pixels()
                y = SiblingConstraint()
                width = 100.percent()
                height = 45.percent()
            } childOf leftBlock

        val buttonContainer = UIContainer()
            .constrain {
                x = CenterConstraint()
                y = 75.percent()
                width = 100.percent()
                height = 100.percent()
            } childOf bottomContainer

        UIRoundedButton(Color(69, 204, 116), "OK", 50, 20) {
            // Reset the GUI scale and close the GUI
            Minecraft.getMinecraft().gameSettings.guiScale = previousGuiScale
            Minecraft.getMinecraft().displayGuiScreen(null)
        }.constrain {
            x = 10.pixels()
            width = 50.pixels()
            height = 20.pixels()
        } childOf buttonContainer

        UIRoundedButton(Color(92, 160, 236), "Website", 50, 20) {
            UDesktop.browse(URI("https://mediamodmc.github.io"))
        }.constrain {
            x = SiblingConstraint(5f)
            width = 50.pixels()
            height = 20.pixels()
        } childOf buttonContainer

        UIRoundedButton(Color(126, 92, 236), "Discord", 50, 20) {
            UDesktop.browse(URI("https://inv.wtf/mediamod"))
        }.constrain {
            x = SiblingConstraint(5f)
            width = 50.pixels()
            height = 20.pixels()
        } childOf buttonContainer

        Inspector(window) childOf window
    }

    override fun initScreen(width: Int, height: Int) {
        super.initScreen(width, height)

        // Force the GUI Scale to normal
        previousGuiScale = Minecraft.getMinecraft().gameSettings.guiScale
        Minecraft.getMinecraft().gameSettings.guiScale = 3
    }

    override fun onScreenClose() {
        super.onScreenClose()

        // Reset the GUI scale
        Minecraft.getMinecraft().gameSettings.guiScale = previousGuiScale
    }
}
