package me.conorthedev.mediamod.gui;

import cc.hyperium.Hyperium;
import cc.hyperium.utils.ChatColor;
import me.conorthedev.mediamod.config.ProgressStyle;
import me.conorthedev.mediamod.config.Settings;
import me.conorthedev.mediamod.gui.util.ButtonTooltip;
import me.conorthedev.mediamod.gui.util.IMediaGui;
import me.conorthedev.mediamod.media.base.ServiceHandler;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import java.io.IOException;

class GuiPlayerSettings extends ButtonTooltip implements IMediaGui {

    @Override
    public void initGui() {
        this.buttonList.add(new GuiButton(0, width / 2 - 100, height - 50, "Back"));

        this.buttonList.add(new GuiButton(1, width / 2 - 120, getRowPos(0), getSuffix(Settings.SHOW_ALBUM_ART, "Show Album Art")));
        this.buttonList.add(new GuiButton(2, width / 2 + 10, getRowPos(0), getSuffix(Settings.AUTO_COLOR_SELECTION, "Color Selection")));
        this.buttonList.add(new GuiButton(3, width / 2 - 120, getRowPos(1), getSuffix(Settings.MODERN_PLAYER_STYLE, "Modern Player")));
        this.buttonList.add(new GuiButton(4, width / 2 + 10, getRowPos(1), "Position Player"));
        this.buttonList.add(new GuiButton(5, width / 2 - 120, getRowPos(2), 250, 20, "Progress Style: "
                + ChatColor.GREEN + Settings.PROGRESS_STYLE.getDisplay()));

        buttonList.stream().filter(button -> button.id != 0 && button.id != 5).forEach(button -> button.setWidth(120));

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

        boolean testing = ServiceHandler.INSTANCE.getCurrentMediaHandler() == null || !ServiceHandler.INSTANCE.getCurrentMediaHandler().handlerReady();

        PlayerOverlay.INSTANCE.drawPlayer((width >> 1) - 80, (height >> 1) + 10, Settings.MODERN_PLAYER_STYLE, testing, 1.0);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected String getButtonTooltip(int buttonId) {
        switch (buttonId) {
            case 1:
                return "Toggling this OFF will disable album art, this affects auto color selection";
            case 2:
                return "Sets the background color of the player to the most prominent color in the album art";
            case 3:
                return "Enables a new player design that involves gradients and shadows, designed by ScottehBoeh";
            case 4:
                return "Move and resize the player";
            case 5:
                return "Cycles through progress display styles";
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
            case 4:
                this.mc.displayGuiScreen(new GuiPlayerPositioning());
                break;
            case 5:
                int nextIndex = Settings.PROGRESS_STYLE.ordinal() + 1;

                if (nextIndex >= ProgressStyle.values().length) {
                    nextIndex = 0;
                }

                Settings.PROGRESS_STYLE = ProgressStyle.values()[nextIndex];
                button.displayString = "Progress Style: " + ChatColor.GREEN + Settings.PROGRESS_STYLE.getDisplay();
                break;
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void onGuiClosed() {
        Hyperium.CONFIG.save();
    }
}