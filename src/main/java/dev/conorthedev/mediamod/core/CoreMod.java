package dev.conorthedev.mediamod.core;

import dev.conorthedev.mediamod.MediaMod;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class CoreMod {
    private final String modID;
    public CoreMod(String modIDin) {
        modID = modIDin;
    }

    public void register() throws IOException {
        URL url = new URL(MediaMod.ENDPOINT + "register");
        String string = "{ \"uuid\": \"" + Minecraft.getMinecraft().getSession().getProfile().getId().toString() + "\", \"currentMod\": \"" + modID + "\" }";
        System.out.println(string);
        HttpURLConnection connection;
        BufferedReader reader;

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", "MediaMod/1.0");
        connection.setRequestProperty("Content-Type", "text/plain; charset=UTF-8");

        connection.setDoOutput(true);
        connection.getOutputStream().write(string.getBytes(StandardCharsets.UTF_8));

        connection.connect();

        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String content = reader.lines().collect(Collectors.joining());

        connection.disconnect();
        reader.close();

        System.out.println("Response: " + content);
    }
}