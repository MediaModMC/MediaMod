package me.conorthedev.mediamod.gui;

import me.conorthedev.mediamod.Settings;
import me.conorthedev.mediamod.gui.util.CustomButton;
import me.conorthedev.mediamod.gui.util.IMediaGui;
import me.conorthedev.mediamod.media.spotify.SpotifyHandler;
import me.conorthedev.mediamod.util.Metadata;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.io.IOException;

class GuiServices extends GuiScreen implements IMediaGui {
    @Override
    public void initGui() {
        Settings.loadConfig();

        this.buttonList.add(new CustomButton(0, width / 2 - 100, height - 50, "Back"));

        if (!SpotifyHandler.logged) {
            this.buttonList.add(new CustomButton(1, width / 2 - 100, getRowPos(1), "Login to Spotify"));
        } else {
            this.buttonList.add(new CustomButton(2, width / 2 - 100, height - 75, "Logout of all"));
        }

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        drawCenteredString(fontRendererObj, "MediaMod v" + Metadata.VERSION, width / 2, 10, -1);
        drawHorizontalLine(50, width - 50, 25, -1);
        drawCenteredString(fontRendererObj, "Services", width / 2, 35, -1);

        if (!SpotifyHandler.logged) {
            drawCenteredString(fontRendererObj, "Spotify not logged in! Please login below", width / 2, 50, Color.red.getRGB());
        } else {
            drawCenteredString(fontRendererObj, "Connected Accounts:", width / 2, 50, Color.green.getRGB());
            drawCenteredString(fontRendererObj, "Spotify", width / 2, 60, -1);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void onGuiClosed() {
        Settings.saveConfig();
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiMediaModSettings());
                break;

            case 1:
                this.mc.displayGuiScreen(null);
                this.mc.thePlayer.addChatComponentMessage(new ChatComponentText(EnumChatFormatting.RED + "[" + EnumChatFormatting.WHITE + "MediaMod" + EnumChatFormatting.RED + "] " + "Opening browser with instructions on what to do, when it opens log in with your Spotify Account and press 'Agree'"));
                SpotifyHandler.connectSpotify();
                break;

            case 2:
                SpotifyHandler.spotifyApi = null;
                SpotifyHandler.logged = false;
                this.mc.displayGuiScreen(new GuiServices());
                break;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
