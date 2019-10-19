package me.conorthedev.mediamod.gui;

import me.conorthedev.mediamod.config.ProgressStyle;
import me.conorthedev.mediamod.config.Settings;
import me.conorthedev.mediamod.gui.util.ButtonTooltip;
import me.conorthedev.mediamod.gui.util.CustomButton;
import me.conorthedev.mediamod.gui.util.IMediaGui;
import me.conorthedev.mediamod.media.base.ServiceHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextFormatting;

import java.io.IOException;

class GuiPlayerSettings extends ButtonTooltip implements IMediaGui {
    @Override
    public void initGui() {
        Settings.loadConfig();
        this.buttonList.add(new CustomButton(0, width / 2 - 100, height - 50, I18n.format("menu.guiplayerpositioning.buttons.back.name")));

        this.buttonList.add(new CustomButton(1, width / 2 - 120, getRowPos(0), getSuffix(Settings.SHOW_ALBUM_ART, I18n.format("menu.guiplayersettings.buttons.showAlbumArt.name"))));
        this.buttonList.add(new CustomButton(2, width / 2 + 10, getRowPos(0), getSuffix(Settings.AUTO_COLOR_SELECTION, I18n.format("menu.guiplayersettings.buttons.colorSelection.name"))));
        this.buttonList.add(new CustomButton(3, width / 2 - 120, getRowPos(1), getSuffix(Settings.MODERN_PLAYER_STYLE, I18n.format("menu.guiplayersettings.buttons.modernPlayer.name"))));
        this.buttonList.add(new CustomButton(4, width / 2 + 10, getRowPos(1), I18n.format("menu.guiplayersettings.buttons.position.name")));
        this.buttonList.add(new CustomButton(5, width / 2 - 120, getRowPos(2), 250, 20, I18n.format("menu.guiplayersettings.buttons.progressStyle.name") + " " /* Minecraft's .lang files don't support trailing spaces */ + TextFormatting.GREEN + I18n.format("menu.guiplayersettings.buttons.progressStyle." + Settings.PROGRESS_STYLE.name().toLowerCase())));

        for (GuiButton button : buttonList) {
            if (button.id != 0 && button.id != 5) {
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
        Gui.drawModalRectWithCustomSizedTexture(width / 2 - 111, 2, 0, 0, 222, 55, 222, 55);
        GlStateManager.popMatrix();

        boolean testing;
        if (ServiceHandler.INSTANCE.getCurrentMediaHandler() == null) {
            testing = true;
        } else {
            testing = !ServiceHandler.INSTANCE.getCurrentMediaHandler().handlerReady();
        }

        PlayerOverlay.INSTANCE.drawPlayer(width / 2 - 80, height / 2 + 10, Settings.MODERN_PLAYER_STYLE, testing, 1.0);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected String getButtonTooltip(int buttonId) {
        switch (buttonId) {
            case 1:
                return I18n.format("menu.guiplayersettings.buttons.showAlbumArt.tooltip");
            case 2:
                return I18n.format("menu.guiplayersettings.buttons.colorSelection.tooltip");
            case 3:
                return I18n.format("menu.guiplayersettings.buttons.modernPlayer.tooltip");
            case 4:
                return I18n.format("menu.guiplayersettings.buttons.position.tooltip");
            case 5:
                return I18n.format("menu.guiplayersettings.buttons.progressStyle.tooltip");
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
                button.displayString = getSuffix(Settings.SHOW_ALBUM_ART, I18n.format("menu.guiplayersettings.buttons.showAlbumArt.name"));
                break;

            case 2:
                Settings.AUTO_COLOR_SELECTION = !Settings.AUTO_COLOR_SELECTION;
                button.displayString = getSuffix(Settings.AUTO_COLOR_SELECTION, I18n.format("menu.guiplayersettings.buttons.colorSelection.name"));
                break;

            case 3:
                Settings.MODERN_PLAYER_STYLE = !Settings.MODERN_PLAYER_STYLE;
                button.displayString = getSuffix(Settings.MODERN_PLAYER_STYLE, I18n.format("menu.guiplayersettings.buttons.modernPlayer.name"));
                break;
            case 4:
                this.mc.displayGuiScreen(new GuiPlayerPositioning());
                break;
            case 5:
                int nextIndex = Settings.PROGRESS_STYLE.ordinal() + 1;
                if (nextIndex >= ProgressStyle.values().length) {
                    nextIndex = 0;
                }
                Settings.PROGRESS_STYLE = ProgressStyle.values()[nextIndex];
                button.displayString = I18n.format("menu.guiplayersettings.buttons.progressStyle.name") + " " + TextFormatting.GREEN + I18n.format("menu.guiplayersettings.buttons.progressStyle." + Settings.PROGRESS_STYLE.name().toLowerCase());
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
}
