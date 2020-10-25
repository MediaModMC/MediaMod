package me.dreamhopping.mediamod.keybinds;

import me.dreamhopping.mediamod.gui.GuiMediaModSettings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import me.dreamhopping.mediamod.config.Settings;
import me.dreamhopping.mediamod.media.MediaHandler;
import me.dreamhopping.mediamod.util.ChatColor;
import me.dreamhopping.mediamod.util.Multithreading;
import me.dreamhopping.mediamod.util.PlayerMessenger;
import me.dreamhopping.mediamod.util.TickScheduler;

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
                PlayerMessenger.sendMessage(ChatColor.GRAY + "Player Visible");
                Settings.SHOW_PLAYER = true;
            } else {
                PlayerMessenger.sendMessage(ChatColor.GRAY + "Player Hidden");
                Settings.SHOW_PLAYER = false;
            }
            Settings.saveConfig();
        } else if (KeybindManager.INSTANCE.menuKeybind.isPressed()) {
            TickScheduler.INSTANCE.schedule(0, () -> Minecraft.getMinecraft().displayGuiScreen(new GuiMediaModSettings()));
        }
        if (MediaHandler.instance.getCurrentService() != null) {
            Multithreading.runAsync(() -> {
                if (KeybindManager.INSTANCE.skipKeybind.isPressed()) {
                    if (MediaHandler.instance.getCurrentService().supportsSkipping()) {
                        if (MediaHandler.instance.getCurrentService().skipTrack()) {
                            PlayerMessenger.sendMessage(ChatColor.GREEN + "Song skipped!", true);
                        }
                    } else {
                        PlayerMessenger.sendMessage(ChatColor.RED + "This service does not support skipping songs", true);
                    }
                } else if (KeybindManager.INSTANCE.pausePlayKeybind.isPressed()) {
                    if (MediaHandler.instance.getCurrentService().supportsPausing()) {
                        if (MediaHandler.instance.getCurrentService().pausePlayTrack()) {
                            PlayerMessenger.sendMessage(ChatColor.GREEN + "Song paused/resumed!", true);
                        }
                    } else {
                        PlayerMessenger.sendMessage(ChatColor.RED + "This service does not support pausing or resuming songs", true);
                    }
                }
            });
        }
    }
}
