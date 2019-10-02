package me.conorthedev.mediamod.keybinds;

import me.conorthedev.mediamod.MediaMod;
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
     * Fired when you want to register keybinds
     *
     * @see MediaMod#preInit(FMLPreInitializationEvent)
     */
    public void register() {
        // Initialize and declare keybid
        INSTANCE.disableKeybind = new KeyBinding("key.disableKeybind", Keyboard.KEY_P, "key.categories.mediamod");
        ClientRegistry.registerKeyBinding(INSTANCE.disableKeybind);
    }
}
