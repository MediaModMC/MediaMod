package me.conorthedev.mediamod.util;

import com.google.gson.Gson;
import me.conorthedev.mediamod.MediaMod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class VersionChecker {
    public static final VersionChecker INSTANCE = new VersionChecker();
    public boolean IS_LATEST_VERSION;
    public VersionResponse LATEST_VERSION_INFO;

    public static void checkVersion() {
        try {
            URL url = new URL("https://raw.githubusercontent.com/MediaModMC/MediaMod/master/version.json");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.connect();
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String content = reader.lines().collect(Collectors.joining());
            reader.close();
            con.disconnect();
            Gson g = new Gson();
            VersionResponse versionResponse = g.fromJson(content, VersionResponse.class);

            if (versionResponse.latestVersionInt > Metadata.VERSION_INT) {
                INSTANCE.IS_LATEST_VERSION = false;
                INSTANCE.LATEST_VERSION_INFO = versionResponse;
            } else {
                INSTANCE.IS_LATEST_VERSION = true;
            }
        } catch (Exception e) {
            MediaMod.INSTANCE.LOGGER.error("Error whilst checking for updates: " + e);
        }
    }

    public static class VersionResponse {
        public String latestVersionString;
        int latestVersionInt;
        public String changelog;

        public VersionResponse(String latestVersionString, int latestVersionInt, String changelog) {
            this.latestVersionString = latestVersionString;
            this.latestVersionInt = latestVersionInt;
            this.changelog = changelog;
        }
    }
}
