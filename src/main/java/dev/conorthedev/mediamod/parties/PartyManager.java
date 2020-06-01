package dev.conorthedev.mediamod.parties;

import com.google.gson.JsonObject;
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
    private JsonObject uuidBody = new JsonObject();
    private String partyCode;
    private String requestSecret;

    public PartyManager() {
        MinecraftForge.EVENT_BUS.register(this);
        uuidBody.addProperty("uuid", Minecraft.getMinecraft().getSession().getProfile().getId().toString());
    }

    public String startParty() {
        try {
            PartyStartResponse response = WebRequest.requestToMediaMod(WebRequestType.POST, "startParty", uuidBody, PartyStartResponse.class);
            if(response == null) {
                return "";
            }

            canStartParty = false;
            partyCode = response.code;
            requestSecret = response.secret;
            return partyCode;
        } catch (IOException ignored) {
            return "";
        }
    }

    public boolean leaveParty() {
        try {
            boolean success = WebRequest.requestToMediaMod(WebRequestType.POST, "leaveParty", uuidBody) == 200;
            canStartParty = success;
            
            return success;
        } catch (IOException ignored) {
            return false;
        }
    }

    public void publishSongChange(CurrentlyPlayingObject track) {
        try {
            JsonObject body = uuidBody;
            body.addProperty("code", partyCode);
            body.addProperty("trackId", track.item.id);
            body.addProperty("secret", requestSecret);
            WebRequest.requestToMediaMod(WebRequestType.POST, "songChange", uuidBody);
        } catch (IOException ignored) {}
    }

    public boolean canStartParty() {
        return canStartParty;
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
