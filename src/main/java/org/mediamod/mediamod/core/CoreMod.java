package org.mediamod.mediamod.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationException;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mediamod.mediamod.MediaMod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.stream.Collectors;

public class CoreMod {
    public final Logger LOGGER;
    private final String modID;
    public String secret;

    public CoreMod(String modIDin) {
        secret = "";
        modID = modIDin;
        LOGGER = LogManager.getLogger("CoreMod (" + modIDin + ")");

        Runtime.getRuntime().addShutdownHook(new Thread("CoreMod (" + modID + ") Shutdown Thread") {
            public void run() {
                shutdown();
            }
        });
    }

    public String getUUID() {
        return Minecraft.getMinecraft().getSession().getProfile().getId().toString();
    }

    public boolean register() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            if (!Minecraft.getMinecraft().isSnooperEnabled()) return false;

            LOGGER.info("Attempting to register with CoreMod API...");

            URL url = new URL(MediaMod.ENDPOINT + "api/register");

            JsonObject obj = new JsonObject();
            obj.addProperty("uuid", getUUID());
            obj.addProperty("mod", modID);
            obj.addProperty("serverID", getServerID());

            String content = obj.toString();

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", modID + "/1.0");
            connection.setRequestProperty("Content-Type", "application/json");

            connection.setDoOutput(true);
            connection.getOutputStream().write(content.getBytes(StandardCharsets.UTF_8));

            connection.connect();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response = reader.lines().collect(Collectors.joining());

            if (connection.getResponseCode() == 200) {
                RegisterResponse registerResponse = new Gson().fromJson(response, RegisterResponse.class);
                secret = registerResponse.secret;

                LOGGER.info("Successfully registered with CoreMod API!");
                return true;
            } else {
                LOGGER.info("Failed to register with CoreMod API... Response: " + response);
                return false;
            }
        } catch (Exception ignored) {
            return false;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
        if (!Minecraft.getMinecraft().isSnooperEnabled() && secret.equals("")) return;

        LOGGER.info("Shutting down CoreMod (" + modID + ")");
        try {
            URL url = new URL(MediaMod.ENDPOINT + "api/offline");

            JsonObject obj = new JsonObject();
            obj.addProperty("uuid", getUUID());
            obj.addProperty("secret", secret);

            HttpURLConnection connection;

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", modID + "/1.0");
            connection.setRequestProperty("Content-Type", "application/json");

            connection.setDoOutput(true);
            connection.getOutputStream().write(obj.toString().getBytes(StandardCharsets.UTF_8));

            connection.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String response = reader.lines().collect(Collectors.joining());
            LOGGER.info(response);

            connection.disconnect();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class RegisterResponse {
        final String secret;

        RegisterResponse(String secret) {
            this.secret = secret;
        }
    }
}