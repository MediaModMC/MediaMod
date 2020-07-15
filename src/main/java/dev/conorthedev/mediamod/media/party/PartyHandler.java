package dev.conorthedev.mediamod.media.party;

import dev.conorthedev.mediamod.MediaMod;
import dev.conorthedev.mediamod.media.base.AbstractMediaHandler;
import dev.conorthedev.mediamod.media.spotify.SpotifyHandler;
import dev.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;

public class PartyHandler extends AbstractMediaHandler {
    public String getHandlerName() {
        return "MediaMod Parties";
    }

    public CurrentlyPlayingObject getCurrentTrack() {
        SpotifyHandler.spotifyApi.setSongFromID(MediaMod.INSTANCE.partyManager.getCurrentTrack());
        return SpotifyHandler.INSTANCE.getCurrentTrack();
    }

    public void initializeHandler() {
    }

    public boolean handlerReady() {
        return MediaMod.INSTANCE.partyManager.isParticipatingInParty();
    }

    public boolean supportsSkipping() {
        return true;
    }

    public boolean supportsPausing() {
        return true;
    }

    public boolean skipTrack() {
        return SpotifyHandler.INSTANCE.skipTrack();
    }

    public boolean pausePlayTrack() {
        return SpotifyHandler.INSTANCE.pausePlayTrack();
    }
}
