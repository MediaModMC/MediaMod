package me.conorthedev.mediamod.media.spotify.api.album;

import me.conorthedev.mediamod.media.spotify.api.artist.ArtistSimplified;

/**
 * The implementation for an album in the Spotify API
 *
 * @author ConorTheDev
 */
public class Album {
    // public String album_group;
    // public String album_type;
    public ArtistSimplified[] artists;
    // public String[] available_markets;
    //  public ArrayListMultimap<String, String> external_urls;
    // public String href;
    // public String id;
    public AlbumImage[] images;
    public String name;
    //public String release_date;
    //public String release_date_precision;
}
