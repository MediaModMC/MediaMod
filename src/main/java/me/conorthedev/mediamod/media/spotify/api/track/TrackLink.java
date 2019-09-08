package me.conorthedev.mediamod.media.spotify.api.track;

import com.google.common.collect.ArrayListMultimap;

/**
 * The implementation for a "Track Link" in the Spotify API
 *
 * @see "https://developer.spotify.com/documentation/web-api/reference/object-model/#track-link"
 */
public class TrackLink {
    public ArrayListMultimap<String, String> external_urls;
    public String href;
    public String id;
    public String type;
    public String uri;
}
