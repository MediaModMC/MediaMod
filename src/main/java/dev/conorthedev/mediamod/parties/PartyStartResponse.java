package dev.conorthedev.mediamod.parties;

public class PartyStartResponse {
    public final String code;
    public final String secret;

    PartyStartResponse(String code, String token) {
        this.secret = token;
        this.code = code;
    }
}
