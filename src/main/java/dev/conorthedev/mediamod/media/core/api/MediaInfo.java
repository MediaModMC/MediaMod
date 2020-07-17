package dev.conorthedev.mediamod.media.core.api;

import com.google.gson.annotations.SerializedName;
import dev.conorthedev.mediamod.media.core.api.track.Track;

public class MediaInfo {
    @SerializedName("progress_ms")
    public Integer timestamp;

    @SerializedName("is_playing")
    public boolean isPlaying;

    @SerializedName("item")
    public Track track;
}
