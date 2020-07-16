package dev.conorthedev.mediamod.parties;

public class PartyTrack {
    final String _id;
    final int timestamp;
    final boolean paused;

    public PartyTrack(String id, Integer timestamp, Boolean paused) {
        this._id = id;
        this.timestamp = timestamp;
        this.paused = paused;
    }
}
