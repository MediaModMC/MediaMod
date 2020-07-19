package org.mediamod.mediamod.util;

import org.mediamod.mediamod.MediaMod;

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

            if (versionResponse == null) return;

            if (versionResponse.latestVersionInt > Metadata.VERSION_INT) {
                INSTANCE.IS_LATEST_VERSION = false;
                INSTANCE.LATEST_VERSION_INFO = versionResponse;
            } else {
                INSTANCE.IS_LATEST_VERSION = true;
            }

            if (VersionChecker.INSTANCE.IS_LATEST_VERSION) {
                MediaMod.INSTANCE.logger.info("MediaMod is up-to-date!");
            } else {
                MediaMod.INSTANCE.logger.warn("MediaMod is NOT up-to-date! Latest Version: v" + VersionChecker.INSTANCE.LATEST_VERSION_INFO.latestVersionS + " Your Version: v" + Metadata.VERSION);
            }
        } catch (Exception e) {
            MediaMod.INSTANCE.logger.error("Error whilst checking for updates: " + e);
        }
    }

    public static class VersionResponse {
        public final String latestVersionS;
        public final int latestVersionInt;
        public final String changelog;
        public final String downloadURL;
        public final String latestUpdater;

        VersionResponse(int latestVersionInt, String latestVersionS, String changelog, String downloadURL, String latestUpdater) {
            this.latestVersionInt = latestVersionInt;
            this.latestVersionS = latestVersionS;
            this.changelog = changelog;
            this.downloadURL = downloadURL;
            this.latestUpdater = latestUpdater;
        }
    }
}
