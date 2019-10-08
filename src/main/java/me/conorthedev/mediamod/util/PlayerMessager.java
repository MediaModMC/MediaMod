package me.conorthedev.mediamod.util;

import cc.hyperium.Hyperium;
import cc.hyperium.utils.ChatColor;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class PlayerMessager {

    public static void sendMessage(String message) {
        Hyperium.INSTANCE.getHandlers().getGeneralChatHandler().sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&c[&fMediaMod&c] " + message), false);
    }

    public static void sendMessage(IChatComponent message) {
        Hyperium.INSTANCE.getHandlers().getGeneralChatHandler().sendMessage(new ChatComponentText(ChatColor.translateAlternateColorCodes('&',
                "&c[&fMediaMod&c] " + message)));
    }
}
