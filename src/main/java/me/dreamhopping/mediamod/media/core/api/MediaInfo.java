package me.dreamhopping.mediamod.media.core.api;

import com.google.gson.annotations.SerializedName;
import me.dreamhopping.mediamod.media.core.api.track.Track;

public class MediaInfo {
    @SerializedName("progress_ms")
    public int timestamp;

    @SerializedName("is_playing")
    public boolean isPlaying;

    @SerializedName("item")
    public Track track;
}

