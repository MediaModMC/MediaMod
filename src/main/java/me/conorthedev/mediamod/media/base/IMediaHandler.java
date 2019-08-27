package me.conorthedev.mediamod.media.base;

import me.conorthedev.mediamod.media.base.exception.HandlerInitializationException;
import me.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;

/**
 * The base class for a MediaHandler
 */
public interface IMediaHandler {
    String getHandlerName();
    CurrentlyPlayingObject getCurrentTrack();
    void initializeHandler() throws HandlerInitializationException;
    boolean handlerReady();
}
