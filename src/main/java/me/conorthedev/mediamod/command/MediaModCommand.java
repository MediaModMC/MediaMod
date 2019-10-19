package me.conorthedev.mediamod.command;

import mcp.MethodsReturnNonnullByDefault;
import me.conorthedev.mediamod.gui.GuiMediaModSettings;
import me.conorthedev.mediamod.util.TickScheduler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.client.FMLClientHandler;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

/**
 * The client-side command to open the MediaMod GUI
 *
 * @see net.minecraft.command.ICommand
 */
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MediaModCommand extends CommandBase {

    @Override
    public String getName() {
        return "mediamod";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "/mediamod";
    }

    @Override
    public List<String> getAliases() {
        return Arrays.asList("media", "mm");
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        TickScheduler.INSTANCE.schedule(1, () -> FMLClientHandler.instance().getClient().displayGuiScreen(new GuiMediaModSettings()));
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public int getRequiredPermissionLevel() {
        return -1;
    }
}
