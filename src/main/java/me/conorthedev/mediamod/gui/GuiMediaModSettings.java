package me.conorthedev.mediamod.gui;

import cc.hyperium.Hyperium;
import me.conorthedev.mediamod.MediaMod;
import me.conorthedev.mediamod.config.Settings;
import me.conorthedev.mediamod.gui.info.GuiTermsOfService;
import me.conorthedev.mediamod.gui.util.ButtonTooltip;
import me.conorthedev.mediamod.gui.util.IMediaGui;
import me.conorthedev.mediamod.util.Metadata;
import net.minecraft.client.gui.GuiButton;

import java.io.IOException;

public class GuiMediaModSettings extends ButtonTooltip implements IMediaGui {
    @Override
    public void initGui() {
        if (!MediaMod.INSTANCE.isTOSAccepted()) {
            mc.displayGuiScreen(new GuiTermsOfService());
        }

        buttonList.add(new GuiButton(0, width / 2 - 100, height / 2 - 47, getSuffix(Settings.ENABLED, "Enabled")));
        buttonList.add(new GuiButton(1, width / 2 - 100, height / 2 - 23, getSuffix(Settings.SHOW_PLAYER, "Show Player")));
        buttonList.add(new GuiButton(2, width / 2 - 100, height / 2, "Player Settings"));
        buttonList.add(new GuiButton(3, width / 2 - 100, height / 2 + 23, "Services"));

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        drawHeader(width, height);

        drawString(fontRendererObj, "Developed by ConorTheDev", width - fontRendererObj.getStringWidth("Developed by ConorTheDev") - 2, height - 10, -1);
        drawString(fontRendererObj, "Version " + Metadata.VERSION, width - fontRendererObj.getStringWidth("Version " + Metadata.VERSION) - 2, height - 20, -1);

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
                mc.displayGuiScreen(new GuiPlayerSettings());
                break;

            case 3:
                mc.displayGuiScreen(new GuiServices());
                break;
        }

        super.actionPerformed(button);
    }

    @Override
    public void onGuiClosed() {
        Hyperium.CONFIG.save();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}