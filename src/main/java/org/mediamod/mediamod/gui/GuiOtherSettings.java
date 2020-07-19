package org.mediamod.mediamod.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import org.mediamod.mediamod.config.Settings;
import org.mediamod.mediamod.gui.util.ButtonTooltip;
import org.mediamod.mediamod.gui.util.CustomButton;
import org.mediamod.mediamod.gui.util.IMediaGui;

import java.io.IOException;

public class GuiOtherSettings extends ButtonTooltip implements IMediaGui {
    public void initGui() {
        GuiButton backButton = new CustomButton(0, width / 2 - 100, height - 50, I18n.format("menu.guiplayerpositioning.buttons.back.name"));
        GuiButton alwaysUpdateButton = new CustomButton(1, width / 2 - 100, height / 2 - 35, getSuffix(Settings.ALWAYS_AUTOUPDATE, "Always Autoupdate"));

        buttonList.add(backButton);
        buttonList.add(alwaysUpdateButton);

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

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    protected String getButtonTooltip(int buttonId) {
        if(buttonId == 1) {
            return "This automatically downloads a MediaMod Update and schedules the update to run after the client closes without user interaction (disabled by default)s";
        } else {
            return null;
        }
    }

    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiMediaModSettings());
                break;
            case 1:
                Settings.ALWAYS_AUTOUPDATE = !Settings.ALWAYS_AUTOUPDATE;
                button.displayString = getSuffix(Settings.ALWAYS_AUTOUPDATE, "Always Autoupdate");
                Settings.saveConfig();
        }
    }

    public boolean doesGuiPauseGame() {
        return false;
    }
}
