package me.conorthedev.mediamod;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.io.File;

public class Settings {
    private static final File configFile = new File(FMLClientHandler.instance().getClient().mcDataDir, "config/mediamod.config");
    public static boolean ENABLED;
    public static boolean SHOW_PLAYER;
    public static boolean SPOTIFY;
    public static boolean SHOW_ALBUM_ART;
    public static boolean AUTO_COLOR_SELECTION;

    public static void saveConfig() {
        MediaMod.INSTANCE.LOGGER.info("Saving configuration...");

        Configuration configuration = new Configuration(configFile);
        updateConfig(configuration, false);
        configuration.save();

        MediaMod.INSTANCE.LOGGER.info("Saved configuration!");
    }

    public static void loadConfig() {
        Configuration configuration = new Configuration(configFile);
        configuration.load();
        updateConfig(configuration, true);
    }

    private static void updateConfig(Configuration configuration, boolean load) {
        Property enabledProperty = configuration.get("General", "enabled", true);
        Property showPlayerProperty = configuration.get("General", "showPlayer", true);
        Property spotifyProperty = configuration.get("General", "spotify", true);
        Property albumArtProperty = configuration.get("Player", "showAlbumArt", true);
        Property autoColorProperty = configuration.get("Player", "automaticColorSelection", true);

        if (load) ENABLED = enabledProperty.getBoolean();
        else enabledProperty.setValue(ENABLED);
        if (load) SHOW_PLAYER = showPlayerProperty.getBoolean();
        else showPlayerProperty.setValue(SHOW_PLAYER);
        if (load) SPOTIFY = spotifyProperty.getBoolean();
        else spotifyProperty.setValue(SPOTIFY);
        if (load) SHOW_ALBUM_ART = albumArtProperty.getBoolean();
        else albumArtProperty.setValue(SHOW_ALBUM_ART);
        if (load) AUTO_COLOR_SELECTION = autoColorProperty.getBoolean();
        else autoColorProperty.setValue(AUTO_COLOR_SELECTION);
    }
}