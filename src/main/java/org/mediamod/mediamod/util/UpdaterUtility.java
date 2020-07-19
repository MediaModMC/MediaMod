package org.mediamod.mediamod.util;

import org.apache.commons.io.FileUtils;
import org.mediamod.mediamod.MediaMod;

import java.io.File;
import java.net.URL;

/**
 * The class that handles auto-updates for MediaMod
 */
public class UpdaterUtility {
    /**
     * Downloads the required files for a MediaMod Update
     */
    public void scheduleUpdate() {
        if(VersionChecker.INSTANCE.isLatestVersion) return;

        PlayerMessenger.sendMessage(ChatColor.GRAY + "Downloading MediaMod v" + VersionChecker.INSTANCE.latestVersionInformation.name, true);
        try {
            URL url = new URL(VersionChecker.INSTANCE.latestVersionInformation.downloadURL);
            URL updater = new URL("https://github.com/MediaModMC/Updater/releases/download/" + VersionChecker.INSTANCE.allVersionInfo.latestUpdater + "/MediaModUpdater-" + VersionChecker.INSTANCE.allVersionInfo.latestUpdater + ".jar");

            File lockFile = new File(MediaMod.INSTANCE.mediamodDirectory, "update.lock");
            File updateJar = new File(MediaMod.INSTANCE.mediamodDirectory, "update.jar");
            File updaterJar = new File(MediaMod.INSTANCE.mediamodDirectory, "updater.jar");

            if (lockFile.exists() && updateJar.exists()) {
                PlayerMessenger.sendMessage(ChatColor.GRAY + "It seems there was a previous update attempt that may have failed. Deleting previous files and attempting again!", true);

                if (!updateJar.delete()) {
                    PlayerMessenger.sendMessage(ChatColor.RED + "Failed to delete previous update jar", true);
                    return;
                }

                if (!lockFile.delete()) {
                    PlayerMessenger.sendMessage(ChatColor.RED + "Failed to delete previous lockfile", true);
                    return;
                }
            }

            if (lockFile.createNewFile()) {
                Multithreading.runAsync(() -> {
                    try {
                        FileUtils.copyURLToFile(url, updateJar);
                        if (!updaterJar.exists()) {
                            FileUtils.copyURLToFile(updater, updaterJar);
                        }

                        PlayerMessenger.sendMessage(ChatColor.GREEN + "Update downloaded! It will automatically install when you close Minecraft", true);
                    } catch (Exception e) {
                        PlayerMessenger.sendMessage(ChatColor.RED + "Failed to download MediaMod v" + VersionChecker.INSTANCE.latestVersionInformation.name, true);
                        MediaMod.INSTANCE.logger.error("Failed to download required files", e);
                    }
                });
            } else {
                PlayerMessenger.sendMessage(ChatColor.RED + "Failed to download MediaMod v" + VersionChecker.INSTANCE.latestVersionInformation.name, true);
                MediaMod.INSTANCE.logger.error("Failed to create lockfile!");
            }
        } catch (Exception e) {
            PlayerMessenger.sendMessage(ChatColor.RED + "Failed to download MediaMod v" + VersionChecker.INSTANCE.latestVersionInformation.name, true);
            MediaMod.INSTANCE.logger.error("Failed to create lockfile", e);
        }
    }
}
