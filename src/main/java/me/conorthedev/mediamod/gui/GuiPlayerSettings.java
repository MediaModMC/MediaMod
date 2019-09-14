package me.conorthedev.mediamod.gui;

import me.conorthedev.mediamod.Settings;
import me.conorthedev.mediamod.gui.util.ButtonTooltip;
import me.conorthedev.mediamod.gui.util.CustomButton;
import me.conorthedev.mediamod.gui.util.IMediaGui;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

import static java.awt.Color.white;

class GuiPlayerSettings extends ButtonTooltip implements IMediaGui {
    @Override
    public void initGui() {
        Settings.loadConfig();
        this.buttonList.add(new CustomButton(0, width / 2 - 100, height - 50, "Back"));

        this.buttonList.add(new CustomButton(1, width / 2 - 120, getRowPos(1), getSuffix(Settings.SHOW_ALBUM_ART, "Show Album Art")));
        this.buttonList.add(new CustomButton(2, width / 2 + 10, getRowPos(1), getSuffix(Settings.AUTO_COLOR_SELECTION, "Color Selection")));
        this.buttonList.add(new CustomButton(3, width / 2 - 120, getRowPos(2), getSuffix(Settings.MODERN_PLAYER_STYLE, "Modern Player")));

        for (GuiButton button : buttonList) {
            if (button.id != 0) {
                button.width = 120;
            }
        }

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);

        // Bind the texture for rendering
        mc.getTextureManager().bindTexture(this.headerResource);

        // Render the header
        Gui.drawModalRectWithCustomSizedTexture(width / 2 - 111, 15, 0, 0, 222, 55, 222, 55);
        GlStateManager.popMatrix();

        drawPlayer();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected String getButtonTooltip(int buttonId) {
        switch (buttonId) {
            case 1:
                return "Toggling this OFF will disable album art, this affects auto colour selection";
            case 2:
                return "Sets the background colour of the player to the most prominant colour in the album art";
            case 3:
                return "Enables a new player design that involves gradients and shadows, designed by ScottehBoeh";
        }
        return null;
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
                break;

            case 2:
                Settings.AUTO_COLOR_SELECTION = !Settings.AUTO_COLOR_SELECTION;
                button.displayString = getSuffix(Settings.AUTO_COLOR_SELECTION, "Color Selection");
                break;

            case 3:
                Settings.MODERN_PLAYER_STYLE = !Settings.MODERN_PLAYER_STYLE;
                button.displayString = getSuffix(Settings.MODERN_PLAYER_STYLE, "Modern Player");
                break;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        Settings.saveConfig();
    }

    private void drawPlayer() {
        Color color = Color.gray;

        if(Settings.MODERN_PLAYER_STYLE) {
            // Draw the outline of the player
            Gui.drawRect(width / 2 - 101, height / 2 + 19, width / 2 + 101, height / 2 + 66, new Color(0, 0, 0, 75).getRGB());
        }

        if (Settings.AUTO_COLOR_SELECTION && Settings.SHOW_ALBUM_ART) {
            // Draw the background of the player
            // Note: .darker()*2 because it just looks static otherwise
            Gui.drawRect(width / 2 - 100, height / 2 + 20, width / 2 + 100, height / 2 + 65, color.darker().darker().getRGB());
        } else {
            // Draw the background of the player
            Gui.drawRect(width / 2 - 100, height / 2 + 20, width / 2 + 100, height / 2 + 65, Color.darkGray.getRGB());
        }

        int textX = width / 2 - 100 + 50;

        if(Settings.MODERN_PLAYER_STYLE) {
            // Draw outline
            Gui.drawRect(textX - 1, height / 2 + 45, textX + 101, height / 2 + 56, new Color(0, 0, 0, 75).getRGB());
        }

        Gui.drawRect(textX, height / 2 + 46, textX + 100, height / 2 + 55, Color.darkGray.darker().getRGB());

        if (Settings.AUTO_COLOR_SELECTION && Settings.SHOW_ALBUM_ART) {
            if(Settings.MODERN_PLAYER_STYLE) {
                drawGradientRect(textX, height / 2 + 46, textX + 50, height / 2 + 55, color.getRGB(), color.darker().getRGB());
            } else {
                Gui.drawRect(textX, height / 2 + 46, textX + 50, height / 2 + 55, color.getRGB());
            }
        } else {
            Gui.drawRect(textX, height / 2 + 13, textX + 50, height / 2 + 22, Color.green.getRGB());
        }

        if (Settings.SHOW_ALBUM_ART) {
            fontRendererObj.drawString("Song Name", textX, height / 2 + 26, -1);
            fontRendererObj.drawString("by Artist Name", textX, height / 2 + 35, white.darker().getRGB());

            ResourceLocation albumResource = new ResourceLocation("mediamod", "mediamod.png");
            if(Settings.MODERN_PLAYER_STYLE) {
                // Draw outline
                Gui.drawRect(width / 2 - 100 + 46, height / 2 + 24, width / 2 - 100 + 9, height / 2 + 61, new Color(0, 0, 0, 75).getRGB());
            }

            GlStateManager.pushMatrix();
            GlStateManager.color(1, 1, 1, 1);

            // Bind the texture for rendering
            mc.getTextureManager().bindTexture(albumResource);

            // Render the album art as 35x35
            Gui.drawModalRectWithCustomSizedTexture(width / 2 - 90, height / 2 + 25, 0, 0, 35, 35, 35, 35);
            GlStateManager.popMatrix();
        } else {
            fontRendererObj.drawString("Song Name", width / 2 - 100 + 5, height / 2 - 15 + 6, -1);
            fontRendererObj.drawString("by Artist Name", width / 2 - 100 + 5, height / 2 - 15 + 15, white.darker().getRGB());
        }
    }
}
