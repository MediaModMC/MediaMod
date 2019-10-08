package me.conorthedev.mediamod.util;

import com.google.gson.Gson;
import me.conorthedev.mediamod.MediaMod;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

public class VersionChecker {
    /**
     * An instance of this class
     */
    public static final VersionChecker INSTANCE = new VersionChecker();

    /**
     * If the mod is up to date
     */
    public boolean IS_LATEST_VERSION = false;

    /**
     * If the mod isn't up to date, this will contain the latest version's information
     */
    public VersionResponse LATEST_VERSION_INFO = null;

    public static void checkVersion() {
        try {
            // Create a connection
            URL url = new URL("https://raw.githubusercontent.com/MediaModMC/MediaMod/master/version.json");

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // Set the request method
            con.setRequestMethod("GET");
            // Connect to the API
            con.connect();

            // Read the output
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String content = in.lines().collect(Collectors.joining());

            // Close the input reader & the connection
            in.close();
            con.disconnect();

            // Parse JSON
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
        public String latestVersionS;
        public int latestVersionInt;
        public String changelog;

        VersionResponse(int latestVersionInt, String latestVersionS, String changelog) {
            this.latestVersionInt = latestVersionInt;
            this.latestVersionS = latestVersionS;
            this.changelog = changelog;
        }
    }
}
