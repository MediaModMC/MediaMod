package dev.conorthedev.mediamod.event;

import dev.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SongChangeEvent extends Event {
    public CurrentlyPlayingObject currentTrack;

    public SongChangeEvent(CurrentlyPlayingObject currentTrackIn) {
        this.currentTrack = currentTrackIn;
    }
}
