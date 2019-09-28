package me.conorthedev.mediamod.media.spotify.api.playing;

import me.conorthedev.mediamod.media.spotify.api.track.Track;

/**
 * The implementation for a "Currently Playing Object" in the Spotify API
 *
 * @author ConorTheDev
 * @see "https://developer.spotify.com/documentation/web-api/reference/player/get-the-users-currently-playing-track/"
 */
public class CurrentlyPlayingObject {
    public int progress_ms;
    public Track item;
    public boolean is_playing;
}