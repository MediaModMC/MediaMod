package dev.conorthedev.mediamod.media.base;

import dev.conorthedev.mediamod.media.base.exception.HandlerInitializationException;
import dev.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;

/**
 * The base class for a MediaHandler
 */
public interface IMediaHandler {
    String getHandlerName();

    CurrentlyPlayingObject getCurrentTrack();

    void initializeHandler() throws HandlerInitializationException;

    boolean handlerReady();

    int getEstimatedProgressMs();

    boolean supportsSkipping();

    boolean supportsPausing();

    boolean skipTrack();

    boolean pausePlayTrack();
}
