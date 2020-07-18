package org.mediamod.mediamod.command;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import org.apache.commons.io.FileUtils;
import org.mediamod.mediamod.util.Multithreading;
import org.mediamod.mediamod.util.PlayerMessager;
import org.mediamod.mediamod.util.VersionChecker;

import java.io.File;
import java.io.IOException;
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
        if (VersionChecker.INSTANCE.IS_LATEST_VERSION) {
            PlayerMessager.sendMessage("MediaMod is up-to-date!", true);
        } else {
            PlayerMessager.sendMessage("Downloading MediaMod v" + VersionChecker.INSTANCE.LATEST_VERSION_INFO.latestVersionS, true);
            try {
                URL url = new URL(VersionChecker.INSTANCE.LATEST_VERSION_INFO.downloadURL);
                URL updater = new URL("https://github.com/MediaModMC/Updater/releases/download/1.0.0-BETA-1/MediaModUpdater-1.0.0-BETA-1.jar");

                if(new File(Minecraft.getMinecraft().mcDataDir, "mediamod/update.lock").createNewFile()) {
                    Multithreading.runAsync(() -> {
                        try {
                            FileUtils.copyURLToFile(url, new File(Minecraft.getMinecraft().mcDataDir, "mediamod/update.jar"));
                            File updaterFile = new File(Minecraft.getMinecraft().mcDataDir, "mediamod/updater.jar");
                            if(!updaterFile.exists()) {
                                FileUtils.copyURLToFile(updater, updaterFile);
                            }

                            PlayerMessager.sendMessage("Update downloaded! Relaunch Minecraft to complete installation", true);
                        } catch (Exception e) {
                            PlayerMessager.sendMessage("Failed to download MediaMod v" + VersionChecker.INSTANCE.LATEST_VERSION_INFO.latestVersionS, true);
                            e.printStackTrace();
                        }
                    });
                } else {
                    PlayerMessager.sendMessage("Failed to download MediaMod v" + VersionChecker.INSTANCE.LATEST_VERSION_INFO.latestVersionS, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                PlayerMessager.sendMessage("Failed to download MediaMod v" + VersionChecker.INSTANCE.LATEST_VERSION_INFO.latestVersionS, true);
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
