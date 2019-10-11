package me.conorthedev.mediamod.util;

import cc.hyperium.Hyperium;
import cc.hyperium.utils.ChatColor;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class PlayerMessager {

    public static void sendMessage(String message) {
        Hyperium.INSTANCE.getHandlers().getGeneralChatHandler().sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&c[&fMediaMod&c] " + message), false);
    }
}
