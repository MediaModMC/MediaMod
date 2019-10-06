package me.conorthedev.mediamod.keybinds;

import cc.hyperium.Hyperium;
import me.conorthedev.mediamod.keybinds.impl.DisableKeybind;

public class KeybindManager {

    public static final KeybindManager INSTANCE = new KeybindManager();

    public void register() {
        Hyperium.INSTANCE.getHandlers().getKeybindHandler().registerKeyBinding(new DisableKeybind());
    }
}
