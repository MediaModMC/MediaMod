package me.conorthedev.mediamod.gui;

import me.conorthedev.mediamod.config.Settings;
import me.conorthedev.mediamod.gui.util.CustomButton;
import me.conorthedev.mediamod.gui.util.IMediaGui;
import me.conorthedev.mediamod.media.base.ServiceHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.fml.client.config.GuiSlider;

import java.io.IOException;

public class GuiPlayerPositioning extends GuiScreen implements IMediaGui {
    private double currentX = Settings.PLAYER_X;
    private double currentY = Settings.PLAYER_Y;
    private double offsetX = -1;
    private double offsetY = -1;
    private GuiSlider slider = null;
    private boolean dragging;

    @Override
    public void initGui() {
        Settings.loadConfig();

        this.buttonList.add(new CustomButton(2, width / 2 - 100, height - 83, EnumChatFormatting.GREEN + "Save"));
        this.buttonList.add(new CustomButton(1, width / 2 - 100, height - 60, EnumChatFormatting.RED + "Reset"));
        this.buttonList.add(new CustomButton(0, width / 2 - 100, height - 37, "Back"));
        this.buttonList.add(slider = new GuiSlider(3, width / 2 - 75, height - 105, 150, 20, "Scale: ", "", 0.1, 2.0, Settings.PLAYER_ZOOM, true, false, it -> {
            // custom display string change stuff
            it.displayString = it.dispString + (Math.round(it.getValue() * 10) / 10.0) + it.suffix;
        }));
        slider.updateSlider(); // call custom display string changer

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

        PlayerOverlay.INSTANCE.drawPlayer(currentX, currentY, Settings.MODERN_PLAYER_STYLE, testing, slider.getValue());

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
                slider.setValue(1.0);

                Settings.PLAYER_X = this.currentX;
                Settings.PLAYER_Y = this.currentY;
                Settings.PLAYER_ZOOM = 1.0;
                this.mc.displayGuiScreen(new GuiPlayerPositioning());
                break;

            case 2:
                Settings.PLAYER_X = this.currentX;
                Settings.PLAYER_Y = this.currentY;
                Settings.PLAYER_ZOOM = slider.getValue();
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
        if (dragging && clickedMouseButton == 0) {
            // It was the left click, change the position
            this.currentX = (mouseX + (offsetX));
            this.currentY = (mouseY + (offsetY));
        }

        // Call the super function
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (mouseButton == 0) {
            for (GuiButton button : this.buttonList) {
                if (button.isMouseOver()) {
                    return;
                }
            }
            dragging = true;
            offsetX = currentX - mouseX;
            offsetY = currentY - mouseY;
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        dragging = false;
    }

    @Override
    public void onGuiClosed() {
        Settings.saveConfig();
        super.onGuiClosed();
    }
}
