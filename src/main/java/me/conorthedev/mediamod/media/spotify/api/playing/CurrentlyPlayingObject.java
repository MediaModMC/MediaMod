package me.conorthedev.mediamod.media.spotify.api.playing;

import me.conorthedev.mediamod.media.spotify.api.track.Track;

public class CurrentlyPlayingObject {
    public int progress_ms;
    public Track item;
    public boolean is_playing;
}
