package me.conorthedev.mediamod.keybinds.impl;

import cc.hyperium.Hyperium;
import cc.hyperium.handlers.handlers.keybinds.HyperiumBind;
import me.conorthedev.mediamod.gui.GuiMediaModSettings;
import org.lwjgl.input.Keyboard;

public class MenuKeybind extends HyperiumBind {

    public MenuKeybind() {
        super("Open menu", Keyboard.KEY_M);
    }

    @Override
    public void onPress() {
        Hyperium.INSTANCE.getHandlers().getGuiDisplayHandler().setDisplayNextTick(new GuiMediaModSettings());
    }
}
