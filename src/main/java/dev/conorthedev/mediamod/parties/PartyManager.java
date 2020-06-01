package dev.conorthedev.mediamod.parties;

import com.google.gson.JsonObject;
import dev.conorthedev.mediamod.util.WebRequest;
import dev.conorthedev.mediamod.util.WebRequestType;
import net.minecraft.client.Minecraft;

import java.io.IOException;

public class PartyManager {
    private boolean canStartParty = true;

    public String startParty() {
        JsonObject body = new JsonObject();
        body.addProperty("uuid", Minecraft.getMinecraft().getSession().getProfile().getId().toString());

        try {
            PartyStartResponse response = WebRequest.requestToMediaMod(WebRequestType.POST, "startParty", body, PartyStartResponse.class);
            if(response == null) {
                return "";
            }

            canStartParty = false;
            return response.code;
        } catch (IOException ignored) {
            return "";
        }
    }

    public boolean leaveParty() {
        JsonObject body = new JsonObject();
        body.addProperty("uuid", Minecraft.getMinecraft().getSession().getProfile().getId().toString());

        try {
            boolean success = WebRequest.requestToMediaMod(WebRequestType.POST, "leaveParty", body) == 200;
            canStartParty = success;
            
            return success;
        } catch (IOException ignored) {
            return false;
        }
    }

    public boolean canStartParty() {
        return canStartParty;
    }
}

class PartyStartResponse {
    final String code;

    PartyStartResponse(String code) {
        this.code = code;
    }
}
