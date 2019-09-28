package me.conorthedev.mediamod.command;

import cc.hyperium.Hyperium;
import cc.hyperium.commands.BaseCommand;
import me.conorthedev.mediamod.gui.GuiMediaModSettings;

import java.util.Arrays;
import java.util.List;

public class MediaModCommand implements BaseCommand {
    @Override
    public String getName() {
        return "mediamod";
    }

    @Override
    public String getUsage() {
        return "/mediamod";
    }

    @Override
    public void onExecute(String[] args) {
        Hyperium.INSTANCE.getHandlers().getGuiDisplayHandler().setDisplayNextTick(new GuiMediaModSettings());
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("media", "mm");
    }
}
