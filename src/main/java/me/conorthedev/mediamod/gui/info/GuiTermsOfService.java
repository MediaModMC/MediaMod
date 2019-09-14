package me.conorthedev.mediamod.gui.info;

import me.conorthedev.mediamod.MediaMod;
import me.conorthedev.mediamod.gui.GuiMediaModSettings;
import me.conorthedev.mediamod.gui.util.CustomButton;
import me.conorthedev.mediamod.gui.util.IMediaGui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSnooper;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.io.IOException;

public class GuiTermsOfService extends GuiScreen implements IMediaGui {
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        drawCenteredString(fontRendererObj, EnumChatFormatting.BOLD + "Terms of Service", width / 2, 10, -1);
        drawCenteredString(fontRendererObj, "When using MediaMod & my other mods, your UUID, the current mod identifier", width / 2, 25, Color.red.getRGB());
        drawCenteredString(fontRendererObj, "and mod version are stored for analytical purposes.", width / 2, 34, Color.red.getRGB());
        drawCenteredString(fontRendererObj, "This info will not be shared. If you press close, you agree to", width / 2, 43, Color.red.getRGB());
        drawCenteredString(fontRendererObj, "this data being collected if you want to disable this: click \"Snooper Settings\"", width / 2, 51, Color.red.getRGB());
        drawCenteredString(fontRendererObj, "below and disable Snooper for Minecraft.", width / 2, 59, Color.red.getRGB());

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.buttonList.add(new CustomButton(0, width / 2 - 205, 100, "Close"));
        this.buttonList.add(new CustomButton(1, width / 2 + 5, 100, "Snooper Settings"));
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
