package me.conorthedev.mediamod.media;

import com.sun.net.httpserver.HttpServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * The class which recieves data from the browser extension & other integrations, big boy time
 *
 * @author ConorTheDev
 */
public class MediaHandler {
    /**
     * Instance of the MediaHandler
     */
    public static final MediaHandler INSTANCE = new MediaHandler();

    /**
     * Logger used to log info messages, debug messages, error messages & more
     *
     * @see org.apache.logging.log4j.Logger
     */
    private static final Logger LOGGER = LogManager.getLogger("MediaMod - MediaHandler");

    /**
     * The MediaHandler Server
     */
    private HttpServer server = null;

    /**
     * Start the Media Handler, listen for requests from the extension, etc.
     */
    public void initializeMediaHandler() {
        if (server != null) {
            LOGGER.warn("Attempt to initialize Media Handler when it was already initialized?");
        }

        try {
            LOGGER.info("Initializing Media Handler");
            LOGGER.info("Starting server on port 3099");
            // Create a HTTP Server for the extension to send requests too (http://localhost:3099)
            server = HttpServer.create(new InetSocketAddress(3099), 0);
            server.setExecutor(null);

            // Start the server
            server.start();
            LOGGER.info("Server started on port 3099");
        } catch (IOException e) {
            e.printStackTrace();
        }

        LOGGER.info("MediaHandler initialized and ready!");
    }
}
