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

    @SerializedName("error")
    public SpotifyError error;

    public static class SpotifyError extends Exception {
        @SerializedName("status")
        public final int status;
        @SerializedName("message")
        public final String message;

        SpotifyError(int status, String message) {
            this.status = status;
            this.message = message;
        }

        @Override
        public String toString() {
            return "Spotify error: status=" + status +", message='" + message+"'";
        }
    }
}

