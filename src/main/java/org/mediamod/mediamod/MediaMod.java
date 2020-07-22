package org.mediamod.mediamod;

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
import org.mediamod.mediamod.api.APIHandler;
import org.mediamod.mediamod.command.MediaModCommand;
import org.mediamod.mediamod.command.MediaModUpdateCommand;
import org.mediamod.mediamod.config.Settings;
import org.mediamod.mediamod.event.MediaInfoUpdateEvent;
import org.mediamod.mediamod.gui.PlayerOverlay;
import org.mediamod.mediamod.keybinds.KeybindInputHandler;
import org.mediamod.mediamod.keybinds.KeybindManager;
import org.mediamod.mediamod.levelhead.LevelheadIntegration;
import org.mediamod.mediamod.media.MediaHandler;
import org.mediamod.mediamod.media.core.api.MediaInfo;
import org.mediamod.mediamod.parties.PartyManager;
import org.mediamod.mediamod.util.*;

import java.io.File;

/**
 * The main class for MediaMod
 *
 * @author ConorTheDev
 * @see net.minecraftforge.fml.common.Mod
 */
@SuppressWarnings("unused")
@Mod(name = Metadata.NAME, modid = Metadata.MODID, version = Metadata.VERSION)
public class MediaMod {
    /**
     * The API Endpoint for MediaMod requests
     */
    public static final String ENDPOINT = "https://mediamodapi.conorthedev.me/";

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
    public final Logger logger = LogManager.getLogger("MediaMod");
    /**
     * If the client successfully registered with API, this will be true
     */
    public boolean authenticatedWithAPI = false;
    /**
     * A File which points to the MediaMod Data directory
     */
    public File mediamodDirectory;
    /**
     * If this is the first load of MediaMod
     */
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
        logger.info("MediaMod starting...");

        // Register event subscribers and commands
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(PlayerOverlay.INSTANCE);
        MinecraftForge.EVENT_BUS.register(PlayerMessenger.INSTANCE);
        MinecraftForge.EVENT_BUS.register(new LevelheadIntegration());

        ClientCommandHandler.instance.registerCommand(new MediaModCommand());
        ClientCommandHandler.instance.registerCommand(new MediaModUpdateCommand());

        mediamodDirectory = new File(FMLClientHandler.instance().getClient().mcDataDir, "mediamod");
        if (!mediamodDirectory.exists()) {
            logger.info("Creating necessary directories and files for first launch...");
            boolean mkdir = mediamodDirectory.mkdir();

            if (mkdir) {
                logger.info("Created necessary directories and files!");
            } else {
                logger.fatal("Failed to create necessary directories and files!");
            }
        }

        logger.info("Checking if MediaMod is up-to-date...");
        VersionChecker.checkVersion();

        logger.info("Loading Configuration...");
        Settings.loadConfig();

        Multithreading.runAsync(() -> {
            logger.info("Attempting to connect with MediaMod API");

            authenticatedWithAPI = APIHandler.instance.connect();

            if (authenticatedWithAPI) {
                logger.info("Connected with MediaMod API!");
            } else {
                logger.warn("Failed to connect with MediaMod API");
            }

            // Load services
            MediaHandler mediaHandler = MediaHandler.instance;
            mediaHandler.loadAll();
        });

        Runtime.getRuntime().addShutdownHook(new Thread("MeidaMod Shutdown Thread") {
            public void run() {
                logger.info("Shutting down MediaMod");
                new UpdaterUtility().performUpdate();
                APIHandler.instance.shutdown();
            }
        });
    }

    /**
     * Fired when the world fires a tick
     *  
     * @param event WorldTickEvent
     * @see net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent
     */
    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (firstLoad && Minecraft.getMinecraft().thePlayer != null) {
            if (!VersionChecker.INSTANCE.isLatestVersion && !Settings.ALWAYS_AUTOUPDATE) {
                PlayerMessenger.sendMessage(ChatColor.RED + "MediaMod is out of date!", true);
                PlayerMessenger.sendMessage(ChatColor.GRAY + "Latest Version: " + ChatColor.WHITE + VersionChecker.INSTANCE.latestVersionInformation.name);
                PlayerMessenger.sendMessage(ChatColor.GRAY + "Your Version: " + ChatColor.WHITE + Metadata.VERSION);
                PlayerMessenger.sendMessage(ChatColor.GRAY + "Changelog: " + ChatColor.WHITE + VersionChecker.INSTANCE.latestVersionInformation.changelog);

                IChatComponent urlComponent = new ChatComponentText(ChatColor.GRAY + "Click this to automatically update now!");
                urlComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "mediamodupdate"));
                urlComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(ChatColor.translateAlternateColorCodes('&',
                        "&7Runs /mmupdate"))));
                PlayerMessenger.sendMessage(urlComponent);
            } else if (Settings.ALWAYS_AUTOUPDATE && !VersionChecker.INSTANCE.isLatestVersion) {
                Multithreading.runAsync(() -> {
                    UpdaterUtility utility = new UpdaterUtility();
                    utility.scheduleUpdate();
                });
            }

            if (!authenticatedWithAPI) {
                PlayerMessenger.sendMessage(ChatColor.RED + "Failed to authenticate with MediaMod API, this means services like Spotify will not work. Please click 'reconnect' in the MediaMod GUI!", true);
            }

            firstLoad = false;
        }
    }


    /**
     * Fired when the current song information changes
     *
     * @see MediaInfoUpdateEvent
     */
    @SubscribeEvent
    public void onMediaInfoChange(MediaInfoUpdateEvent event) {
        MediaInfo info = event.mediaInfo;
        if (info == null) return;

        if (Settings.ANNOUNCE_TRACKS) {
            PlayerMessenger.sendMessage(ChatColor.GRAY + "Current track: " + info.track.name + " by " + info.track.artists[0].name, true);
        }

        PartyManager.instance.updateInfo(info);
    }

}