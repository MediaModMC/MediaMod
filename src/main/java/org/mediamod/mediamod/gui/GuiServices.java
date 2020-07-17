package org.mediamod.mediamod.gui;

import org.mediamod.mediamod.MediaMod;
import org.mediamod.mediamod.config.Settings;
import org.mediamod.mediamod.gui.util.ButtonTooltip;
import org.mediamod.mediamod.gui.util.CustomButton;
import org.mediamod.mediamod.gui.util.IMediaGui;
import org.mediamod.mediamod.media.services.spotify.SpotifyService;
import org.mediamod.mediamod.util.ChatColor;
import org.mediamod.mediamod.util.PlayerMessager;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

class GuiServices extends ButtonTooltip implements IMediaGui {
    @Override
    public void initGui() {
        Settings.loadConfig();

        this.buttonList.add(new CustomButton(0, width / 2 - 100, height - 50, I18n.format("menu.guiplayerpositioning.buttons.back.name")));

        if (!SpotifyService.isLoggedIn()) {
            this.buttonList.add(new CustomButton(1, width / 2 - 100, height / 2 - 35, I18n.format("menu.guiservices.buttons.loginSpotify.name")));
        } else {
            this.buttonList.add(new CustomButton(2, width / 2 - 100, height / 2 - 35, I18n.format("menu.guiservices.buttons.logoutSpotify.name")));
            this.buttonList.add(new CustomButton(4, width / 2 - 100, height / 2 + 15, getSuffix(Settings.SAVE_SPOTIFY_TOKEN, I18n.format("menu.guiservices.buttons.saveSpotifyToken.name"))));
        }

        this.buttonList.add(new CustomButton(3, width / 2 - 100, height / 2 - 10, getSuffix(Settings.EXTENSION_ENABLED, I18n.format("menu.guiservices.buttons.useBrowserExt.name"))));

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

        if (!SpotifyService.isLoggedIn()) {
            drawCenteredString(fontRendererObj, I18n.format("menu.guiservices.text.spotifyNotLogged.name"), width / 2, height / 2 - 53, Color.red.getRGB());
        } else {
            drawCenteredString(fontRendererObj, I18n.format("menu.guiservices.text.spotifyLogged.name"), width / 2, height / 2 - 53, Color.green.getRGB());
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected String getButtonTooltip(int buttonId) {
        if (buttonId == 3) {
            return I18n.format("menu.guiservices.buttons.useBrowserExt.tooltip");
        } else if (buttonId == 4) {
            return I18n.format("menu.guiservices.buttons.saveSpotifyToken.tooltip");
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
                PlayerMessager.sendMessage("&cOpening browser with instructions on what to do, when it opens log in with your Spotify Account and press 'Agree'");

                Desktop desktop = Desktop.getDesktop();
                String spotifyUrl = "https://accounts.spotify.com/authorize?client_id=" + MediaMod.INSTANCE.spotifyClientID + "&response_type=code&redirect_uri=http%3A%2F%2Flocalhost:9103%2Fcallback%2F&scope=user-read-playback-state%20user-read-currently-playing%20user-modify-playback-state&state=34fFs29kd09";

                try {
                    desktop.browse(new URI(spotifyUrl));
                } catch (URISyntaxException e) {
                    MediaMod.INSTANCE.LOGGER.fatal("Something has gone terribly wrong... SpotifyHandler:l59");
                    e.printStackTrace();
                } catch (Exception e) {
                    PlayerMessager.sendMessage("&cFailed to open browser with the Spotify Auth URL!");
                    IChatComponent urlComponent = new ChatComponentText(ChatColor.translateAlternateColorCodes('&', "&lOpen URL"));
                    urlComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, spotifyUrl));
                    urlComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(ChatColor.translateAlternateColorCodes('&',
                            "&7Click this to open the Spotify Auth URL"))));
                    PlayerMessager.sendMessage(urlComponent);
                }
                break;

            case 2:
                SpotifyService.logout();
                this.mc.displayGuiScreen(new GuiServices());
                break;

            case 3:
                Settings.EXTENSION_ENABLED = !Settings.EXTENSION_ENABLED;
                button.displayString = getSuffix(Settings.EXTENSION_ENABLED, I18n.format("menu.guiservices.buttons.useBrowserExt.name"));
                break;

            case 4:
                Settings.SAVE_SPOTIFY_TOKEN = !Settings.SAVE_SPOTIFY_TOKEN;
                Settings.REFRESH_TOKEN = "";
                button.displayString = getSuffix(Settings.SAVE_SPOTIFY_TOKEN, I18n.format("menu.guiservices.buttons.saveSpotifyToken.name"));
                break;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
