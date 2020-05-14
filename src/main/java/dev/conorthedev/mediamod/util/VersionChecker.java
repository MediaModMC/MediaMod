package dev.conorthedev.mediamod.util;

import dev.conorthedev.mediamod.MediaMod;

import java.net.URL;
import java.util.HashMap;

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
            VersionResponse versionResponse = WebRequest.makeRequest(WebRequestType.GET, new URL("https://raw.githubusercontent.com/MediaModMC/MediaMod/master/version.json"), VersionResponse.class, new HashMap<>());

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
