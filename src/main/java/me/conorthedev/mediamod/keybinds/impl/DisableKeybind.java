package me.conorthedev.mediamod.keybinds.impl;

import cc.hyperium.handlers.handlers.keybinds.HyperiumBind;
import me.conorthedev.mediamod.config.Settings;
import me.conorthedev.mediamod.util.PlayerMessager;
import org.lwjgl.input.Keyboard;

public class DisableKeybind extends HyperiumBind {

    public DisableKeybind() {
        super("Disable Player", Keyboard.KEY_P);
    }

    @Override
    public void onPress() {
        Settings.SHOW_PLAYER = !Settings.SHOW_PLAYER;
        if (Settings.SHOW_PLAYER) {
            PlayerMessager.sendMessage("Player Visible");
        } else {
            PlayerMessager.sendMessage("Player Hidden");
        }
    }
}
