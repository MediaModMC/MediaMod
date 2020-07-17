package org.mediamod.mediamod.parties.responses;

public class PartyJoinResponse {
    public final Boolean success;
    public final String host;

    PartyJoinResponse(Boolean success, String host) {
        this.success = success;
        this.host = host;
    }
}
