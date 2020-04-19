package dev.cbyrne.mediamod.keybinds

import dev.cbyrne.mediamod.MediaMod
import net.minecraft.client.settings.KeyBinding
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import org.lwjgl.input.Keyboard

object KeybindManager {
    val disableKeybind: KeyBinding = KeyBinding(
        "key.disableKeybind",
        Keyboard.KEY_P,
        "keys.categories.mediamod"
    )
    val menuKeybind: KeyBinding = KeyBinding(
        "key.menuKeybind",
        Keyboard.KEY_M,
        "keys.categories.mediamod"
    )

    fun register() {
        MediaMod.logger.info("Registering Keybinds")

        ClientRegistry.registerKeyBinding(disableKeybind)
        ClientRegistry.registerKeyBinding(menuKeybind)

        MinecraftForge.EVENT_BUS.register(KeybindInputHandler())

        MediaMod.logger.info("Keybinds Initialized")
    }
}