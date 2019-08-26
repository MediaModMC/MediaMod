package me.conorthedev.mediamod.gui.util;

import net.minecraft.util.EnumChatFormatting;

public interface IMediaGui {

    default String getSuffix(boolean option, String label) {
        return option ? (label + ": " + EnumChatFormatting.GREEN + "YES") : (label + ": " + EnumChatFormatting.RED + "NO");
    }

    default int getRowPos(int rowNumber) {
        return 55 + rowNumber * 23;
    }
}
