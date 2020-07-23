package org.mediamod.mediamod.gui.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.mediamod.mediamod.gui.core.util.ButtonTooltip;

/**
 * The class that all MediaMod GUIs extend
 */
public abstract class MediaModGui extends ButtonTooltip {
    private final ResourceLocation headerResource = new ResourceLocation("mediamod", "header.png");

    private void drawHeader() {
        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);

        // Bind the texture for rendering
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.headerResource);

        // Render the album art as 35x35
        Gui.drawModalRectWithCustomSizedTexture((width / 2) - 111, 4, 0, 0, 222, 55, 222, 55);
        GlStateManager.popMatrix();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawHeader();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public boolean doesGuiPauseGame() {
        return false;
    }

    protected String getSuffix(boolean option, String label) {
        return option ? (label + ": " + EnumChatFormatting.GREEN + I18n.format("menu.guimediamod.buttons.yes")) : (label + ": " + EnumChatFormatting.RED + I18n.format("menu.guimediamod.buttons.no"));
    }

    protected int getRowPos(int rowNumber) {
        return 60 + rowNumber * 23;
    }
}
