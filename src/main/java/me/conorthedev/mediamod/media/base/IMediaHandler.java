package me.conorthedev.mediamod.media.base;

import me.conorthedev.mediamod.media.base.exception.HandlerInitializationException;
import me.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;

public interface IMediaHandler {
    String getHandlerName();
    CurrentlyPlayingObject getCurrentTrack();
    void initializeHandler() throws HandlerInitializationException;
    boolean handlerReady();
    int getEstimatedProgressMs();
}
