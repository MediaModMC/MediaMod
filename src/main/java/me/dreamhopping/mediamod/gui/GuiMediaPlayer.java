package me.dreamhopping.mediamod.gui;

import me.dreamhopping.mediamod.gui.core.util.CustomButton;
import me.dreamhopping.mediamod.gui.core.util.DynamicTextureWrapper;
import me.dreamhopping.mediamod.media.MediaHandler;
import me.dreamhopping.mediamod.media.core.api.track.Track;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;
import java.net.URL;

public class GuiMediaPlayer extends GuiScreen {
    public void initGui() {
        this.buttonList.add(new CustomButton(0, (width / 2) - 100, height - 30, "Close"));

        if (MediaHandler.instance.getCurrentMediaInfo() != null) {
            GuiMediaPlayerManager.instance.currentTrack = MediaHandler.instance.getCurrentMediaInfo().track;
        }

        super.initGui();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        // Setup OpenGL
        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);

        try {
            mc.getTextureManager().bindTexture(DynamicTextureWrapper.getTexture(new URL(GuiMediaPlayerManager.instance.currentTrack.album.images[0].url)));
        } catch (Exception ignored) {
        }

        // Render the album art
        drawModalRectWithCustomSizedTexture((width / 2) - 50, 5, 0, 0, 100, 100, 100, 100);

        for (Track previousTrack : GuiMediaPlayerManager.instance.previousTracks) {
            if (!(GuiMediaPlayerManager.instance.previousTracks.indexOf(previousTrack) >= (width / 80))) {
                try {
                    mc.getTextureManager().bindTexture(DynamicTextureWrapper.getTexture(new URL(previousTrack.album.images[0].url)));
                } catch (Exception ignored) {
                }

                drawModalRectWithCustomSizedTexture(50 + (70 * GuiMediaPlayerManager.instance.previousTracks.indexOf(previousTrack)), height - 100, 0, 0, 50, 50, 50, 50);
                drawCenteredString(fontRendererObj, previousTrack.name.substring(0, (Math.min(previousTrack.name.length(), 9))), 75 + (70 * GuiMediaPlayerManager.instance.previousTracks.indexOf(previousTrack)), height - 45, -1);
            }
        }

        GlStateManager.popMatrix();

        drawCenteredString(fontRendererObj, GuiMediaPlayerManager.instance.currentTrack != null ? GuiMediaPlayerManager.instance.currentTrack.name : "No track playing", width / 2, 110, -1);
        drawCenteredString(fontRendererObj, GuiMediaPlayerManager.instance.currentTrack != null ? GuiMediaPlayerManager.instance.currentTrack.artists[0].name : "", width / 2, 120, Color.LIGHT_GRAY.getRGB());

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void actionPerformed(GuiButton button) {
        if (button.id == 0) {
            mc.displayGuiScreen(null);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

}
