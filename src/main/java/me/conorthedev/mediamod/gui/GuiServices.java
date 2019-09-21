package me.conorthedev.mediamod.gui;

import me.conorthedev.mediamod.Settings;
import me.conorthedev.mediamod.gui.util.ButtonTooltip;
import me.conorthedev.mediamod.gui.util.CustomButton;
import me.conorthedev.mediamod.gui.util.IMediaGui;
import me.conorthedev.mediamod.media.spotify.SpotifyHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.io.IOException;

class GuiServices extends ButtonTooltip implements IMediaGui {
    @Override
    public void initGui() {
        Settings.loadConfig();

        this.buttonList.add(new CustomButton(0, width / 2 - 100, height - 50, "Back"));

        if (!SpotifyHandler.logged) {
            this.buttonList.add(new CustomButton(1, width / 2 - 100, height / 2 - 35, "Login to Spotify"));
        } else {
            this.buttonList.add(new CustomButton(2, width / 2 - 100, height / 2 - 35, "Logout of all"));
        }

        this.buttonList.add(new CustomButton(3, width / 2 - 100, height / 2 - 10, getSuffix(Settings.EXTENSION_ENABLED, "Use Browser Extension")));

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
        Gui.drawModalRectWithCustomSizedTexture(width / 2 - 111, height / 2 - 110, 0, 0, 222, 55, 222, 55);
        GlStateManager.popMatrix();

        if (!SpotifyHandler.logged) {
            drawCenteredString(fontRendererObj, "Spotify not logged in! Please login below", width / 2, height / 2 - 53, Color.red.getRGB());
        } else {
            drawCenteredString(fontRendererObj, "Spotify is logged in!", width / 2, height / 2 - 53, Color.green.getRGB());
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected String getButtonTooltip(int buttonId) {
        if (buttonId == 3) {
            return "Disables or Enables the Browser Extension";
        } else {
            return null;
        }
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
            case 3:
                Settings.EXTENSION_ENABLED = !Settings.EXTENSION_ENABLED;
                button.displayString = getSuffix(Settings.EXTENSION_ENABLED, "Use Browser Extension");
                break;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
