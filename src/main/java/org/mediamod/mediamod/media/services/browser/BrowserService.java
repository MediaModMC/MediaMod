package org.mediamod.mediamod.media.services.browser;

import com.google.gson.Gson;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.mediamod.mediamod.MediaMod;
import org.mediamod.mediamod.config.Settings;
import org.mediamod.mediamod.media.core.IServiceHandler;
import org.mediamod.mediamod.media.core.api.MediaInfo;
import org.mediamod.mediamod.util.Multithreading;

import javax.annotation.Nullable;
import java.net.InetSocketAddress;

/**
 * The service that communicates with the browser extension to display media from sites like SoundCloud, YouTube and Apple Music
 */
public class BrowserService implements IServiceHandler {
    /**
     * The current MediaInfo instance received from the browser
     */
    private static MediaInfo mediaInfo = null;

    /**
     * The last timestamp an estimation was performed at
     */
    private int lastTimestamp = 0;

    /**
     * The timestamp of the last estimation update
     */
    private long lastEstimationUpdate = 0;
    /**
     * An instance of our websocket server
     */
    private volatile BrowserSocketServer server;

    /**
     * The name to be shown in MediaMod Menus
     */
    public String displayName() {
        return "Browser Extension";
    }

    /**
     * This should initialise any needed variables, start any local servers, etc.
     *
     * @return If initialisation was successful
     */
    public boolean load() {
        Multithreading.runAsync(() -> {
            try {
                server = new BrowserSocketServer();
                server.start();
            } catch (Exception ignored) {
                MediaMod.INSTANCE.logger.warn("Failed to create Spotify callback server! Is the port already in use?");
            }
        });

        return true;
    }

    /**
     * This indicates if the handler is ready for usage
     */
    public boolean isReady() {
        return server != null && server.getConnections() != null && server.getConnections().size() >= 1 && Settings.EXTENSION_ENABLED;
    }

    /**
     * The priority of the service, this indicates if the mod should use this service instead of another if they are both ready
     */
    public int getPriority() {
        return 0;
    }

    /**
     * Returns metadata about the current track
     * If no track is playing, or an error occurs it can return null
     */
    @Nullable
    public MediaInfo getCurrentMediaInfo() {
        mediaInfo = server.getMediaInfo();

        if (mediaInfo != null) {
            this.lastEstimationUpdate = System.currentTimeMillis();
            this.lastTimestamp = mediaInfo.timestamp;
        }

        return mediaInfo;
    }

    /**
     * Returns the current MediaInfo from the browser
     *
     * @param info: The MediaInfo instance
     */
    public static void setCurrentMediaInfo(@Nullable MediaInfo info) {
        mediaInfo = info;
    }

    /**
     * Returns an estimation of the current progress of the track
     */
    public int getEstimatedProgress() {
        if (mediaInfo == null) return 0;

        if (mediaInfo.isPlaying) {
            int estimate = (int) (lastTimestamp + (System.currentTimeMillis() - lastEstimationUpdate));
            if (estimate > mediaInfo.track.duration) {
                estimate = mediaInfo.track.duration;
            }
            return estimate;
        } else {
            return lastTimestamp;
        }
    }

    /**
     * The web socket server for communication with the browser extension
     */
    static class BrowserSocketServer extends WebSocketServer {
        private volatile MediaInfo currentMediaInfo = null;
        private volatile MediaInfo previousMediaInfo = null;

        public BrowserSocketServer() {
            super(new InetSocketAddress(9102));
        }

        public void onOpen(WebSocket conn, ClientHandshake handshake) {
            MediaMod.INSTANCE.logger.info("Client Connected");
            broadcast("Hello");
            broadcast("Send");
        }

        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            MediaMod.INSTANCE.logger.info("Client Disconnected");
            BrowserService.setCurrentMediaInfo(null);
        }

        public void onMessage(WebSocket conn, String message) {
            if (!message.equals("Hello")) {
                Gson gson = new Gson();

                MediaInfo info = null;
                try {
                    info = gson.fromJson(message, MediaInfo.class);
                } catch (Exception ignored) {
                }

                currentMediaInfo = info;
            }
        }

        public void onError(WebSocket conn, Exception ex) {
            MediaMod.INSTANCE.logger.error("Error: ", ex);
        }

        public void onStart() {
            setConnectionLostTimeout(0);
            setConnectionLostTimeout(100);
        }

        /**
         * Queries the extension for the current MediaInfo
         */
        public MediaInfo getMediaInfo() {
            broadcast("Send");

            while (currentMediaInfo == previousMediaInfo) {
            }
            previousMediaInfo = currentMediaInfo;

            return currentMediaInfo;
        }
    }
}
