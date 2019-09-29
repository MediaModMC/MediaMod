package me.conorthedev.mediamod.gui.info;

import me.conorthedev.mediamod.MediaMod;
import me.conorthedev.mediamod.gui.GuiMediaModSettings;
import me.conorthedev.mediamod.gui.util.CustomButton;
import me.conorthedev.mediamod.gui.util.IMediaGui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSnooper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.io.IOException;

public class GuiTermsOfService extends GuiScreen implements IMediaGui {
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        drawCenteredString(fontRendererObj, EnumChatFormatting.BOLD + I18n.format("menu.guitermsofservice.text.title.name"), width / 2, 10, -1);
        drawCenteredString(fontRendererObj, I18n.format("menu.guitermsofservice.text.description.line1"), width / 2, 25, Color.red.getRGB());
        drawCenteredString(fontRendererObj, I18n.format("menu.guitermsofservice.text.description.line2"), width / 2, 34, Color.red.getRGB());
        drawCenteredString(fontRendererObj, I18n.format("menu.guitermsofservice.text.description.line3"), width / 2, 43, Color.red.getRGB());
        drawCenteredString(fontRendererObj, I18n.format("menu.guitermsofservice.text.description.line4"), width / 2, 51, Color.red.getRGB());
        drawCenteredString(fontRendererObj, I18n.format("menu.guitermsofservice.text.description.line5"), width / 2, 59, Color.red.getRGB());

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new CustomButton(0, width / 2 - 205, 100, I18n.format("menu.guitermsofservice.buttons.agree.text")));
        this.buttonList.add(new CustomButton(1, width / 2 + 5, 100, I18n.format("menu.guitermsofservice.buttons.snooperSettings.text")));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        switch (button.id) {
            case 0:
                MediaMod.INSTANCE.setTOSAccepted();
                this.mc.displayGuiScreen(new GuiMediaModSettings());
                break;

            case 1:
                this.mc.displayGuiScreen(new GuiSnooper(this, mc.gameSettings));
                break;
        }

        super.actionPerformed(button);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return super.doesGuiPauseGame();
    }
}
