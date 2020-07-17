package org.mediamod.mediamod.media.services.browser;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.apache.commons.io.IOUtils;
import org.mediamod.mediamod.MediaMod;
import org.mediamod.mediamod.config.Settings;
import org.mediamod.mediamod.media.core.IServiceHandler;
import org.mediamod.mediamod.media.core.api.MediaInfo;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

/**
 * The service that communicates with the browser extension to display media from sites like SoundCloud, YouTube and Apple Music
 */
public class BrowserService implements IServiceHandler {
    /**
     * A list of regex that indicate if the URL is allowed or not
     */
    public static final List<String> allowedOrigins = Arrays.asList("https://[^.]*\\.?youtube.com(/.*)?", "https://[^.]*\\.?music.apple.com(/.*)?", "https://[^.]*\\.?soundcloud.com(/.*)?", "https://[^.]*\\.?music.youtube.com(/.*)?");

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
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 9102), 0);
            server.createContext("/", new BrowserUpdateHandler());
            server.createContext("/disconnect", new BrowserDisconnectHandler());
            server.start();

            return true;
        } catch (IOException ignored) {
            MediaMod.INSTANCE.logger.warn("Failed to create Spotify callback server! Is the port already in use?");
            return false;
        }
    }

    /**
     * This indicates if the handler is ready for usage
     */
    public boolean isReady() {
        return mediaInfo != null && Settings.EXTENSION_ENABLED;
    }

    /**
     * Returns metadata about the current track
     * If no track is playing, or an error occurs it can return null
     */
    @Nullable
    public MediaInfo getCurrentMediaInfo() {
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
}

class BrowserUpdateHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        String requestOrigin = exchange.getRequestHeaders().get("Origin").get(0);

        for (String origin : BrowserService.allowedOrigins) {
            if (requestOrigin.matches(origin)) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", requestOrigin);
            }
        }

        if (!exchange.getResponseHeaders().containsKey("Access-Control-Allow-Origin")) {
            MediaMod.INSTANCE.logger.warn("Request to set information came from unknown domain... ignoring!");

            exchange.sendResponseHeaders(400, "Bad Request".length());

            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write("Bad Request".getBytes());
            outputStream.close();

            return;
        }

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", requestOrigin);
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String body = IOUtils.toString(exchange.getRequestBody());

        if (body == null || body.equals("")) {
            exchange.sendResponseHeaders(400, "Bad Request".length());

            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write("Bad Request".getBytes());
            outputStream.close();

            return;
        }

        MediaInfo info = new Gson().fromJson(body, MediaInfo.class);
        BrowserService.setCurrentMediaInfo(info);

        exchange.sendResponseHeaders(200, "OK".length());

        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write("OK".getBytes());
        outputStream.close();
    }
}

class BrowserDisconnectHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        String requestOrigin = exchange.getRequestHeaders().get("Origin").get(0);

        for (String origin : BrowserService.allowedOrigins) {
            if (requestOrigin.matches(origin)) {
                exchange.getResponseHeaders().add("Access-Control-Allow-Origin", requestOrigin);
            }
        }

        if (!exchange.getResponseHeaders().containsKey("Access-Control-Allow-Origin")) {
            MediaMod.INSTANCE.logger.warn("Request to set information came from unknown domain... ignoring!");

            exchange.sendResponseHeaders(400, "Bad Request".length());

            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write("Bad Request".getBytes());
            outputStream.close();

            return;
        }

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", requestOrigin);
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        BrowserService.setCurrentMediaInfo(null);

        exchange.sendResponseHeaders(200, "OK".length());

        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write("OK".getBytes());
        outputStream.close();
    }
}
