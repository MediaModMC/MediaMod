package org.mediamod.mediamod.media;

import net.minecraftforge.common.MinecraftForge;
import org.mediamod.mediamod.MediaMod;
import org.mediamod.mediamod.event.MediaInfoUpdateEvent;
import org.mediamod.mediamod.media.core.IServiceHandler;
import org.mediamod.mediamod.media.core.api.MediaInfo;
import org.mediamod.mediamod.media.services.browser.BrowserService;
import org.mediamod.mediamod.media.services.spotify.SpotifyService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
     * A cached version of the current MediaInfo instance from the service, changes every 3 seconds
     */
    private MediaInfo cachedMediaInfo = null;

    /**
     * A cached version of the previous MediaInfo instance from the service, changes every 3 seconds
     */
    private MediaInfo previousMediaInfo = null;

    public MediaHandler() {
        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.scheduleAtFixedRate(() -> {
            try {
                if (getCurrentService() != null) {
                    cachedMediaInfo = getCurrentService().getCurrentMediaInfo();

                    if (previousMediaInfo == null || cachedMediaInfo == null || !previousMediaInfo.track.name.equals(cachedMediaInfo.track.name)) {
                        previousMediaInfo = cachedMediaInfo;
                        MinecraftForge.EVENT_BUS.post(new MediaInfoUpdateEvent(cachedMediaInfo));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    /**
     * Load all services to prepare for usage
     *
     * @see IServiceHandler#load()
     */
    private void loadServices() {
        for (IServiceHandler serviceHandler : servicesToLoad) {
            loadService(serviceHandler);
        }
    }

    /**
     * Loads a service handler for usage
     *
     * @param serviceHandler: the service handler to load
     */
    private void loadService(IServiceHandler serviceHandler) {
        MediaMod.INSTANCE.logger.info("Loading '" + serviceHandler.displayName() + "'");

        boolean loaded = serviceHandler.load();
        if (loaded) {
            loadedServices.add(serviceHandler);
        } else {
            MediaMod.INSTANCE.logger.warn("Failed to load '" + serviceHandler.displayName() + "'");
            failedServices.add(serviceHandler);
        }
    }

    /**
     * Add all default handlers and prepare them for usage
     *
     * @see IServiceHandler
     */
    public void loadAll() {
        MediaMod.INSTANCE.logger.info("Loading service handlers...");

        if (!loadedServices.isEmpty()) {
            MediaMod.INSTANCE.logger.warn("Attempt to load services when they have already been loaded! Ignoring");
            return;
        }

        addService(new SpotifyService());
        addService(new BrowserService());

        loadServices();

        MediaMod.INSTANCE.logger.info("Successfully loaded " + loadedServices.size() + " services");
        if (!failedServices.isEmpty()) {
            MediaMod.INSTANCE.logger.warn("Failed to load " + failedServices.size() + " services");
        }
    }

    /**
     * Adds a service to the servicesToLoad array and sorts the array by priority (0 being the highest)
     *
     * @param serviceHandler: Service to add
     * @see IServiceHandler#compareTo(IServiceHandler)
     */
    public void addService(@Nonnull IServiceHandler serviceHandler) {
        MediaMod.INSTANCE.logger.info("Preparing to load " + serviceHandler.displayName());

        servicesToLoad.add(serviceHandler);
        servicesToLoad.sort(IServiceHandler::compareTo);
    }

    /**
     * Reloads a service
     */
    public void reloadService(@Nonnull Class<? extends IServiceHandler> serviceHandlerClass) {
        try {
            IServiceHandler serviceHandlerInstance = serviceHandlerClass.newInstance();

            // Remove the original service
            failedServices.removeIf(serviceHandler -> serviceHandler.displayName().equals(serviceHandlerInstance.displayName()));
            loadedServices.removeIf(serviceHandler -> serviceHandler.displayName().equals(serviceHandlerInstance.displayName()));
            servicesToLoad.removeIf(serviceHandler -> serviceHandler.displayName().equals(serviceHandlerInstance.displayName()));

            addService(serviceHandlerInstance);
            loadService(serviceHandlerInstance);
        } catch (InstantiationException | IllegalAccessException ignored) {
            MediaMod.INSTANCE.logger.warn("Failed to reload service " + serviceHandlerClass);
        }
    }

    /**
     * Returns the current service in use (decided by if they are ready or not)
     * If no handlers are ready, it will return null
     *
     * @see IServiceHandler#compareTo(IServiceHandler)
     * @see IServiceHandler#isReady()
     */
    @Nullable
    public IServiceHandler getCurrentService() {
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
    public @Nullable
    MediaInfo getCurrentMediaInfo() {
        return cachedMediaInfo;
    }
}
