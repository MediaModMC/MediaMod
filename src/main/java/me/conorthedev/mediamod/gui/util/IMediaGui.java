package me.conorthedev.mediamod.gui.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public interface IMediaGui {
    ResourceLocation iconResource = new ResourceLocation("mediamod", "mediamod.png");
    ResourceLocation headerResource = new ResourceLocation("mediamod", "header.png");

    default String getSuffix(boolean option, String label) {
        return option ? (label + ": " + EnumChatFormatting.GREEN + I18n.format("menu.guimediamod.buttons.yes")) : (label + ": " + EnumChatFormatting.RED + I18n.format("menu.guimediamod.buttons.no"));
    }

    default int getRowPos(int rowNumber) {
        return 60 + rowNumber * 23;
    }

    default void drawHeader(int width, int height) {
        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);

        // Bind the texture for rendering
        Minecraft.getMinecraft().getTextureManager().bindTexture(this.headerResource);

        // Render the album art as 35x35
        Gui.drawModalRectWithCustomSizedTexture(width / 2 - 111, height / 2 - 110, 0, 0, 222, 55, 222, 55);
        GlStateManager.popMatrix();
    }
}
