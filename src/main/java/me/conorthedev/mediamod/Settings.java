package me.conorthedev.mediamod;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.io.File;

public class Settings {
    private static final File configFile = new File(FMLClientHandler.instance().getClient().mcDataDir, "config/mediamod.config");

    public static boolean ENABLED;
    public static boolean SHOW_PLAYER;
    public static boolean MODERN_PLAYER_STYLE;
    public static boolean SHOW_ALBUM_ART;
    public static boolean AUTO_COLOR_SELECTION;
    public static int PLAYER_X;
    public static int PLAYER_Y;
    public static double PLAYER_ZOOM;
    public static boolean EXTENSION_ENABLED;

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
        Property modernPlayerProperty = configuration.get("Player", "modernPlayer", true);
        Property albumArtProperty = configuration.get("Player", "showAlbumArt", true);
        Property autoColorProperty = configuration.get("Player", "automaticColorSelection", true);
        Property playerXProperty = configuration.get("Player", "playerX", 5);
        Property playerYProperty = configuration.get("Player", "playerY", 5);
        Property playerZoomProperty = configuration.get("Player", "playerZoom", 1.0);
        Property browserExtProperty = configuration.get("Player", "useBrowserExtension", true);

        if (load) ENABLED = enabledProperty.getBoolean();
        else enabledProperty.setValue(ENABLED);

        if (load) SHOW_PLAYER = showPlayerProperty.getBoolean();
        else showPlayerProperty.setValue(SHOW_PLAYER);

        if (load) MODERN_PLAYER_STYLE = modernPlayerProperty.getBoolean();
        else modernPlayerProperty.setValue(MODERN_PLAYER_STYLE);

        if (load) SHOW_ALBUM_ART = albumArtProperty.getBoolean();
        else albumArtProperty.setValue(SHOW_ALBUM_ART);

        if (load) AUTO_COLOR_SELECTION = autoColorProperty.getBoolean();
        else autoColorProperty.setValue(AUTO_COLOR_SELECTION);

        if (load) PLAYER_X = playerXProperty.getInt();
        else playerXProperty.setValue(PLAYER_X);

        if (load) PLAYER_Y = playerYProperty.getInt();
        else playerYProperty.setValue(PLAYER_Y);

        if (load) PLAYER_ZOOM = playerZoomProperty.getDouble();
        else playerZoomProperty.setValue(PLAYER_ZOOM);

        if (load) EXTENSION_ENABLED = browserExtProperty.getBoolean();
        else browserExtProperty.setValue(EXTENSION_ENABLED);
    }
}