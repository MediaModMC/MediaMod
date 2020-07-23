package org.mediamod.mediamod.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import org.mediamod.mediamod.MediaMod;
import org.mediamod.mediamod.api.APIHandler;
import org.mediamod.mediamod.config.Settings;
import org.mediamod.mediamod.gui.core.MediaModGui;
import org.mediamod.mediamod.gui.core.util.CustomButton;
import org.mediamod.mediamod.util.ChatColor;
import org.mediamod.mediamod.util.Metadata;
import org.mediamod.mediamod.util.Multithreading;
import org.mediamod.mediamod.util.PlayerMessenger;

import java.io.IOException;

/**
 * The Gui for editing the MediaMod Settings
 *
 * @see MediaModGui
 */
public class GuiMediaModSettings extends MediaModGui {
    public void initGui() {
        Settings.loadConfig();

        this.buttonList.add(new CustomButton(0, (width / 2) - 100, getRowPos(0), getSuffix(Settings.ENABLED, "Enabled")));
        this.buttonList.add(new CustomButton(1, (width / 2) - 100, getRowPos(1), "Customise Player"));
        this.buttonList.add(new CustomButton(2, (width / 2) - 100, getRowPos(2), "Services"));
        this.buttonList.add(new CustomButton(3, (width / 2) - 100, height - 30, "Close"));


        super.initGui();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        this.drawString(this.fontRendererObj, I18n.format("menu.guimediamod.text.version.name") + " " + Metadata.VERSION, this.width - this.fontRendererObj.getStringWidth("Version " + Metadata.VERSION) - 2, this.height - 10, -1);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected void actionPerformed(GuiButton button) {
        switch (button.id) {
            case 1:
                mc.displayGuiScreen(new GuiPlayerSettings());
                break;
            case 2:
                mc.displayGuiScreen(new GuiServices());
                break;
            case 3:
                mc.displayGuiScreen(null);
                break;
        }
    }

    protected String getButtonTooltip(int buttonId) {
        return null;
    }
}
