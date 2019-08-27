package me.conorthedev.mediamod.media.spotify.api.track;

import com.google.common.collect.ArrayListMultimap;
import me.conorthedev.mediamod.media.spotify.api.album.Album;

/**
 * The implementation for a track in the Spotify API
 *
 * @author ConorTheDev
 */
public class Track {
    public Album album;
    //public String[] available_markets;
    //public int disc_number;
    public int duration_ms;
    //public boolean explicit;
    //public ArrayListMultimap<String, String> external_ids;
    //public ArrayListMultimap<String, String> external_urls;
    //public String href;
   // public String id;
   // public boolean is_playable;
    //public  TrackLink linked_from;
    // Restrictions should go here, but it is not documented
    public  String name;
   // public   int popularity;
    //public   String preview_url;
    //public  int track_number;
    //public   String type;
    //public   String uri;
}
