package me.conorthedev.mediamod.media.spotify.api.track;

import me.conorthedev.mediamod.media.spotify.api.album.Album;

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
