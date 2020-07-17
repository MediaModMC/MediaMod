package org.mediamod.mediamod.keybinds;

import org.mediamod.mediamod.MediaMod;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.lwjgl.input.Keyboard;

/**
 * The class that initializes and declares keybinds
 */
public class KeybindManager {
    /**
     * An instance of this class
     */
    public static final KeybindManager INSTANCE = new KeybindManager();

    /**
     * The KeyBinding class for the disable keybind
     *
     * @see KeyBinding
     */
    KeyBinding disableKeybind;

    /**
     * The KeyBinding class for the menu keybind
     *
     * @see KeyBinding
     */
    KeyBinding menuKeybind;

    /**
     * The KeyBinding class for the skip keybind
     *
     * @see KeyBinding
     */
    KeyBinding skipKeybind;

    /**
     * The KeyBinding class for the skip keybind
     *
     * @see KeyBinding
     */
    KeyBinding pausePlayKeybind;

    /**
     * Fired when you want to register keybinds
     *
     * @see MediaMod#preInit(FMLPreInitializationEvent)
     */
    public void register() {
        // Initialize and declare keybinds
        INSTANCE.disableKeybind = new KeyBinding("key.disableKeybind", Keyboard.KEY_P, "key.categories.mediamod");
        INSTANCE.menuKeybind = new KeyBinding("key.menuKeybind", Keyboard.KEY_M, "key.categories.mediamod");
        INSTANCE.skipKeybind = new KeyBinding("key.skipKeybind", Keyboard.KEY_F, "key.categories.mediamod");
        INSTANCE.pausePlayKeybind = new KeyBinding("key.pausePlayKeybind", Keyboard.KEY_N, "key.categories.mediamod");

        ClientRegistry.registerKeyBinding(disableKeybind);
        ClientRegistry.registerKeyBinding(menuKeybind);
        ClientRegistry.registerKeyBinding(skipKeybind);
        ClientRegistry.registerKeyBinding(pausePlayKeybind);
    }
}
