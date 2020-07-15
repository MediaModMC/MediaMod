package dev.conorthedev.mediamod.parties;

import com.google.gson.JsonObject;
import dev.conorthedev.mediamod.MediaMod;
import dev.conorthedev.mediamod.event.SongChangeEvent;
import dev.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;
import dev.conorthedev.mediamod.util.WebRequest;
import dev.conorthedev.mediamod.util.WebRequestType;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;

public class PartyManager {
    private boolean canStartParty = true;
    private boolean isHostOfParty = false;
    private JsonObject uuidBody = new JsonObject();
    private String partyCode;
    private String requestSecret;

    public PartyManager() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public String startParty() {
        try {
            uuidBody.addProperty("uuid", MediaMod.INSTANCE.coreMod.getUUID());
            PartyStartResponse response = WebRequest.requestToMediaMod(WebRequestType.POST, "startParty", uuidBody, PartyStartResponse.class);
            if(response == null) {
                return "";
            }

            canStartParty = false;
            isHostOfParty = true;
            partyCode = response.code;
            requestSecret = response.secret;
            return partyCode;
        } catch (IOException ignored) {
            return "";
        }
    }

    public PartyJoinResponse joinParty(String inputCode) {
        try {
            uuidBody.addProperty("uuid", MediaMod.INSTANCE.coreMod.getUUID());
            uuidBody.addProperty("code", inputCode);
            PartyJoinResponse response = WebRequest.requestToMediaMod(WebRequestType.POST, "joinParty", uuidBody, PartyJoinResponse.class);
            uuidBody.remove("code");

            if(response != null && response.success) {
                canStartParty = false;
                partyCode = inputCode;
                isHostOfParty = false;
            }

            return response;
        } catch (IOException ignored) {
            return null;
        }
    }

    public boolean leaveParty() {
        try {
            uuidBody.addProperty("secret", requestSecret);
            boolean success = WebRequest.requestToMediaMod(WebRequestType.POST, "leaveParty", uuidBody) == 200;
            uuidBody.remove("secret");

            canStartParty = success;
            isHostOfParty = success;

            return success;
        } catch (IOException ignored) {
            return false;
        }
    }

    public void publishSongChange(CurrentlyPlayingObject track) {
        try {
            if(isHostOfParty && !canStartParty) {
                JsonObject body = uuidBody;
                body.addProperty("code", partyCode);
                body.addProperty("trackId", track.item.id);
                body.addProperty("secret", requestSecret);
                WebRequest.requestToMediaMod(WebRequestType.POST, "songChange", uuidBody);
            }
        } catch (IOException ignored) {}
    }

    public boolean canStartParty() {
        return canStartParty;
    }

    public boolean isParticipatingInParty() {
        return !isHostOfParty && !canStartParty;
    }

    public String getCurrentTrack() {
        System.out.println("Getting current track!");
        if(isParticipatingInParty()) {
            try {
                uuidBody.addProperty("code", partyCode);
                PartyInfoResponse response = WebRequest.requestToMediaMod(WebRequestType.POST, "partyInfo", uuidBody, PartyInfoResponse.class);
                uuidBody.remove("code");

                if(response != null) {
                    System.out.println("Track: " + response.track);
                    return response.track;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    @SubscribeEvent
    public void onSongChange(SongChangeEvent event) {
        publishSongChange(event.currentTrack);
    }

}

class PartyStartResponse {
    final String code;
    final String secret;

    PartyStartResponse(String code, String token) {
        this.secret = token;
        this.code = code;
    }
}

class PartyInfoResponse {
    final String track;

    PartyInfoResponse(String track) {
        this.track = track;
    }
}

