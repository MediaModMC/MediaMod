package me.conorthedev.mediamod.gui;

import me.conorthedev.mediamod.Settings;
import me.conorthedev.mediamod.gui.util.CustomButton;
import me.conorthedev.mediamod.util.Metadata;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;

import static java.awt.Color.white;

public class GuiPlayerSettings extends GuiScreen {
    @Override
    public void initGui() {
        this.buttonList.add(new CustomButton(0, width / 2 - 100, height - 50, "Back"));
        this.buttonList.add(new CustomButton(1, width / 2 - 100, getRowPos(0), getSuffix(Settings.SHOW_ALBUM_ART, "Show Album Art")));
        this.buttonList.add(new CustomButton(2, width / 2 - 100, getRowPos(1), getSuffix(Settings.AUTO_COLOR_SELECTION, "Auto Color Selection")));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        drawCenteredString(fontRendererObj, "MediaMod v" + Metadata.VERSION, width / 2, 10, Color.white.getRGB());
        drawHorizontalLine(50, width - 50, 25, Color.white.getRGB());
        drawCenteredString(fontRendererObj, "Player Settings", width / 2, 35, Color.white.getRGB());
        drawCenteredString(fontRendererObj, "Player Preview", width / 2, getRowPos(2) + 30, Color.white.getRGB());
        drawPlayer();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        if (button.id == 1) {
            Settings.SHOW_ALBUM_ART = !Settings.SHOW_ALBUM_ART;
            button.displayString = getSuffix(Settings.SHOW_ALBUM_ART, "Show Album Art");
            Settings.saveConfig();
        } else if (button.id == 2) {
            Settings.AUTO_COLOR_SELECTION = !Settings.AUTO_COLOR_SELECTION;
            button.displayString = getSuffix(Settings.AUTO_COLOR_SELECTION, "Auto Color Selection");
            Settings.saveConfig();
        } else if (button.id == 0) {
            this.mc.displayGuiScreen(new GuiMediaModSettings());
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private void drawPlayer() {
        Gui.drawRect(width / 2 - 100, height / 2 - 15, width / 2 + 100, height / 2 + 30, Color.darkGray.getRGB());

        if (Settings.SHOW_ALBUM_ART) {
            fontRendererObj.drawString("Song Name", width / 2 - 100 + 50, height / 2 - 15 + 6, -1);
            fontRendererObj.drawString("by Artist Name", width / 2 - 100 + 50, height / 2 - 15 + 15, white.darker().getRGB());

            ResourceLocation albumResource = new ResourceLocation("mediamod", "no_album_art.png");

            // Bind the texture for rendering
            Minecraft.getMinecraft().getTextureManager().bindTexture(albumResource);
            // Render the album art as 35x35
            Gui.drawModalRectWithCustomSizedTexture(width / 2 - 100 + 10, height / 2 - 10, 0, 0, 35, 35, 35, 35);
        } else {
            fontRendererObj.drawString("Song Name", width / 2 - 100 + 5, height / 2 - 15 + 6, -1);
            fontRendererObj.drawString("by Artist Name", width / 2 - 100 + 5, height / 2 - 15 + 15, white.darker().getRGB());
        }
    }

    private String getSuffix(boolean option, String label) {
        return option ? (label + ": " + EnumChatFormatting.GREEN + "YES") : (label + ": " + EnumChatFormatting.RED + "NO");
    }

    private int getRowPos(int rowNumber) {
        return 55 + rowNumber * 23;
    }
}
