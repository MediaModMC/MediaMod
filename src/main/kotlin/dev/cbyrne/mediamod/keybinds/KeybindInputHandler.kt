package dev.cbyrne.mediamod.keybinds

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.InputEvent

class KeybindInputHandler {
    @SubscribeEvent
    fun onKey(e: InputEvent.KeyInputEvent) {
        if(KeybindManager.disableKeybind.isPressed) {
            // todo: disable / show player
        } else if (KeybindManager.menuKeybind.isPressed) {
            // todo: show settings gui
        }
    }
}