package me.conorthedev.mediamod.gui;

import me.conorthedev.mediamod.Settings;
import me.conorthedev.mediamod.gui.util.CustomButton;
import me.conorthedev.mediamod.gui.util.IMediaGui;
import me.conorthedev.mediamod.util.Metadata;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

import java.awt.*;
import java.io.IOException;

/**
 * The Gui for editing the MediaMod Settings
 *
 * @see net.minecraft.client.gui.GuiScreen
 */
public class GuiMediaModSettings extends GuiScreen implements IMediaGui {

    @Override
    public void initGui() {
        Settings.loadConfig();
        this.buttonList.add(new CustomButton(0, width / 2 - 100, getRowPos(0), getSuffix(Settings.ENABLED, "Enabled")));
        this.buttonList.add(new CustomButton(1, width / 2 - 100, getRowPos(1), getSuffix(Settings.ENABLED, "Show Player")));
        this.buttonList.add(new CustomButton(2, width / 2 - 100, getRowPos(2), getSuffix(Settings.ENABLED, "Enable Spotify")));
        this.buttonList.add(new CustomButton(3, width / 2 - 100, getRowPos(4), "Player Settings"));
        this.buttonList.add(new CustomButton(4, width / 2 - 100, getRowPos(5), "Services"));

        buttonList.get(0).enabled = false;
        buttonList.get(1).enabled = false;
        buttonList.get(2).enabled = false;

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
        switch (button.id) {
            case 0:
                Settings.ENABLED = !Settings.ENABLED;
                button.displayString = getSuffix(Settings.ENABLED, "Enabled");
                Settings.saveConfig();
                break;

            case 1:
                Settings.SHOW_PLAYER = !Settings.SHOW_PLAYER;
                button.displayString = getSuffix(Settings.SHOW_PLAYER, "Show Player");
                Settings.saveConfig();
                break;

            case 2:
                Settings.SPOTIFY = !Settings.SPOTIFY;
                button.displayString = getSuffix(Settings.SPOTIFY, "Enable Spotify");
                Settings.saveConfig();
                break;

            case 3:
                this.mc.displayGuiScreen(new GuiPlayerSettings());
                break;

            case 4:
                this.mc.displayGuiScreen(new GuiServices());
                break;
        }

        super.actionPerformed(button);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
