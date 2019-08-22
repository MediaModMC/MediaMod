package me.conorthedev.mediamod.gui;

import me.conorthedev.mediamod.Settings;
import me.conorthedev.mediamod.gui.util.CustomButton;
import me.conorthedev.mediamod.util.Metadata;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.io.IOException;

/**
 * The Gui for editing the MediaMod Settings
 *
 * @see net.minecraft.client.gui.GuiScreen
 */
public class GuiMediaModSettings extends GuiScreen {
    @Override
    public void initGui() {
        Settings.loadConfig();
        this.buttonList.add(new CustomButton(0, width / 2 - 100, getRowPos(0), getSuffix(Settings.ENABLED, "Enabled")));
        this.buttonList.add(new CustomButton(1, width / 2 - 100, getRowPos(1), getSuffix(Settings.ENABLED, "Show Player")));
        this.buttonList.add(new CustomButton(2, width / 2 - 100, getRowPos(2), getSuffix(Settings.ENABLED, "Enable Spotify")));
        this.buttonList.add(new CustomButton(3, width / 2 - 100, getRowPos(4), "Player Settings"));
        this.buttonList.add(new CustomButton(4, width / 2 - 100, getRowPos(5), "Services"));
        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();
        drawCenteredString(fontRendererObj, "MediaMod v" + Metadata.VERSION, width / 2, 10, Color.white.getRGB());
        drawHorizontalLine(50, width - 50, 25, Color.white.getRGB());
        drawCenteredString(fontRendererObj, "General Settings", width / 2, 35, Color.white.getRGB());
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        if (button.id == 0) {
            Settings.ENABLED = !Settings.ENABLED;
            button.displayString = getSuffix(Settings.ENABLED, "Enabled");
            Settings.saveConfig();
        } else if (button.id == 1) {
            Settings.SHOW_PLAYER = !Settings.SHOW_PLAYER;
            button.displayString = getSuffix(Settings.SHOW_PLAYER, "Show Player");
            Settings.saveConfig();
        } else if (button.id == 2) {
            Settings.SPOTIFY = !Settings.SPOTIFY;
            button.displayString = getSuffix(Settings.SPOTIFY, "Enable Spotify");
            Settings.saveConfig();
        } else if (button.id == 3) {
            this.mc.displayGuiScreen(new GuiPlayerSettings());
        } else if (button.id == 4) {
            this.mc.displayGuiScreen(new GuiServices());
        }

        super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    private String getSuffix(boolean option, String label) {
        return option ? (label + ": " + EnumChatFormatting.GREEN + "YES") : (label + ": " + EnumChatFormatting.RED + "NO");
    }

    private int getRowPos(int rowNumber) {
        return 55 + rowNumber * 23;
    }
}
