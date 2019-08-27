package me.conorthedev.mediamod.media.browser;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import me.conorthedev.mediamod.media.base.IMediaHandler;
import me.conorthedev.mediamod.media.base.exception.HandlerInitializationException;
import me.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * The class which receives data from the browser extension
 *
 * @author ConorTheDev
 */
public class BrowserHandler implements IMediaHandler {
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
     * The MediaHandler Server
     */
    private HttpServer server = null;

    /**
     * Start the Media Handler, listen for requests from the extension, etc.
     */
    @Override
    public void initializeHandler() throws HandlerInitializationException  {
        if (INITIALIZED) {
            LOGGER.warn("Attempt to initialize Media Handler when it was already initialized?");
        }

        try {
            LOGGER.info("Initializing Media Handler");

            // Create a HTTP Server for the extension to send requests too (http://localhost:1388)
            server = HttpServer.create(new InetSocketAddress(1388), 0);
            server.setExecutor(null);
            server.createContext("/", new ConnectionCallbackHandler());

            // Start the server
            server.start();
            LOGGER.info("Server started on port 1388");
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
        // TODO
        return null;
    }

    @Override
    public boolean handlerReady() {
        return INITIALIZED;
    }

    /**
     * The connection callback handler
     */
    private static class ConnectionCallbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) {
            LOGGER.info("Established connection with MediaMod Browser Extension!");
        }
    }
}
