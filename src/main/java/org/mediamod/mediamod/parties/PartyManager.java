package org.mediamod.mediamod.parties;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.mediamod.mediamod.MediaMod;
import org.mediamod.mediamod.media.MediaHandler;
import org.mediamod.mediamod.media.core.api.MediaInfo;
import org.mediamod.mediamod.parties.meta.PartyMediaInfo;
import org.mediamod.mediamod.parties.responses.PartyJoinResponse;
import org.mediamod.mediamod.parties.responses.PartyStartResponse;
import org.mediamod.mediamod.parties.responses.PartyStatusResponse;
import org.mediamod.mediamod.util.WebRequest;
import org.mediamod.mediamod.util.WebRequestType;

import java.io.IOException;

/**
 * The class which manages everything to do with creating, updating and leaving MediaMod Parties
 */
public class PartyManager {
    public static final PartyManager instance = new PartyManager();

    private volatile boolean isPartyHost = false;
    private volatile boolean isInParty = false;
    private volatile String partyCode = "";
    private volatile String partySecret = "";

    /**
     * Contacts the MediaMod API to create a party and also sends the current track information
     *
     * @see PartyStartResponse
     */
    public PartyStartResponse startParty() {
        if (isInParty) return null;

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("secret", MediaMod.INSTANCE.coreMod.secret);
        requestBody.addProperty("uuid", MediaMod.INSTANCE.coreMod.getUUID());

        MediaInfo mediaInfo = MediaHandler.instance.getCurrentMediaInfo();
        if (mediaInfo != null) {
            requestBody.addProperty("currentTrack", new Gson().toJson(MediaHandler.instance.getCurrentMediaInfo()));
        }

        try {
            PartyStartResponse response = WebRequest.requestToMediaMod(WebRequestType.POST, "api/party/start", requestBody, PartyStartResponse.class);

            if (response == null) {
                return null;
            } else {
                isPartyHost = true;
                isInParty = true;

                partyCode = response.code;
                partySecret = response.secret;
            }

            return response;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Joins a MediaMod with the provided partyCode
     *
     * @param partyCode: The code provided by the server to the host
     * @see PartyJoinResponse
     */
    public PartyJoinResponse joinParty(String partyCode) {
        if (partyCode == null || partyCode.length() != 6) return null;

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("secret", MediaMod.INSTANCE.coreMod.secret);
        requestBody.addProperty("uuid", MediaMod.INSTANCE.coreMod.getUUID());
        requestBody.addProperty("partyCode", partyCode);

        try {
            PartyJoinResponse response = WebRequest.requestToMediaMod(WebRequestType.POST, "api/party/join", requestBody, PartyJoinResponse.class);

            if (response != null && response.success) {
                this.partyCode = partyCode;
                isPartyHost = false;
                isInParty = true;
            }

            return response;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Contacts the MediaMod server to leave the current party or to delete the current one if the user is the host
     *
     * @return if the leave was successful
     */
    public boolean leaveParty() {
        if (partyCode == null || partyCode.length() != 6) return false;

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("secret", MediaMod.INSTANCE.coreMod.secret);
        requestBody.addProperty("uuid", MediaMod.INSTANCE.coreMod.getUUID());
        requestBody.addProperty("partyCode", partyCode);

        if (isPartyHost) {
            requestBody.addProperty("partySecret", partySecret);
        }

        try {
            boolean success = WebRequest.requestToMediaMod(WebRequestType.POST, "api/party/leave", requestBody) == 200;

            if (success) {
                partyCode = "";
                partySecret = "";
                isPartyHost = false;
                isInParty = false;

                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    /**
     * A public accessor for isInParty
     */
    public boolean isInParty() {
        return isInParty;
    }

    /**
     * A public accessor for isPartyHost
     */
    public boolean isPartyHost() {
        return isPartyHost;
    }

    /**
     * Contacts the MediaMod API to get the party's current Media Information
     *
     * @see MediaInfo
     */
    public PartyMediaInfo getPartyMediaInfo() {
        if (partyCode == null || partyCode.length() != 6) return null;

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("secret", MediaMod.INSTANCE.coreMod.secret);
        requestBody.addProperty("uuid", MediaMod.INSTANCE.coreMod.getUUID());
        requestBody.addProperty("partyCode", partyCode);

        try {
            PartyStatusResponse response = WebRequest.requestToMediaMod(WebRequestType.POST, "api/party/status", requestBody, PartyStatusResponse.class);

            if (response != null) {
                return response.info;
            }

            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Contacts the MediaMod API to create a party and also sends the current track information
     *
     * @see PartyStartResponse
     */
    public boolean updateInfo(MediaInfo info) {
        if (!isPartyHost) return false;

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("secret", MediaMod.INSTANCE.coreMod.secret);
        requestBody.addProperty("uuid", MediaMod.INSTANCE.coreMod.getUUID());
        requestBody.addProperty("partySecret", partySecret);
        requestBody.addProperty("partyCode", partyCode);

        if (info != null) {
            requestBody.addProperty("currentTrack", new Gson().toJson(new PartyMediaInfo(info.track.identifier)));
        }

        try {
            PartyStartResponse response = WebRequest.requestToMediaMod(WebRequestType.POST, "api/party/update", requestBody, PartyStartResponse.class);
            return response != null;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}

