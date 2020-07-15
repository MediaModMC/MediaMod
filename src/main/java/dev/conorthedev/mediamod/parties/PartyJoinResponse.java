package dev.conorthedev.mediamod.parties;

public class PartyJoinResponse {
    public final Boolean success;
    public final String host;

    PartyJoinResponse(Boolean success, String host) {
        this.success = success;
        this.host = host;
    }
}
