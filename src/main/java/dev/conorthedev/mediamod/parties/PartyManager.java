package dev.conorthedev.mediamod.parties;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.conorthedev.mediamod.MediaMod;
import dev.conorthedev.mediamod.event.SongChangeEvent;
import dev.conorthedev.mediamod.media.base.ServiceHandler;
import dev.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;
import dev.conorthedev.mediamod.media.spotify.api.track.Track;
import dev.conorthedev.mediamod.util.WebRequest;
import dev.conorthedev.mediamod.util.WebRequestType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;

public class PartyManager {
    private String partyCode = "";
    private String partySecret = "";

    private Boolean isHostOfParty = false;
    private Boolean isParticipatingInParty = false;

    public PartyManager() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    public PartyStartResponse startParty() {
        System.out.println(new Gson().toJson(this.getPartyTrack()));

        try {
            JsonObject body = new JsonObject();
            body.addProperty("secret", MediaMod.INSTANCE.coreMod.secret);
            body.addProperty("uuid", MediaMod.INSTANCE.coreMod.getUUID());
            body.addProperty("currentTrack", new Gson().toJson(this.getPartyTrack()));

            PartyStartResponse response = WebRequest.requestToMediaMod(WebRequestType.POST, "api/party/start", body, PartyStartResponse.class);
            if (response == null) {
                return null;
            }

            isHostOfParty = true;
            isParticipatingInParty = true;
            partyCode = response.code;
            partySecret = response.secret;

            return response;
        } catch (IOException ignored) {
            return null;
        }
    }

    public PartyJoinResponse joinParty(String inputCode) {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("uuid", MediaMod.INSTANCE.coreMod.getUUID());
            body.addProperty("secret", MediaMod.INSTANCE.coreMod.secret);
            body.addProperty("code", inputCode);

            PartyJoinResponse response = WebRequest.requestToMediaMod(WebRequestType.POST, "joinParty", body, PartyJoinResponse.class);

            if (response != null && response.success) {
                partyCode = inputCode;
                isHostOfParty = false;
                isParticipatingInParty = true;
            }

            return response;
        } catch (IOException ignored) {
            return null;
        }
    }

    public boolean leaveParty() {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("uuid", MediaMod.INSTANCE.coreMod.getUUID());
            body.addProperty("secret", MediaMod.INSTANCE.coreMod.secret);
            body.addProperty("partyCode", partyCode);

            if(isHostOfParty) {
                body.addProperty("partySecret", partySecret);
            }

            boolean success = WebRequest.requestToMediaMod(WebRequestType.POST, "api/party/leave", body) == 200;

            if(success) {
                isParticipatingInParty = false;
                isHostOfParty = false;
            }

            return success;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void publishSongChange() {
        try {
            if (isHostOfParty && isParticipatingInParty) {
                JsonObject body = new JsonObject();
                body.addProperty("uuid", MediaMod.INSTANCE.coreMod.getUUID());
                body.addProperty("track", new Gson().toJson(this.getPartyTrack()));
                body.addProperty("code", partyCode);
                body.addProperty("secret", MediaMod.INSTANCE.coreMod.secret);
                body.addProperty("partySecret", partySecret);

                WebRequest.requestToMediaMod(WebRequestType.POST, "api/party/update", body);
            }
        } catch (IOException ignored) {
        }
    }

    public boolean isInParty() {
        return MediaMod.INSTANCE.partyManager.isParticipatingInParty;
    }

    public PartyTrack getPartyTrack() {
        CurrentlyPlayingObject track = ServiceHandler.INSTANCE.getCurrentMediaHandler().getCurrentTrack();
        return new PartyTrack(track.item.id, track.progress_ms, !track.is_playing);
    }

    public String getCurrentTrack() {
       /* System.out.println("Getting current track!");
        if (isParticipatingInParty()) {
            try {
                uuidBody.addProperty("code", partyCode);
                PartyInfoResponse response = WebRequest.requestToMediaMod(WebRequestType.POST, "partyInfo", uuidBody, PartyInfoResponse.class);
                uuidBody.remove("code");

                if (response != null) {
                    System.out.println("Track: " + response.track);
                    return response.track;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/
        return "";
    }

    @SubscribeEvent
    public void onSongChange(SongChangeEvent event) {
        publishSongChange();
    }
}
