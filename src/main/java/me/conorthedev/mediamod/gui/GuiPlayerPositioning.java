package me.conorthedev.mediamod.gui;

import me.conorthedev.mediamod.Settings;
import me.conorthedev.mediamod.gui.util.CustomButton;
import me.conorthedev.mediamod.gui.util.IMediaGui;
import me.conorthedev.mediamod.media.base.ServiceHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;

import java.io.IOException;

public class GuiPlayerPositioning extends GuiScreen implements IMediaGui {
    private int currentX = Settings.PLAYER_X;
    private int currentY = Settings.PLAYER_Y;

    @Override
    public void initGui() {
        Settings.loadConfig();

        this.buttonList.add(new CustomButton(2, width / 2 - 100, height - 83, EnumChatFormatting.GREEN + "Save"));
        this.buttonList.add(new CustomButton(1, width / 2 - 100, height - 60, EnumChatFormatting.RED + "Reset"));
        this.buttonList.add(new CustomButton(0, width / 2 - 100, height - 37, "Back"));

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        this.drawCenteredString(fontRendererObj, "Position the player by dragging it around, click reset to reset position.", width / 2, height - 120, -1);

        boolean testing = !ServiceHandler.INSTANCE.getCurrentMediaHandler().handlerReady();
        PlayerOverlay.INSTANCE.drawPlayer(currentX, currentY, Settings.MODERN_PLAYER_STYLE, testing);

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        super.actionPerformed(button);
        switch (button.id) {
            case 0:
                this.mc.displayGuiScreen(new GuiPlayerSettings());
                break;

            case 1:
                this.currentX = 5;
                this.currentY = 5;

                Settings.PLAYER_X = this.currentX;
                Settings.PLAYER_Y = this.currentY;
                Settings.saveConfig();
                break;

            case 2:
                Settings.PLAYER_X = this.currentX;
                Settings.PLAYER_Y = this.currentY;
                Settings.saveConfig();
                this.mc.displayGuiScreen(new GuiPlayerSettings());
                break;
        }
    }

    /**
     * Invoked when the mouse is being dragged
     *
     * @param mouseX             - the mouse's X position
     * @param mouseY             - the mouse's Y position
     * @param clickedMouseButton - the mouse button that was clicked
     * @param timeSinceLastClick - the time since the last click
     */
    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (clickedMouseButton == 0) {
            // It was the left click, change the position
            this.currentX = mouseX;
            this.currentY = mouseY;
        }

        // Call the super function
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
    }
}
