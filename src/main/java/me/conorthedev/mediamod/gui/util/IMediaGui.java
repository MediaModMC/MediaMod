package me.conorthedev.mediamod.gui.util;

import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;

public interface IMediaGui {
    ResourceLocation headerResource = new ResourceLocation("mediamod", "header.png");

    default String getSuffix(boolean option, String label) {
        return option ? (label + ": " + EnumChatFormatting.GREEN + "YES") : (label + ": " + EnumChatFormatting.RED + "NO");
    }

    default int getRowPos(int rowNumber) {
        return 60 + rowNumber * 23;
    }
}
