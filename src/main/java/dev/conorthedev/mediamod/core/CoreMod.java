package dev.conorthedev.mediamod.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.conorthedev.mediamod.MediaMod;
import dev.conorthedev.mediamod.util.Multithreading;
import net.minecraft.client.Minecraft;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class CoreMod {
    public final Logger LOGGER;
    private final String modID;
    public String secret;

    public CoreMod(String modIDin) {
        modID = modIDin;
        LOGGER = LogManager.getLogger("CoreMod (" + modIDin + ")");

        Runtime.getRuntime().addShutdownHook(new Thread("CoreMod (" + modID +") Shutdown Thread") {
            public void run() {
                shutdown();
            }
        });
    }

    public String getUUID() {
        return Minecraft.getMinecraft().getSession().getProfile().getId().toString();
    }

    public void register() throws IOException {
        if(!Minecraft.getMinecraft().isSnooperEnabled()) return;

        LOGGER.info("Attempting to register with CoreMod API...");

        URL url = new URL(MediaMod.ENDPOINT + "api/register");

        JsonObject obj = new JsonObject();
        obj.addProperty("uuid", this.getUUID());
        obj.addProperty("mod", modID);

        String content = obj.toString();

        HttpURLConnection connection;
        BufferedReader reader;

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", modID + "/1.0");
        connection.setRequestProperty("Content-Type", "application/json");

        connection.setDoOutput(true);
        connection.getOutputStream().write(content.getBytes(StandardCharsets.UTF_8));

        connection.connect();

        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String response = reader.lines().collect(Collectors.joining());

        connection.disconnect();
        reader.close();

        if(connection.getResponseCode() == 200) {
            RegisterResponse registerResponse = new Gson().fromJson(response, RegisterResponse.class);
            secret = registerResponse.secret;

            LOGGER.info("Successfully registered with CoreMod API!");
        } else {
            LOGGER.info("Failed to register with CoreMod API... Response: " + response);
        }
    }

    public void shutdown() {
        if(!Minecraft.getMinecraft().isSnooperEnabled()) return;

        LOGGER.info("Shutting down CoreMod (" + modID + ")");
        try {
            URL url = new URL(MediaMod.ENDPOINT + "api/offline");

            JsonObject obj = new JsonObject();
            obj.addProperty("uuid", this.getUUID());
            obj.addProperty("secret", this.secret);

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
}

class RegisterResponse {
    final String secret;

    RegisterResponse(String secret) {
        this.secret = secret;
    }
}