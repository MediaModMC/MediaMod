package me.conorthedev.mediamod.command;

import me.conorthedev.mediamod.gui.GuiMediaModSettings;
import me.conorthedev.mediamod.util.TickScheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

import java.util.Arrays;
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
        return Arrays.asList("mm", "media");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        TickScheduler.INSTANCE.schedule(1, () -> Minecraft.getMinecraft().displayGuiScreen(new GuiMediaModSettings()));
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
