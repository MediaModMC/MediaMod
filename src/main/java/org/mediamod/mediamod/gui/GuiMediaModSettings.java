package org.mediamod.mediamod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import org.mediamod.mediamod.MediaMod;
import org.mediamod.mediamod.config.Settings;
import org.mediamod.mediamod.gui.util.ButtonTooltip;
import org.mediamod.mediamod.gui.util.CustomButton;
import org.mediamod.mediamod.gui.util.IMediaGui;
import org.mediamod.mediamod.util.ChatColor;
import org.mediamod.mediamod.util.Metadata;
import org.mediamod.mediamod.util.Multithreading;
import org.mediamod.mediamod.util.PlayerMessenger;

import java.io.IOException;

/**
 * The Gui for editing the MediaMod Settings
 *
 * @see net.minecraft.client.gui.GuiScreen
 */
public class GuiMediaModSettings extends ButtonTooltip implements IMediaGui {
    @Override
    public void initGui() {
        Settings.loadConfig();

        this.buttonList.add(new CustomButton(0, width / 2 - 100, height / 2 - 47, getSuffix(Settings.ENABLED, I18n.format("menu.guimediamod.buttons.enabled.name"))));
        this.buttonList.add(new CustomButton(1, width / 2 - 100, height / 2 - 23, getSuffix(Settings.SHOW_PLAYER, I18n.format("menu.guimediamod.buttons.showPlayer.name"))));
        this.buttonList.add(new CustomButton(4, width / 2 - 100, height / 2, getSuffix(Settings.ANNOUNCE_TRACKS, I18n.format("menu.guimediamod.buttons.announceTracks.name"))));
        this.buttonList.add(new CustomButton(2, width / 2 - 100, height / 2 + 23, I18n.format("menu.guimediamod.buttons.playerSettings.name")));
        this.buttonList.add(new CustomButton(3, width / 2 - 100, height / 2 + 47, I18n.format("menu.guimediamod.buttons.servicesSettings.name")));

        if(!MediaMod.INSTANCE.authenticatedWithAPI) {
            this.buttonList.add(new CustomButton(5, 5, height - 25, "Reconnect"));
            this.buttonList.get(5).width = 100;
        }

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        this.drawHeader(width, height);

        this.drawString(this.fontRenderer, I18n.format("menu.guimediamod.text.author.name"), this.width - this.fontRenderer.getStringWidth(I18n.format("menu.guimediamod.text.author.name")) - 2, this.height - 10, -1);
        this.drawString(this.fontRenderer, I18n.format("menu.guimediamod.text.version.name") + " " + Metadata.VERSION, this.width - this.fontRenderer.getStringWidth("Version " + Metadata.VERSION) - 2, this.height - 20, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected String getButtonTooltip(int buttonId) {
        switch (buttonId) {
            case 0:
                return I18n.format("menu.guimediamod.buttons.enabled.tooltip");
            case 1:
                return I18n.format("menu.guimediamod.buttons.showPlayer.tooltip");
            case 4:
                return I18n.format("menu.guimediamod.buttons.announceTracks.tooltip");
            case 5:
                return "Failed to connect to MediaMod API. Click here to reconnect!";
        }
        return null;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                Settings.ENABLED = !Settings.ENABLED;
                button.displayString = getSuffix(Settings.ENABLED, I18n.format("menu.guimediamod.buttons.enabled.name"));
                break;
            case 1:
                Settings.SHOW_PLAYER = !Settings.SHOW_PLAYER;
                button.displayString = getSuffix(Settings.SHOW_PLAYER, I18n.format("menu.guimediamod.buttons.showPlayer.name"));
                break;
            case 2:
                this.mc.displayGuiScreen(new GuiPlayerSettings());
                break;
            case 3:
                this.mc.displayGuiScreen(new GuiServices());
                break;
            case 4:
                Settings.ANNOUNCE_TRACKS = !Settings.ANNOUNCE_TRACKS;
                button.displayString = getSuffix(Settings.ANNOUNCE_TRACKS, I18n.format("menu.guimediamod.buttons.announceTracks.name"));
                break;
            case 5:
                Multithreading.runAsync(() -> {
                    PlayerMessenger.sendMessage(ChatColor.GRAY + "Connecting to MediaMod API...", true);
                    MediaMod.INSTANCE.authenticatedWithAPI = MediaMod.INSTANCE.coreMod.register();

                    if (MediaMod.INSTANCE.authenticatedWithAPI) {
                        PlayerMessenger.sendMessage(ChatColor.GREEN + "Connected!", true);

                        Minecraft.getMinecraft().displayGuiScreen(null);
                        Minecraft.getMinecraft().displayGuiScreen(new GuiMediaModSettings());
                    } else {
                        PlayerMessenger.sendMessage(ChatColor.RED + "Failed to connect to MediaMod API!");
                    }
                });
                break;
        }

        super.actionPerformed(button);
    }

    @Override
    public void onGuiClosed() {
        Settings.saveConfig();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
