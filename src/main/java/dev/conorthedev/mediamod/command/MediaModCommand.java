package dev.conorthedev.mediamod.command;

import dev.conorthedev.mediamod.MediaMod;
import dev.conorthedev.mediamod.gui.GuiMediaModSettings;
import dev.conorthedev.mediamod.util.ChatColor;
import dev.conorthedev.mediamod.util.PlayerMessager;
import dev.conorthedev.mediamod.util.TickScheduler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.client.FMLClientHandler;
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
        return Arrays.asList("media", "mm");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if(args.length <= 0) {
            TickScheduler.INSTANCE.schedule(1, () -> FMLClientHandler.instance().getClient().displayGuiScreen(new GuiMediaModSettings()));
        } else {
            String subcommand = args[0];
            if(subcommand.equalsIgnoreCase("party")) {
                if(args.length >= 2) {
                    String function = args[1];
                    if(function.equalsIgnoreCase("start")) {
                        if(!MediaMod.INSTANCE.partyManager.canStartParty()) {
                            PlayerMessager.sendMessage(ChatColor.RED + "You are already in a party!", true);
                            return;
                        }

                        PlayerMessager.sendMessage(ChatColor.GRAY + "Creating MediaMod Party... " + "(note: this only works with spotify at the moment)", true);

                        String code = MediaMod.INSTANCE.partyManager.startParty();
                        if(code.equals("")) {
                            PlayerMessager.sendMessage(ChatColor.RED + "An error occurred whilst creating your MediaMod party!", true);
                        } else {
                            IChatComponent urlComponent = new ChatComponentText(ChatColor.WHITE + "" + ChatColor.BOLD + code);
                            urlComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, code));
                            urlComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText("Copy Code")));
                            PlayerMessager.sendMessage(new ChatComponentText(ChatColor.GRAY + "Share this code with your friends to invite them to your party: ").appendSibling(urlComponent), true);
                        }
                    } else if (function.equalsIgnoreCase("leave")) {
                        if(!MediaMod.INSTANCE.partyManager.canStartParty()) {
                            boolean success = MediaMod.INSTANCE.partyManager.leaveParty();
                            if(success) {
                                PlayerMessager.sendMessage((ChatColor.GRAY + "You have left the party"), true);
                            } else {
                                PlayerMessager.sendMessage(ChatColor.RED + "An error occurred whilst trying to leave the party!", true);
                            }
                        } else {
                            PlayerMessager.sendMessage(ChatColor.RED + "You are not in a party!");
                        }
                    }
                } else {
                    PlayerMessager.sendMessage(ChatColor.RED + "Incorrect syntax! Usage: /mm party <start/invite/info/join/leave>");
                }
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
