package me.conorthedev.mediamod.media.browser;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import me.conorthedev.mediamod.config.Settings;
import me.conorthedev.mediamod.media.base.AbstractMediaHandler;
import me.conorthedev.mediamod.media.base.exception.HandlerInitializationException;
import me.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

/**
 * The class which receives data from the browser extension
 *
 * @author ConorTheDev
 */
public class BrowserHandler extends AbstractMediaHandler {
    /**
     * Instance of the MediaHandler
     */
    public static final BrowserHandler INSTANCE = new BrowserHandler();

    /**
     * Logger used to log info messages, debug messages, error messages & more
     *
     * @see org.apache.logging.log4j.Logger
     */
    private static final Logger LOGGER = LogManager.getLogger("MediaMod - BrowserHandler");

    /**
     * If the handler has been initialized or not
     */
    private static Boolean INITIALIZED = false;

    /**
     * The current track
     */
    private CurrentlyPlayingObject currentTrack;

    /**
     * Start the Media Handler, listen for requests from the extension, etc.
     */
    @Override
    public void initializeHandler() throws HandlerInitializationException {
        if (INITIALIZED) {
            LOGGER.warn("Attempt to initialize Media Handler when it was already initialized?");
        }

        try {
            LOGGER.info("Initializing Media Handler");

            // Create a HTTP Server for the extension to send requests to (http://localhost:9102)
            HttpServer server = HttpServer.create(new InetSocketAddress(9102), 0);
            server.setExecutor(null);
            server.createContext("/", new ConnectionCallbackHandler());
            server.createContext("/disconnect", new DisconnectionCallbackHandler());

            // Start the server
            server.start();
            LOGGER.info("Server started on port 9102");
        } catch (IOException e) {
            throw new HandlerInitializationException(e);
        }

        LOGGER.info("Browser Handler initialized and ready!");

        INITIALIZED = true;
    }

    @Override
    public String getHandlerName() {
        return "Browser Extension";
    }

    @Override
    public CurrentlyPlayingObject getCurrentTrack() {
        return BrowserHandler.INSTANCE.currentTrack;
    }

    @Override
    public boolean handlerReady() {
        if (!Settings.EXTENSION_ENABLED) {
            return false;
        } else {
            return BrowserHandler.INSTANCE.currentTrack != null;
        }
    }

    @Override
    public int getEstimatedProgressMs() {
        if (!this.equals(INSTANCE)) {
            return INSTANCE.getEstimatedProgressMs();
        } else {
            return super.getEstimatedProgressMs();
        }
    }

    /**
     * The connection callback handler
     */
    private static class ConnectionCallbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            t.getResponseHeaders().add("Access-Control-Allow-Origin", "http://localhost:9102");

            if (t.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
                t.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, OPTIONS");
                t.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type,Authorization");
                t.sendResponseHeaders(204, -1);
                return;
            }

            String requestBody = IOUtils.toString(t.getRequestBody());

            String response = "DENIED";

            JsonElement data = new JsonParser().parse(requestBody);

            if (data.isJsonObject()) {
                JsonObject object = data.getAsJsonObject();
                // These two things are crucial for rendering the player, while album and progress data isn't (we can just leave that out while rendering)
                if (object.has("item") && object.get("item").isJsonObject() && Settings.EXTENSION_ENABLED) {
                    JsonObject item = object.getAsJsonObject("item");
                    if (item.has("name") && item.get("name").isJsonPrimitive() && item.getAsJsonPrimitive("name").isString() && !item.getAsJsonPrimitive("name").getAsString().trim().isEmpty()) {
                        try {
                            Gson g = new Gson();
                            BrowserHandler.INSTANCE.currentTrack = g.fromJson(data, CurrentlyPlayingObject.class);
                            BrowserHandler.INSTANCE.lastProgressUpdate = System.currentTimeMillis();
                            if (BrowserHandler.INSTANCE.currentTrack != null) {
                                BrowserHandler.INSTANCE.paused = !BrowserHandler.INSTANCE.currentTrack.is_playing;
                                BrowserHandler.INSTANCE.durationMs = BrowserHandler.INSTANCE.currentTrack.item.duration_ms;
                                BrowserHandler.INSTANCE.lastProgressMs = BrowserHandler.INSTANCE.currentTrack.progress_ms;
                            } else {
                                BrowserHandler.INSTANCE.paused = true;
                                BrowserHandler.INSTANCE.durationMs = 0;
                                BrowserHandler.INSTANCE.lastProgressMs = 0;
                            }
                            response = "OK";
                        } catch (JsonSyntaxException ignored) {
                            // it'll send denied
                        }
                    }
                }
            }

            t.sendResponseHeaders(200, response.length());

            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    /**
     * The disconnection callback handler
     */
    private static class DisconnectionCallbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            BrowserHandler.INSTANCE.currentTrack = null;
            INITIALIZED = false;

            t.getResponseHeaders().add("Access-Control-Allow-Origin", "*");

            String response = "OK";

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
