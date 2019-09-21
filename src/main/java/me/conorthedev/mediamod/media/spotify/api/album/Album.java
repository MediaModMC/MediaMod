package me.conorthedev.mediamod.media.spotify.api.album;

import me.conorthedev.mediamod.media.spotify.api.artist.ArtistSimplified;

/**
 * The implementation for an album in the Spotify API
 *
 * @author ConorTheDev
 */
public class Album {
    public ArtistSimplified[] artists;
    public AlbumImage[] images;
    public String name;
}
