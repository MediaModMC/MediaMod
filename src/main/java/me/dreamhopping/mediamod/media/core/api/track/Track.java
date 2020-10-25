package me.dreamhopping.mediamod.media.core.api.track;

import com.google.gson.annotations.SerializedName;

/**
 * Information about the current track
 * <p>
 * Adapted from https://developer.spotify.com/documentation/web-api/reference/object-model/#track-object-full
 */
public class Track {
    @SerializedName("id")
    public String identifier;

    @SerializedName("duration_ms")
    public int duration;

    public String name;

    public Album album;
    public Artist[] artists;
}
