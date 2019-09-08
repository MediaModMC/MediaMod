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
    private final Logger LOGGER = LogManager.getLogger("MediaMod - ServiceHandler");

    /**
     * List of Media Handlers registered
     */
    private final ArrayList<IMediaHandler> MEDIA_HANDLERS = new ArrayList<>();

    /**
     * List of Media Handlers initalized
     */
    private final ArrayList<IMediaHandler> INITIALIED_HANDLERS = new ArrayList<>();

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
                INITIALIED_HANDLERS.add(handler);
            } catch (HandlerInitializationException e) {
                LOGGER.error("Error whilst initializing handler: " + handler.getHandlerName());
                e.printStackTrace();
            }
        }
    }

    /**
     * Get the current Media Handler
     */
    public IMediaHandler getCurrentMediaHandler() {
        if (INITIALIED_HANDLERS.size() > 0) {
            return INITIALIED_HANDLERS.get(0);
        } else {
            return null;
        }
    }
}
