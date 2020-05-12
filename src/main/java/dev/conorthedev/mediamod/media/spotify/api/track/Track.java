package dev.conorthedev.mediamod.media.spotify.api.track;

import dev.conorthedev.mediamod.media.spotify.api.album.Album;

/**
 * The implementation for a track in the Spotify API
 *
 * @author ConorTheDev
 */
public class Track {
    public Album album;
    public int duration_ms;
    public String name;
}
