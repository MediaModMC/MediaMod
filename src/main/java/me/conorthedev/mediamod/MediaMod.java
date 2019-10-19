package me.conorthedev.mediamod;

import me.conorthedev.mediamod.base.BaseMod;
import me.conorthedev.mediamod.command.MediaModCommand;
import me.conorthedev.mediamod.config.Settings;
import me.conorthedev.mediamod.gui.PlayerOverlay;
import me.conorthedev.mediamod.keybinds.KeybindInputHandler;
import me.conorthedev.mediamod.keybinds.KeybindManager;
import me.conorthedev.mediamod.media.base.ServiceHandler;
import me.conorthedev.mediamod.media.browser.BrowserHandler;
import me.conorthedev.mediamod.media.spotify.SpotifyHandler;
import me.conorthedev.mediamod.util.Metadata;
import me.conorthedev.mediamod.util.Multithreading;
import me.conorthedev.mediamod.util.PlayerMessager;
import me.conorthedev.mediamod.util.VersionChecker;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;

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
    public final boolean DEVELOPMENT_ENVIRONMENT = fieldExists(Minecraft.class, "theMinecraft");

    private boolean firstLoad = true;

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

        // Register with analytics
        if (FMLClientHandler.instance().getClient().gameSettings.snooperEnabled && getTOSAccepted()) {
            Multithreading.runAsync(() -> {
                LOGGER.info("Attempting to register with analytics...");

                boolean successful = BaseMod.init();

                if (successful) {
                    LOGGER.info("Successfully registered with analytics!");
                } else {
                    LOGGER.error("Failed to register with analytics...");
                }
            });
        } else {
            LOGGER.info("Skipping registration with analytics!");
        }

        // Check if MediaMod is up-to-date
        LOGGER.info("Checking if MediaMod is up-to-date...");
        VersionChecker.checkVersion();

        if (VersionChecker.INSTANCE.IS_LATEST_VERSION) {
            LOGGER.info("MediaMod is up-to-date!");
        } else {
            LOGGER.warn("MediaMod is NOT up-to-date! Latest Version: v" + VersionChecker.INSTANCE.LATEST_VERSION_INFO.latestVersionS + " Your Version: v" + Metadata.VERSION);
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

    /**
     * Fired when the world fires a tick
     *
     * @param event WorldTickEvent
     * @see net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent
     */
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (firstLoad && !VersionChecker.INSTANCE.IS_LATEST_VERSION && Minecraft.getMinecraft().player != null) {
            PlayerMessager.sendMessage("&cMediaMod is out of date!" +
                    "\n&7Latest Version: &r&lv" + VersionChecker.INSTANCE.LATEST_VERSION_INFO.latestVersionS +
                    "\n&7Changelog: &r&l" + VersionChecker.INSTANCE.LATEST_VERSION_INFO.changelog);
            firstLoad = false;
        }
    }

    /**
     * Checks if a field exists by the field name
     *
     * @param clazz     - the class the field can be in
     * @param fieldName - the field name
     * @return boolean
     */
    private boolean fieldExists(Class<?> clazz, String fieldName) {
        try {
            clazz.getDeclaredField(fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
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