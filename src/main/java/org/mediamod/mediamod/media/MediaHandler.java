package org.mediamod.mediamod.media;

import org.mediamod.mediamod.MediaMod;
import org.mediamod.mediamod.media.core.IServiceHandler;
import org.mediamod.mediamod.media.core.api.MediaInfo;
import org.mediamod.mediamod.media.services.browser.BrowserService;
import org.mediamod.mediamod.media.services.spotify.SpotifyService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;

/**
 * The class that manages which service is currently being used and more
 */
public class MediaHandler {
    /**
     * An instance of the current class for usage across all other classes
     */
    public static final MediaHandler instance = new MediaHandler();

    /**
     * An array of services handlers that should be loaded
     */
    private final ArrayList<IServiceHandler> servicesToLoad = new ArrayList<>();

    /**
     * An array of loaded services handlers
     */
    private final ArrayList<IServiceHandler> loadedServices = new ArrayList<>();

    /**
     * An array of service handlers that failed to load
     */
    private final ArrayList<IServiceHandler> failedServices = new ArrayList<>();

    /**
     * Load all services to prepare for usage
     *
     * @see IServiceHandler#load()
     */
    private void loadServices() {
        for (IServiceHandler serviceHandler : servicesToLoad) {
            MediaMod.INSTANCE.LOGGER.info("Loading '" + serviceHandler.displayName() + "'");

            boolean loaded = serviceHandler.load();
            if (loaded) {
                loadedServices.add(serviceHandler);
            } else {
                failedServices.add(serviceHandler);
            }
        }
    }

    /**
     * Add all default handlers and prepare them for usage
     *
     * @see IServiceHandler
     */
    public void loadAll() {
        MediaMod.INSTANCE.LOGGER.info("Loading service handlers...");

        if (!loadedServices.isEmpty()) {
            MediaMod.INSTANCE.LOGGER.warn("Attempt to load services when they have already been loaded! Ignoring");
            return;
        }

        addService(new SpotifyService());
        addService(new BrowserService());

        loadServices();

        MediaMod.INSTANCE.LOGGER.info("Successfully loaded " + loadedServices.size() + " services");
        if (!failedServices.isEmpty()) {
            MediaMod.INSTANCE.LOGGER.warn("Failed to load " + failedServices.size() + " services");
        }
    }

    /**
     * Adds a service to the servicesToLoad array and sorts the array by priority (0 being the highest)
     *
     * @param serviceHandler: Service to add
     * @see IServiceHandler#compareTo(IServiceHandler)
     */
    public void addService(@Nonnull IServiceHandler serviceHandler) {
        MediaMod.INSTANCE.LOGGER.info("Preparing to load " + serviceHandler.displayName());

        servicesToLoad.add(serviceHandler);
        servicesToLoad.sort(IServiceHandler::compareTo);
    }

    /**
     * Returns the current service in use (decided by if they are ready or not)
     * If no handlers are ready, it will return null
     *
     * @see IServiceHandler#compareTo(IServiceHandler)
     * @see IServiceHandler#isReady()
     */
    public @Nullable
    IServiceHandler getCurrentService() {
        for (IServiceHandler handler : loadedServices) {
            if (handler.isReady()) {
                return handler;
            }
        }

        return null;
    }

    /**
     * Returns the current media information from the current service handler
     * Can return null if no media is available or no services are available
     *
     * @see MediaInfo
     */
    public @Nullable MediaInfo getCurrentMediaInfo() {
        IServiceHandler serviceHandler = getCurrentService();
        return serviceHandler != null ? serviceHandler.getCurrentMediaInfo() : null;
    }
}
