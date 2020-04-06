package me.conorthedev.mediamod;

import club.sk1er.modcore.ModCoreInstaller;
import java.io.File;
import java.io.IOException;
import me.conorthedev.mediamod.command.MediaModCommand;
import me.conorthedev.mediamod.config.Settings;
import me.conorthedev.mediamod.gui.PlayerOverlay;
import me.conorthedev.mediamod.keybinds.KeybindInputHandler;
import me.conorthedev.mediamod.keybinds.KeybindManager;
import me.conorthedev.mediamod.media.base.ServiceHandler;
import me.conorthedev.mediamod.media.browser.BrowserHandler;
import me.conorthedev.mediamod.media.spotify.SpotifyHandler;
import me.conorthedev.mediamod.util.Metadata;
import me.conorthedev.mediamod.util.PlayerMessager;
import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The main class for MediaMod
 *
 * @author ConorTheDev
 * @see net.minecraftforge.fml.common.Mod
 */
@Mod(name = Metadata.NAME, modid = Metadata.MODID, version = Metadata.VERSION)
public class MediaMod {
    /**
     * Path to the file verifying that the user has accepted the Terms of Service
     */
    private static final File TOS_ACCEPTED_FILE = new File(FMLClientHandler.instance().getClient().mcDataDir, "mediamod/tosaccepted.lock");

    /**
     * An instance of this class to access non-static methods from other classes
     */
    @Mod.Instance(Metadata.MODID)
    public static MediaMod INSTANCE;

    /**
     * Logger used to log info messages, debug messages, error messages & more
     *
     * @see org.apache.logging.log4j.Logger
     */
    public final Logger LOGGER = LogManager.getLogger("MediaMod");

    /**
     * Check if the user is in a development environment, this is used for DEBUG messages
     */
    public final boolean DEVELOPMENT_ENVIRONMENT = (boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    /**
     * Fired before Minecraft starts
     *
     * @param event - FMLPreInitializationEvent
     * @see FMLPreInitializationEvent
     */
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        // Register Keybinds
        KeybindManager.INSTANCE.register();

        // Register the keybind handler
        MinecraftForge.EVENT_BUS.register(new KeybindInputHandler());
    }

    /**
     * Fired when Minecraft is starting
     *
     * @param event - FMLInitializationEvent
     * @see FMLInitializationEvent
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {
        ModCoreInstaller.initializeModCore(Minecraft.getMinecraft().mcDataDir);
        LOGGER.info("MediaMod starting...");

        // Register event subscribers and commands
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(PlayerOverlay.INSTANCE);
        MinecraftForge.EVENT_BUS.register(PlayerMessager.INSTANCE);
        ClientCommandHandler.instance.registerCommand(new MediaModCommand());

        // Create the MediaMod Directory if it does not exist
        File MEDIAMOD_DIRECTORY = new File(FMLClientHandler.instance().getClient().mcDataDir, "mediamod");

        if (!MEDIAMOD_DIRECTORY.exists()) {
            LOGGER.info("Creating necessary directories and files for first launch...");
            boolean mkdir = MEDIAMOD_DIRECTORY.mkdir();

            if (mkdir) {
                LOGGER.info("Created necessary directories and files!");
            } else {
                LOGGER.fatal("Failed to create necessary directories and files!");
            }
        }

        // Load the config
        LOGGER.info("Loading Configuration...");
        Settings.loadConfig();

        // Load Media Handlers
        ServiceHandler serviceHandler = ServiceHandler.INSTANCE;
        serviceHandler.registerHandler(new BrowserHandler());
        serviceHandler.registerHandler(new SpotifyHandler());

        // Initialize the handlers
        serviceHandler.initializeHandlers();
    }

    public boolean getTOSAccepted() {
        return TOS_ACCEPTED_FILE.exists();
    }

    public void setTOSAccepted() {
        try {
            boolean created = TOS_ACCEPTED_FILE.createNewFile();

            if (!created) {
                LOGGER.fatal("Failed to create TOSACCEPTED.lock!");
            }
        } catch (IOException e) {
            LOGGER.fatal("Failed to create TOSACCEPTED.lock!");
            e.printStackTrace();
        }
    }
}