package me.dreamhopping.mediamod.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.dreamhopping.mediamod.MediaMod;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.stream.Collectors;

public class WebRequest {
    public static <T> T requestToMediaMod(WebRequestType type, String path, JsonObject body, Class<T> toClass) throws IOException {
        URL url = new URL(MediaMod.ENDPOINT + path);

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(type.name());
            connection.setRequestProperty("User-Agent", "MediaMod/1.0");

            if (type == WebRequestType.POST) {
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.getOutputStream().write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            connection.connect();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String content = reader.lines().collect(Collectors.joining());

                if (connection.getResponseCode() == 200) {
                    return new Gson().fromJson(content, toClass);
                }
            }
        } catch (Exception e) {
            MediaMod.INSTANCE.logger.error("Failed to perform web request! Error: {}", e.getLocalizedMessage(), e);
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                MediaMod.INSTANCE.logger.error("Failed to perform web request! Error: {}", e.getMessage(), e);
            }
        }

        return null;
    }

    public static int requestToMediaMod(WebRequestType type, String path, JsonObject body) throws IOException {
        URL url = new URL(MediaMod.ENDPOINT + path);

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(type.name());
            connection.setRequestProperty("User-Agent", "MediaMod/1.0");
            if (type == WebRequestType.POST) {
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.getOutputStream().write(body.toString().getBytes(StandardCharsets.UTF_8));
            }
            connection.connect();

            return connection.getResponseCode();
        } catch (Exception e) {
            MediaMod.INSTANCE.logger.error("Failed to perform web request! Error: {}", e.getLocalizedMessage(), e);
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                MediaMod.INSTANCE.logger.error("Failed to perform web request! Error: {}", e.getMessage(), e);
            }
        }

        return 0;
    }

    public static <T> T requestToMediaMod(WebRequestType type, String path, Class<T> toClass) throws IOException {
        URL url = new URL(MediaMod.ENDPOINT + path);

        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(type.name());
            connection.setRequestProperty("User-Agent", "MediaMod/1.0");

            connection.connect();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String content = reader.lines().collect(Collectors.joining());

                if (connection.getResponseCode() == 200) {
                    return new Gson().fromJson(content, toClass);
                }
            }
        } catch (Exception e) {
            MediaMod.INSTANCE.logger.error("Failed to perform web request! Error: {}", e.getLocalizedMessage(), e);
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                MediaMod.INSTANCE.logger.error("Failed to perform web request! Error: {}", e.getMessage(), e);
            }
        }

        return null;
    }

    public static <T> T makeRequest(WebRequestType type, URL url, Class<T> toClass, HashMap<String, String> properties) {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(type.name());
            connection.setRequestProperty("User-Agent", "MediaMod/1.0");

            for (String key : properties.keySet()) {
                String value = properties.get(key);
                connection.setRequestProperty(key, value);
            }

            if (type == WebRequestType.POST || type == WebRequestType.PUT) {
                connection.setDoOutput(true);
                connection.getOutputStream().write("".getBytes(StandardCharsets.UTF_8));
            }

            connection.connect();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String content = reader.lines().collect(Collectors.joining());

                if (toClass == null) {
                    return null;
                }

                return new Gson().fromJson(content, toClass);
            }
        } catch (Exception e) {
            MediaMod.INSTANCE.logger.error("Failed to perform web request! Error: {}", e.getLocalizedMessage(), e);
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                MediaMod.INSTANCE.logger.error("Failed to perform web request! Error: {}", e.getMessage(), e);
            }
        }

        return null;
    }

    public static int makeRequest(WebRequestType type, URL url, JsonObject body, HashMap<String, String> properties) {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(type.name());
            connection.setRequestProperty("User-Agent", "MediaMod/1.0");

            for (String key : properties.keySet()) {
                String value = properties.get(key);
                connection.setRequestProperty(key, value);
            }

            if (type == WebRequestType.POST || type == WebRequestType.PUT) {
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);
                connection.getOutputStream().write(body.toString().getBytes(StandardCharsets.UTF_8));
            }

            connection.connect();

            return connection.getResponseCode();
        } catch (Exception e) {
            MediaMod.INSTANCE.logger.error("Failed to perform web request! Error: {}", e.getLocalizedMessage(), e);
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                MediaMod.INSTANCE.logger.error("Failed to perform web request! Error: {}", e.getMessage(), e);
            }
        }

        return 0;
    }

    public static int makeRequest(WebRequestType type, URL url, HashMap<String, String> properties) {
        HttpURLConnection connection = null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(type.name());
            connection.setRequestProperty("User-Agent", "MediaMod/1.0");

            for (String key : properties.keySet()) {
                String value = properties.get(key);
                connection.setRequestProperty(key, value);
            }

            if (type == WebRequestType.POST || type == WebRequestType.PUT) {
                connection.setDoOutput(true);
                connection.getOutputStream().write("".getBytes(StandardCharsets.UTF_8));
            }

            connection.connect();

            return connection.getResponseCode();
        } catch (Exception e) {
            MediaMod.INSTANCE.logger.error("Failed to perform web request! Error: {}", e.getLocalizedMessage(), e);
        } finally {
            try {
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                MediaMod.INSTANCE.logger.error("Failed to perform web request! Error: {}", e.getMessage(), e);
            }
        }

        return 0;
    }
}
