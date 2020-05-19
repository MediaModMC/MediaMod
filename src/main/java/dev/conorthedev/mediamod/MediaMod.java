package dev.conorthedev.mediamod;

import dev.conorthedev.mediamod.command.MediaModCommand;
import dev.conorthedev.mediamod.command.MediaModUpdateCommand;
import dev.conorthedev.mediamod.config.Settings;
import dev.conorthedev.mediamod.gui.PlayerOverlay;
import dev.conorthedev.mediamod.keybinds.KeybindInputHandler;
import dev.conorthedev.mediamod.keybinds.KeybindManager;
import dev.conorthedev.mediamod.media.base.ServiceHandler;
import dev.conorthedev.mediamod.media.browser.BrowserHandler;
import dev.conorthedev.mediamod.media.spotify.SpotifyHandler;
import dev.conorthedev.mediamod.util.ChatColor;
import dev.conorthedev.mediamod.util.Metadata;
import dev.conorthedev.mediamod.util.PlayerMessager;
import dev.conorthedev.mediamod.util.VersionChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
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

/**
 * The main class for MediaMod
 *
 * @author ConorTheDev
 * @see net.minecraftforge.fml.common.Mod
 */
@Mod(name = Metadata.NAME, modid = Metadata.MODID, version = Metadata.VERSION)
public class MediaMod {
    /**
     * The API Endpoint for MediaMod requests
     */
    public static final String ENDPOINT = "https://mediamodapi.cbyrne.dev/";

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
        KeybindManager.INSTANCE.register();
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
        //ClientCommandHandler.instance.registerCommand(new MediaModUpdateCommand());

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

        LOGGER.info("Checking if MediaMod is up-to-date...");
        VersionChecker.checkVersion();

        if (VersionChecker.INSTANCE.IS_LATEST_VERSION) {
            LOGGER.info("MediaMod is up-to-date!");
        } else {
            LOGGER.warn("MediaMod is NOT up-to-date! Latest Version: v" + VersionChecker.INSTANCE.LATEST_VERSION_INFO.latestVersionS + " Your Version: v" + Metadata.VERSION);
        }

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
        if (firstLoad && !VersionChecker.INSTANCE.IS_LATEST_VERSION && Minecraft.getMinecraft().thePlayer != null) {
            PlayerMessager.sendMessage("&cMediaMod is out of date!" +
                    "\n&7Latest Version: &r&lv" + VersionChecker.INSTANCE.LATEST_VERSION_INFO.latestVersionS +
                    "\n&7Changelog: &r&l" + VersionChecker.INSTANCE.LATEST_VERSION_INFO.changelog);

            /*IChatComponent urlComponent = new ChatComponentText(ChatColor.GRAY + "" + ChatColor.BOLD +  "Click this to automatically update now!");
            urlComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "mediamodupdate"));
            urlComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(ChatColor.translateAlternateColorCodes('&',
                    "&7Runs /mediamodupdate"))));
            PlayerMessager.sendMessage(urlComponent);*/
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
}