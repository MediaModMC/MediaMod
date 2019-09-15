package me.conorthedev.mediamod.gui;

import me.conorthedev.mediamod.MediaMod;
import me.conorthedev.mediamod.Settings;
import me.conorthedev.mediamod.gui.info.GuiTermsOfService;
import me.conorthedev.mediamod.gui.util.ButtonTooltip;
import me.conorthedev.mediamod.gui.util.CustomButton;
import me.conorthedev.mediamod.gui.util.IMediaGui;
import me.conorthedev.mediamod.util.Metadata;
import net.minecraft.client.gui.GuiButton;

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

        if (!MediaMod.INSTANCE.getTOSAccepted()) {
            this.mc.displayGuiScreen(new GuiTermsOfService());
        }

        this.buttonList.add(new CustomButton(0, width / 2 - 100, height / 2 - 47, getSuffix(Settings.ENABLED, "Enabled")));
        this.buttonList.add(new CustomButton(1, width / 2 - 100, height / 2 - 23, getSuffix(Settings.ENABLED, "Show Player")));
        this.buttonList.add(new CustomButton(2, width / 2 - 100, height / 2, "Player Settings"));
        this.buttonList.add(new CustomButton(3, width / 2 - 100, height / 2 + 23, "Services"));

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        this.drawHeader(width, height);

        this.drawString(this.fontRendererObj, "Developed by ConorTheDev", this.width - this.fontRendererObj.getStringWidth("Developed by ConorTheDev") - 2, this.height - 10, -1);
        this.drawString(this.fontRendererObj, "Version " + Metadata.VERSION, this.width - this.fontRendererObj.getStringWidth("Version " + Metadata.VERSION) - 2, this.height - 20, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected String getButtonTooltip(int buttonId) {
        switch (buttonId) {
            case 0:
                return "Disables the mod entirely (requires a restart to work fully)";
            case 1:
                return "Disable or Enable the Player HUD";
        }
        return null;
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                Settings.ENABLED = !Settings.ENABLED;
                button.displayString = getSuffix(Settings.ENABLED, "Enabled");
                break;

            case 1:
                Settings.SHOW_PLAYER = !Settings.SHOW_PLAYER;
                button.displayString = getSuffix(Settings.SHOW_PLAYER, "Show Player");
                break;

            case 2:
                this.mc.displayGuiScreen(new GuiPlayerSettings());
                break;

            case 3:
                this.mc.displayGuiScreen(new GuiServices());
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
