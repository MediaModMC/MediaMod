package org.mediamod.mediamod.command;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import org.apache.commons.io.FileUtils;
import org.mediamod.mediamod.util.Multithreading;
import org.mediamod.mediamod.util.PlayerMessenger;
import org.mediamod.mediamod.util.VersionChecker;

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
    public String getName() {
        return "mediamodupdate";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/mediamodupdate";
    }

    @Override
    public List<String> getAliases() {
        return Collections.singletonList("mmupdate");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (VersionChecker.INSTANCE.IS_LATEST_VERSION) {
            PlayerMessenger.sendMessage("MediaMod is up-to-date!", true);
        } else {
            PlayerMessenger.sendMessage("Downloading MediaMod v" + VersionChecker.INSTANCE.LATEST_VERSION_INFO.latestVersionS, true);
            try {
                URL url = new URL(VersionChecker.INSTANCE.LATEST_VERSION_INFO.downloadURL);
                URL updater = new URL("https://github.com/MediaModMC/Updater/releases/download/1.0.0-BETA-1/MediaModUpdater-1.0.0-BETA-1.jar");

                if(new File(Minecraft.getMinecraft().gameDir, "mediamod/update.lock").createNewFile()) {
                    Multithreading.runAsync(() -> {
                        try {
                            FileUtils.copyURLToFile(url, new File(Minecraft.getMinecraft().gameDir, "mediamod/update.jar"));
                            File updaterFile = new File(Minecraft.getMinecraft().gameDir, "mediamod/updater.jar");
                            if(!updaterFile.exists()) {
                                FileUtils.copyURLToFile(updater, updaterFile);
                            }

                            PlayerMessenger.sendMessage("Update downloaded! Relaunch Minecraft to complete installation", true);
                        } catch (Exception e) {
                            PlayerMessenger.sendMessage("Failed to download MediaMod v" + VersionChecker.INSTANCE.LATEST_VERSION_INFO.latestVersionS, true);
                            e.printStackTrace();
                        }
                    });
                } else {
                    PlayerMessenger.sendMessage("Failed to download MediaMod v" + VersionChecker.INSTANCE.LATEST_VERSION_INFO.latestVersionS, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
                PlayerMessenger.sendMessage("Failed to download MediaMod v" + VersionChecker.INSTANCE.LATEST_VERSION_INFO.latestVersionS, true);
            }
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
