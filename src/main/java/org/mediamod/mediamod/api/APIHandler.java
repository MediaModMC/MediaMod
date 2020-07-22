package org.mediamod.mediamod.api;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import net.minecraft.client.Minecraft;
import org.mediamod.mediamod.util.WebRequest;
import org.mediamod.mediamod.util.WebRequestType;

import java.math.BigInteger;
import java.util.Random;

/**
 * The class that handles analytics for MediaMod + my other mods
 */
public class APIHandler {
    public static final APIHandler instance = new APIHandler();

    public String requestSecret;

    public boolean connect() {
        try {
            JsonObject body = new JsonObject();
            body.addProperty("uuid", Minecraft.getMinecraft().getSession().getProfile().getId().toString());
            body.addProperty("mod", "mediamod");
            body.addProperty("serverID", getServerID());

            RegisterResponse registerResponse = WebRequest.requestToMediaMod(WebRequestType.POST, "api/register", body, RegisterResponse.class);
            if (registerResponse != null) {
                requestSecret = registerResponse.secret;

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    private String getServerID() throws AuthenticationException {
        GameProfile profile = Minecraft.getMinecraft().getSession().getProfile();
        String accessToken = Minecraft.getMinecraft().getSession().getToken();

        Random random = new Random();
        Random random1 = new Random(System.identityHashCode(new Object()));

        BigInteger randomBigInt = new BigInteger(128, random);
        BigInteger randomBigInt1 = new BigInteger(128, random1);

        BigInteger serverBigInt = randomBigInt.xor(randomBigInt1);

        String serverId = serverBigInt.toString(16);
        Minecraft.getMinecraft().getSessionService().joinServer(profile, accessToken, serverId);

        return serverId;
    }

    public void shutdown() {
        JsonObject body = new JsonObject();
        body.addProperty("uuid", Minecraft.getMinecraft().getSession().getProfile().getId().toString());
        body.addProperty("secret", requestSecret);

        try {
            WebRequest.requestToMediaMod(WebRequestType.POST, "api/offline", body);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static class RegisterResponse {
        public final String secret;

        public RegisterResponse(String secret) {
            this.secret = secret;
        }
    }
}
