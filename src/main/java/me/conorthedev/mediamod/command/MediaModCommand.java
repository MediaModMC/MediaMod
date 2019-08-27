package me.conorthedev.mediamod.command;

import me.conorthedev.mediamod.gui.GuiMediaModSettings;
import me.conorthedev.mediamod.media.spotify.SpotifyHandler;
import me.conorthedev.mediamod.media.spotify.api.SpotifyAPI;
import me.conorthedev.mediamod.util.TickScheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * The client-side command to open the MediaMod GUI
 *
 * @see net.minecraft.command.ICommand
 */
public class MediaModCommand extends CommandBase {
    @Override
    public String getCommandName() {
        return "mediamod";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/mediamod";
    }

    @Override
    public List<String> getCommandAliases() {
        List<String> aliases = new ArrayList<>();
        aliases.add("media");
        aliases.add("mm");

        return aliases;
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        TickScheduler.INSTANCE.schedule(1, () -> Minecraft.getMinecraft().displayGuiScreen(new GuiMediaModSettings()));
        System.out.println(SpotifyHandler.spotifyApi.getAccessToken());
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }
}
