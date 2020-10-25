package me.dreamhopping.mediamod.util;

import com.github.zafarkhaja.semver.Version;
import me.dreamhopping.mediamod.MediaMod;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class VersionChecker {
    /**
     * An instance of this class
     */
    public static final VersionChecker INSTANCE = new VersionChecker();

    /**
     * If the mod is up to date
     */
    public boolean isLatestVersion = false;

    /**
     * If the mod isn't up to date, this will contain the latest version's information
     */
    public VersionInformation latestVersionInformation = null;

    /**
     * The whole version.json file parsed into a java class
     */
    public AllVersionInfo allVersionInfo = null;

    public static void checkVersion() {
        try {
            INSTANCE.allVersionInfo = WebRequest.makeRequest(WebRequestType.GET, new URL("https://raw.githubusercontent.com/MediaModMC/MediaMod/master/version-beta.json"), AllVersionInfo.class, new HashMap<>());
            if (INSTANCE.allVersionInfo == null) return;

            VersionInformation information = INSTANCE.allVersionInfo.versions.get(Metadata.MINECRAFT_VERSION);
            if (information == null) return;

            Version version = Version.valueOf(information.name);
            version.setBuildMetadata("beta");

            information.downloadURL = "https://github.com/MediaModMC/MediaMod/releases/download/" + information.name + "/MediaMod-" + version.getNormalVersion() + "-" + Metadata.MINECRAFT_VERSION + information.name.replace(version.getNormalVersion(), "") + ".jar";

            if (information.version > Metadata.VERSION_INT) {
                INSTANCE.isLatestVersion = false;
                INSTANCE.latestVersionInformation = information;
            } else {
                INSTANCE.isLatestVersion = true;
            }

            if (VersionChecker.INSTANCE.isLatestVersion) {
                MediaMod.INSTANCE.logger.info("MediaMod is up-to-date!");
            } else {
                MediaMod.INSTANCE.logger.warn("MediaMod is NOT up-to-date! Latest Version: v" + VersionChecker.INSTANCE.latestVersionInformation.name + " Your Version: v" + Metadata.VERSION);
            }
        } catch (Exception e) {
            MediaMod.INSTANCE.logger.error("Error whilst checking for updates: " + e);
        }
    }

    public static class AllVersionInfo {
        public final Map<String, VersionInformation> versions;
        public final String latestUpdater;

        AllVersionInfo(Map<String, VersionInformation> versions, String latestUpdater) {
            this.versions = versions;
            this.latestUpdater = latestUpdater;
        }
    }

    public static class VersionInformation {
        public final int version;
        public final String name;
        public final String changelog;
        public String downloadURL;

        public VersionInformation(int version, String name, String changelog) {
            this.version = version;
            this.name = name;
            this.changelog = changelog;
        }
    }
}
