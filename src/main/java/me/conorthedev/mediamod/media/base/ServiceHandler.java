package me.conorthedev.mediamod.media.base;

import me.conorthedev.mediamod.media.base.exception.HandlerInitializationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;

/**
 * The class that handles all of the services
 *
 * @author ConorTheDev
 */
public class ServiceHandler {
    /**
     * An instance of the ServiceHandler class
     */
    public static final ServiceHandler INSTANCE = new ServiceHandler();

    /**
     * Logger used to log info messages, debug messages, error messages & more
     *
     * @see org.apache.logging.log4j.Logger
     */
    private final Logger LOGGER = LogManager.getLogger("MediaMod - MediaHandler");

    /**
     * List of Media Handlers registered
     */
    private ArrayList<IMediaHandler> MEDIA_HANDLERS = new ArrayList<>();

    /**
     * Register a Media Handler
     *
     * @param mediaHandler - Media Handler to register
     */
    public void registerHandler(IMediaHandler mediaHandler) {
        LOGGER.info("Registering Handler: " + mediaHandler.getHandlerName());
        MEDIA_HANDLERS.add(mediaHandler);
    }

    /**
     * Initialize all Media Handlers
     */
    public void initializeHandlers() {
        for (IMediaHandler handler : MEDIA_HANDLERS) {
            LOGGER.info("Initializing Handler: " + handler.getHandlerName());
            try {
                handler.initializeHandler();
            } catch (HandlerInitializationException e) {
                LOGGER.error("Error whilst initializing handler: " + handler.getHandlerName());
                e.printStackTrace();
            }
        }
    }
}
