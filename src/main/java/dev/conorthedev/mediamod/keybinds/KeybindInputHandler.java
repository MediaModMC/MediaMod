package dev.conorthedev.mediamod.keybinds;

import dev.conorthedev.mediamod.MediaMod;
import dev.conorthedev.mediamod.config.Settings;
import dev.conorthedev.mediamod.gui.GuiMediaModSettings;
import dev.conorthedev.mediamod.media.base.ServiceHandler;
import dev.conorthedev.mediamod.util.ChatColor;
import dev.conorthedev.mediamod.util.PlayerMessager;
import dev.conorthedev.mediamod.util.TickScheduler;
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
        if(ServiceHandler.INSTANCE.getCurrentMediaHandler() != null) {
            if (KeybindManager.INSTANCE.skipKeybind.isPressed()) {
                if(!ServiceHandler.INSTANCE.getCurrentMediaHandler().handlerReady()) {
                    PlayerMessager.sendMessage(ChatColor.RED + "You can not skip when no services are playing music!", true);
                } else {
                    if(ServiceHandler.INSTANCE.getCurrentMediaHandler().supportsSkipping()) {
                        if(ServiceHandler.INSTANCE.getCurrentMediaHandler().skipTrack()) {
                            PlayerMessager.sendMessage(ChatColor.GREEN + "Song skipped!", true);
                        }
                    } else {
                        PlayerMessager.sendMessage(ChatColor.RED + "This service does not support skipping songs", true);
                    }
                }
            } else if (KeybindManager.INSTANCE.pausePlayKeybind.isPressed()) {
                if(!ServiceHandler.INSTANCE.getCurrentMediaHandler().handlerReady()) {
                    PlayerMessager.sendMessage(ChatColor.RED + "You can't pause or resume a song when no services are playing music!", true);
                } else {
                    if(ServiceHandler.INSTANCE.getCurrentMediaHandler().supportsPausing()) {
                        if(ServiceHandler.INSTANCE.getCurrentMediaHandler().pausePlayTrack()) {
                            PlayerMessager.sendMessage(ChatColor.GREEN + "Song paused/resumed!", true);
                        }
                    } else {
                        PlayerMessager.sendMessage(ChatColor.RED + "This service does not support pausing or resuming songs", true);
                    }
                }
            }
        }
    }
}
