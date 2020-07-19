package org.mediamod.mediamod.discord;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRPC;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.mediamod.mediamod.MediaMod;
import org.mediamod.mediamod.config.Settings;
import org.mediamod.mediamod.event.MediaInfoUpdateEvent;
import org.mediamod.mediamod.media.MediaHandler;
import org.mediamod.mediamod.media.core.api.MediaInfo;
import org.mediamod.mediamod.util.Metadata;
import org.mediamod.mediamod.util.Multithreading;

import javax.annotation.Nullable;

/**
 * The class that manages the discord rich presence feature
 */
public class RichPresenceManager {
    private final DiscordRPC rpc;
    private final DiscordRichPresence richPresence;
    private final String applicationID;

    private Thread callbackThread;

    public RichPresenceManager(String applicationID) {
        rpc = DiscordRPC.INSTANCE;
        richPresence = new DiscordRichPresence();

        this.applicationID = applicationID;
    }

    /**
     * Connects to the discord client
     */
    public void start() {
        if(!Settings.DISCORD_RPC_ENABLED) return;

        DiscordEventHandlers handlers = new DiscordEventHandlers();
        handlers.ready = (user -> MediaMod.INSTANCE.logger.info("Rich Presence Ready"));
        handlers.disconnected = ((errorCode, message) -> MediaMod.INSTANCE.logger.info("Rich Presence Disconnected"));

        rpc.Discord_Initialize(applicationID, handlers, true, null);

        if(MediaHandler.instance.getCurrentService() != null) {
            Multithreading.runAsync(() -> setPresenceInfo(MediaHandler.instance.getCurrentMediaInfo()));
        } else {
            setPresenceInfo(null);
        }

        callbackThread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted() && Settings.DISCORD_RPC_ENABLED) {
                rpc.Discord_RunCallbacks();

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    rpc.Discord_Shutdown();
                    break;
                }
            }
        }, "RPC-Callback-Handler");

        callbackThread.start();
    }

    /**
     * Disconnects from the client
     */
    public void stopPresence() {
        rpc.Discord_ClearPresence();
        rpc.Discord_Shutdown();

        callbackThread.interrupt();
    }

    /**
     * Sends the client the new presence information
     * @param info: The media information, can be null
     */
    public void setPresenceInfo(@Nullable MediaInfo info) {
        if(info == null || info.track == null) {
            richPresence.state = "No media playing!";
            richPresence.details = "Listening to music in Minecraft";
        } else {
            richPresence.startTimestamp = (System.currentTimeMillis() - info.timestamp) / 1000;
            richPresence.endTimestamp = richPresence.startTimestamp + info.track.duration / 1000;
            richPresence.state = info.track.name + " by " + info.track.artists[0].name;
            richPresence.details = "Listening to music in Minecraft";

            richPresence.smallImageKey = info.isPlaying ? "playing" : "paused";
            richPresence.smallImageText = info.isPlaying ? "Playing" : "Paused";
        }

        richPresence.largeImageKey = "mediamod";
        richPresence.largeImageText = "MediaMod v" + Metadata.VERSION;


        rpc.Discord_UpdatePresence(richPresence);
    }


    /**
     * Fired when the song changes
     * @see MediaInfoUpdateEvent
     */
    @SubscribeEvent
    public void onMediaInfoChange(MediaInfoUpdateEvent event) {
        setPresenceInfo(event.mediaInfo);
    }
}
