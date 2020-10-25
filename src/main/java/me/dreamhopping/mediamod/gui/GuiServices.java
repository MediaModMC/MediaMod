package me.dreamhopping.mediamod.gui;

import me.dreamhopping.mediamod.gui.core.util.ButtonTooltip;
import me.dreamhopping.mediamod.gui.core.util.IMediaGui;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import me.dreamhopping.mediamod.MediaMod;
import me.dreamhopping.mediamod.config.Settings;
import me.dreamhopping.mediamod.gui.core.util.CustomButton;
import me.dreamhopping.mediamod.media.MediaHandler;
import me.dreamhopping.mediamod.media.services.spotify.SpotifyService;
import me.dreamhopping.mediamod.util.ChatColor;
import me.dreamhopping.mediamod.util.PlayerMessenger;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

class GuiServices extends ButtonTooltip implements IMediaGui {

    public void initGui() {
        Settings.loadConfig();

        GuiButton backButton = new CustomButton(0, width / 2 - 100, height - 50, I18n.format("menu.guiplayerpositioning.buttons.back.name"));
        GuiButton loginoutSpotifyButton = new CustomButton(1, width / 2 - 100, height / 2 - 35, I18n.format("menu.guiservices.buttons." + (SpotifyService.isLoggedOut() ? "login" : "logout") + "Spotify.name"));
        GuiButton useBrowserExtButton = new CustomButton(3, width / 2 - 100, height / 2 + 15, getSuffix(Settings.EXTENSION_ENABLED, I18n.format("menu.guiservices.buttons.useBrowserExt.name")));
        GuiButton saveSpotifyTokenButton = new CustomButton(4, width / 2 - 100, height / 2 + 40, getSuffix(Settings.SAVE_SPOTIFY_TOKEN, "Save Spotify Token"));

        buttonList.add(backButton);
        buttonList.add(loginoutSpotifyButton);
        buttonList.add(useBrowserExtButton);
        buttonList.add(saveSpotifyTokenButton);

        saveSpotifyTokenButton.enabled = !SpotifyService.isLoggedOut();

        super.initGui();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        GlStateManager.pushMatrix();
        GlStateManager.color(1, 1, 1, 1);

        // Bind the texture for rendering
        mc.getTextureManager().bindTexture(this.headerResource);

        // Render the header
        Gui.drawModalRectWithCustomSizedTexture(width / 2 - 111, height / 2 - 110, 0, 0, 222, 55, 222, 55);
        GlStateManager.popMatrix();

        if (SpotifyService.isLoggedOut()) {
            drawCenteredString(fontRendererObj, I18n.format("menu.guiservices.text.spotifyNotLogged.name"), width / 2, height / 2 - 53, Color.red.getRGB());
        } else {
            drawCenteredString(fontRendererObj, I18n.format("menu.guiservices.text.spotifyLogged.name"), width / 2, height / 2 - 53, Color.green.getRGB());
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected String getButtonTooltip(int buttonId) {
        switch (buttonId) {
            case 3:
                return I18n.format("menu.guiservices.buttons.useBrowserExt.tooltip");
            case 4:
                return I18n.format("menu.guiservices.buttons.saveSpotifyToken.tooltip");
            default:
                return null;
        }
    }

    public void onGuiClosed() {
        Settings.saveConfig();
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiMediaModSettings());
                break;
            case 1:
                if (!SpotifyService.isLoggedOut()) {
                    SpotifyService.logout();
                    this.mc.displayGuiScreen(new GuiServices());
                    return;
                }

                this.mc.displayGuiScreen(null);

                Desktop desktop = Desktop.getDesktop();
                if (SpotifyService.spotifyClientID == null) {
                    MediaHandler.instance.reloadService(SpotifyService.class);

                    if (!SpotifyService.isLoggedOut()) {
                        return;
                    }
                }

                PlayerMessenger.sendMessage("&cOpening browser with instructions on what to do, when it opens log in with your Spotify Account and press 'Agree'");
                String spotifyUrl = "https://accounts.spotify.com/authorize?client_id=" + SpotifyService.spotifyClientID + "&response_type=code&redirect_uri=http%3A%2F%2Flocalhost:9103%2Fcallback%2F&scope=user-read-playback-state%20user-read-currently-playing%20user-modify-playback-state&state=34fFs29kd09";

                try {
                    desktop.browse(new URI(spotifyUrl));
                } catch (URISyntaxException e) {
                    MediaMod.INSTANCE.logger.fatal("Something has gone terribly wrong... SpotifyHandler:l59");
                    e.printStackTrace();
                } catch (Exception e) {
                    PlayerMessenger.sendMessage("&cFailed to open browser with the Spotify Auth URL!");
                    IChatComponent urlComponent = new ChatComponentText(ChatColor.translateAlternateColorCodes('&', "&lOpen URL"));
                    urlComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, spotifyUrl));
                    urlComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(ChatColor.translateAlternateColorCodes('&',
                            "&7Click this to open the Spotify Auth URL"))));
                    PlayerMessenger.sendMessage(urlComponent);
                }
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

    public boolean doesGuiPauseGame() {
        return false;
    }
}
