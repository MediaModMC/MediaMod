package me.conorthedev.mediamod.media.spotify.api.playing;

import com.google.common.collect.ArrayListMultimap;
import me.conorthedev.mediamod.media.spotify.api.track.Track;

/**
 * The implementation for a "Currently Playing Object" in the Spotify API
 *
 * @author ConorTheDev
 * @see "https://developer.spotify.com/documentation/web-api/reference/player/get-the-users-currently-playing-track/"
 */
public class CurrentlyPlayingObject {
    //public CurrentlyPlayingContext context;
    //public int timestamp;
    //public int progress_ms;
    //public boolean is_playing;
    public Track item;
    //public String currently_playing_type;

    private static class CurrentlyPlayingContext {
        // The uri of the context
        public String uri;
        // The href of the context, or null if not available
        public String href;
        // The external_urls of the context, or null if not available
        public ArrayListMultimap<String, String> external_urls;
        // The object type of the item’s context. Can be one of album , artist or playlist
        public String type;
    }
}