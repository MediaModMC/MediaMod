package dev.conorthedev.mediamod.util;

import com.google.gson.Gson;
import dev.conorthedev.mediamod.MediaMod;
import org.lwjgl.Sys;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.stream.Collectors;

public class WebRequest {
    public static <T> T requestToMediaMod(WebRequestType type, String path, Class<T> toClass) throws IOException {
        URL url = new URL(MediaMod.ENDPOINT + "api/" + path);

        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(type.name());
            connection.setRequestProperty("User-Agent", "MediaMod/1.0");
            connection.connect();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String content = reader.lines().collect(Collectors.joining());

            return new Gson().fromJson(content, toClass);
        } catch (Exception e) {
            MediaMod.INSTANCE.LOGGER.error("Failed to perform web request! Error: " + e.getMessage());
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
                }

                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                MediaMod.INSTANCE.LOGGER.error("Failed to perform web request! Error: " + e.getMessage());
            }
        }

        return null;
    }

    public static <T> T makeRequest(WebRequestType type, URL url, Class<T> toClass, HashMap<String, String> properties) throws IOException {
        HttpURLConnection connection;
        BufferedReader reader;

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(type.name());
        connection.setRequestProperty("User-Agent", "MediaMod/1.0");

        for (String key : properties.keySet()) {
            String value = properties.get(key);
            connection.setRequestProperty(key, value);
        }

        if(type == WebRequestType.POST || type == WebRequestType.PUT) {
            connection.setDoOutput(true);
            connection.getOutputStream().write("".getBytes(StandardCharsets.UTF_8));
        }

        connection.connect();

        reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String content = reader.lines().collect(Collectors.joining());

        connection.disconnect();
        reader.close();

        if(toClass == null) {
            return null;
        }

        return new Gson().fromJson(content, toClass);
    }
}
