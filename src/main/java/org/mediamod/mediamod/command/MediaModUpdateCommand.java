package org.mediamod.mediamod.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import org.apache.commons.io.FileUtils;
import org.mediamod.mediamod.MediaMod;
import org.mediamod.mediamod.util.*;

import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * The client-side command to update MediaMod
 *
 * @see net.minecraft.command.ICommand
 */
public class MediaModUpdateCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "mediamodupdate";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/mediamodupdate";
    }

    @Override
    public List<String> getCommandAliases() {
        return Collections.singletonList("mmupdate");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (VersionChecker.INSTANCE.isLatestVersion) {
            PlayerMessenger.sendMessage(ChatColor.GRAY + "MediaMod is up-to-date!", true);
        } else {
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

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
