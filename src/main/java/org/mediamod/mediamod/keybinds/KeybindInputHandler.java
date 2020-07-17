package org.mediamod.mediamod.keybinds;

import org.mediamod.mediamod.config.Settings;
import org.mediamod.mediamod.gui.GuiMediaModSettings;
import org.mediamod.mediamod.media.MediaHandler;
import org.mediamod.mediamod.util.ChatColor;
import org.mediamod.mediamod.util.PlayerMessager;
import org.mediamod.mediamod.util.TickScheduler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

/**
 * The class that handles keybind events
 */
public class KeybindInputHandler {
    /**
     * Fired when a key is pressed
     *
     * @param event - KeyInputEvent
     * @see InputEvent.KeyInputEvent
     */
    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeybindManager.INSTANCE.disableKeybind.isPressed()) {
            if (!Settings.SHOW_PLAYER) {
                PlayerMessager.sendMessage("Player Visible");
                Settings.SHOW_PLAYER = true;
            } else {
                PlayerMessager.sendMessage("Player Hidden");
                Settings.SHOW_PLAYER = false;
            }
            Settings.saveConfig();
        } else if (KeybindManager.INSTANCE.menuKeybind.isPressed()) {
            TickScheduler.INSTANCE.schedule(0, () -> Minecraft.getMinecraft().displayGuiScreen(new GuiMediaModSettings()));
        }
        if (MediaHandler.instance.getCurrentService() != null) {
            if (KeybindManager.INSTANCE.skipKeybind.isPressed()) {
                if (MediaHandler.instance.getCurrentService().supportsSkipping()) {
                    if (MediaHandler.instance.getCurrentService().skipTrack()) {
                        PlayerMessager.sendMessage(ChatColor.GREEN + "Song skipped!", true);
                    }
                } else {
                    PlayerMessager.sendMessage(ChatColor.RED + "This service does not support skipping songs", true);
                }
            }
        } else if (KeybindManager.INSTANCE.pausePlayKeybind.isPressed()) {
            if (MediaHandler.instance.getCurrentService().supportsPausing()) {
                if (MediaHandler.instance.getCurrentService().pausePlayTrack()) {
                    PlayerMessager.sendMessage(ChatColor.GREEN + "Song paused/resumed!", true);
                }
            } else {
                PlayerMessager.sendMessage(ChatColor.RED + "This service does not support pausing or resuming songs", true);
            }
        }
    }
}
