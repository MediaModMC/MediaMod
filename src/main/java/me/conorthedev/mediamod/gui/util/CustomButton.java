package me.conorthedev.mediamod.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.Color;

public class CustomButton extends GuiButton {

    public CustomButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible) {
            FontRenderer fontrenderer = mc.fontRendererObj;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            this.hovered =
                    mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width
                            && mouseY < this.yPosition + this.height;

            Gui.drawRect(xPosition, yPosition, xPosition + width, yPosition + height, new Color(0, 0, 0, 175).getRGB());

            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;

            if (!this.enabled) {
                j = 10526880;
            } else if (this.hovered) {
                j = new Color(180, 180, 180).getRGB();
            }

            drawCenteredString(fontrenderer, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, j);
        }
    }
}