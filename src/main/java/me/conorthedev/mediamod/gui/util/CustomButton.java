package me.conorthedev.mediamod.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;

@ParametersAreNonnullByDefault
public class CustomButton extends GuiButton {

    public CustomButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
    }

    public CustomButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            this.hovered =
                    mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width
                            && mouseY < this.y + this.height;

            Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, new Color(0, 0, 0, 175).getRGB());

            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;

            if (!this.enabled) {
                j = 10526880;
            } else if (this.hovered) {
                j = new Color(180, 180, 180).getRGB();
            }

            drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
        }
    }
}