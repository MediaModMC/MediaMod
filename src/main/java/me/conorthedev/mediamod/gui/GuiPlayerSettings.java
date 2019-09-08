package me.conorthedev.mediamod.gui;

import me.conorthedev.mediamod.Settings;
import me.conorthedev.mediamod.gui.util.CustomButton;
import me.conorthedev.mediamod.gui.util.IMediaGui;
import me.conorthedev.mediamod.util.Metadata;
import me.conorthedev.mediamod.util.Multithreading;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

import static java.awt.Color.white;

class GuiPlayerSettings extends GuiScreen implements IMediaGui {
    @Override
    public void initGui() {
        this.buttonList.add(new CustomButton(0, width / 2 - 100, height - 50, "Back"));
        this.buttonList.add(new CustomButton(1, width / 2 - 100, getRowPos(0), getSuffix(Settings.SHOW_ALBUM_ART, "Show Album Art")));
        this.buttonList.add(new CustomButton(2, width / 2 - 100, getRowPos(1), getSuffix(Settings.AUTO_COLOR_SELECTION, "Auto Color Selection")));
        this.buttonList.add(new CustomButton(3, width / 2 - 100, getRowPos(2), getSuffix(Settings.MODERN_PLAYER_STYLE, "Modern Player")));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        drawCenteredString(fontRendererObj, "MediaMod v" + Metadata.VERSION, width / 2, 10, Color.white.getRGB());
        drawHorizontalLine(50, width - 50, 25, Color.white.getRGB());
        drawCenteredString(fontRendererObj, "Player Settings", width / 2, 35, Color.white.getRGB());

        drawPlayer();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiMediaModSettings());
                break;

            case 1:
                Settings.SHOW_ALBUM_ART = !Settings.SHOW_ALBUM_ART;
                button.displayString = getSuffix(Settings.SHOW_ALBUM_ART, "Show Album Art");
                Multithreading.runAsync(Settings::saveConfig);
                break;

            case 2:
                Settings.AUTO_COLOR_SELECTION = !Settings.AUTO_COLOR_SELECTION;
                button.displayString = getSuffix(Settings.AUTO_COLOR_SELECTION, "Auto Color Selection");
                Multithreading.runAsync(Settings::saveConfig);
                break;

            case 3:
                Settings.MODERN_PLAYER_STYLE = !Settings.MODERN_PLAYER_STYLE;
                button.displayString = getSuffix(Settings.MODERN_PLAYER_STYLE, "Modern Player");
                Multithreading.runAsync(Settings::saveConfig);
                break;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void drawPlayer() {
        Color color = Color.gray;

        if(Settings.MODERN_PLAYER_STYLE) {
            // Draw the outline of the player
            Gui.drawRect(width / 2 - 101, height / 2 - 16, width / 2 + 101, height / 2 + 31, new Color(0, 0, 0, 75).getRGB());
        }

        if (Settings.AUTO_COLOR_SELECTION && Settings.SHOW_ALBUM_ART) {
            // Draw the background of the player
            // Note: .darker()*2 because it just looks static otherwise
            Gui.drawRect(width / 2 - 100, height / 2 - 15, width / 2 + 100, height / 2 + 30, color.darker().darker().getRGB());
        } else {
            // Draw the background of the player
            Gui.drawRect(150, 5, 5, 50, Color.darkGray.getRGB());
        }

        int textX = width / 2 - 100 + 50;

        if(Settings.MODERN_PLAYER_STYLE) {
            // Draw outline
            Gui.drawRect(textX - 1, height / 2 + 12, textX + 101, height / 2 + 23, new Color(0, 0, 0, 75).getRGB());
        }

        Gui.drawRect(textX, height / 2 + 13, textX + 100, height / 2 + 22, Color.darkGray.darker().getRGB());

        if (Settings.AUTO_COLOR_SELECTION && Settings.SHOW_ALBUM_ART) {
            if(Settings.MODERN_PLAYER_STYLE) {
                drawGradientRect(textX, height / 2 + 13, textX + 50, height / 2 + 22, color.getRGB(), color.darker().getRGB());
            } else {
                Gui.drawRect(textX, height / 2 + 13, textX + 50, height / 2 + 22, color.getRGB());
            }
        } else {
            Gui.drawRect(textX, height / 2 + 13, textX + 50, height / 2 + 22, Color.green.getRGB());
        }

        if (Settings.SHOW_ALBUM_ART) {
            fontRendererObj.drawString("Song Name", textX, height / 2 - 15 + 6, -1);
            fontRendererObj.drawString("by Artist Name", textX, height / 2 - 15 + 15, white.darker().getRGB());

            ResourceLocation albumResource = new ResourceLocation("mediamod", "mediamod.png");
            if(Settings.MODERN_PLAYER_STYLE) {
                // Draw outline
                Gui.drawRect(width / 2 - 100 + 46, height / 2 - 11, width / 2 - 100 + 9, height / 2 + 26, new Color(0, 0, 0, 75).getRGB());
            }

            GlStateManager.pushMatrix();
            GlStateManager.color(1, 1, 1, 1);

            // Bind the texture for rendering
            mc.getTextureManager().bindTexture(albumResource);

            // Render the album art as 35x35
            Gui.drawModalRectWithCustomSizedTexture(width / 2 - 100 + 10, height / 2 - 10, 0, 0, 35, 35, 35, 35);
            GlStateManager.popMatrix();
        } else {
            fontRendererObj.drawString("Song Name", width / 2 - 100 + 5, height / 2 - 15 + 6, -1);
            fontRendererObj.drawString("by Artist Name", width / 2 - 100 + 5, height / 2 - 15 + 15, white.darker().getRGB());
        }
    }
}
