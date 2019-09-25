package me.conorthedev.mediamod.gui;

import me.conorthedev.mediamod.Settings;
import me.conorthedev.mediamod.gui.util.CustomButton;
import me.conorthedev.mediamod.gui.util.IMediaGui;
import me.conorthedev.mediamod.media.base.ServiceHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.io.IOException;

public class GuiPlayerPositioning extends GuiScreen implements IMediaGui {
    private int currentX = Settings.PLAYER_X;
    private int currentY = Settings.PLAYER_Y;
    private double currentZoom = Settings.PLAYER_ZOOM;

    @Override
    public void initGui() {
        Settings.loadConfig();

        this.buttonList.add(new CustomButton(2, width / 2 - 100, height - 83, EnumChatFormatting.GREEN + "Save"));
        this.buttonList.add(new CustomButton(1, width / 2 - 100, height - 60, EnumChatFormatting.RED + "Reset"));
        this.buttonList.add(new CustomButton(0, width / 2 - 100, height - 37, "Back"));
        this.buttonList.add(new GuiSlider(3, width / 2 - 75, height - 105, "Scale: ", 1.0, 2.0, Settings.PLAYER_ZOOM, null));

        super.initGui();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawDefaultBackground();

        this.drawCenteredString(fontRendererObj, "Position the player by dragging it around, click reset to reset position.", width / 2, height - 120, -1);

        boolean testing;
        if (ServiceHandler.INSTANCE.getCurrentMediaHandler() == null) {
            testing = true;
        } else {
            testing = !ServiceHandler.INSTANCE.getCurrentMediaHandler().handlerReady();
        }

        PlayerOverlay.INSTANCE.drawPlayer(currentX, currentY, Settings.MODERN_PLAYER_STYLE, testing, true);

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
                this.currentZoom = 1.0;

                Settings.PLAYER_X = this.currentX;
                Settings.PLAYER_Y = this.currentY;
                Settings.PLAYER_ZOOM = this.currentZoom;
                this.mc.displayGuiScreen(new GuiPlayerPositioning());
                break;

            case 2:
                Settings.PLAYER_X = this.currentX;
                Settings.PLAYER_Y = this.currentY;
                this.mc.displayGuiScreen(new GuiPlayerSettings());
                break;
            case 3:
                GuiSlider slider = (GuiSlider) button;
                this.currentZoom = Math.round(slider.getValue() * 10) / 10.0;

                Settings.PLAYER_ZOOM = this.currentZoom;
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
        for (GuiButton button : this.buttonList) {
            if (button.isMouseOver() && button.id == 3) {
                GuiSlider slider = (GuiSlider) button;
                this.currentZoom = Math.round(slider.getValue() * 10) / 10.0;

                Settings.PLAYER_ZOOM = this.currentZoom;
                super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
                return;
            } else if (button.isMouseOver()) {
                // Call the super function
                super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
                return;
            }
        }

        if (clickedMouseButton == 0) {
            // It was the left click, change the position
            this.currentX = mouseX - 75;
            this.currentY = mouseY - 25;
        }

        // Call the super function
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    public void onGuiClosed() {
        Settings.saveConfig();
        super.onGuiClosed();
    }
}
