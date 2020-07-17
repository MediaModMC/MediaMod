package org.mediamod.mediamod.config;

import org.mediamod.mediamod.MediaMod;
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
    public static boolean SAVE_SPOTIFY_TOKEN;
    public static boolean ANNOUNCE_TRACKS;
    public static double PLAYER_X;
    public static double PLAYER_Y;
    public static double PLAYER_ZOOM;
    public static boolean EXTENSION_ENABLED;
    public static ProgressStyle PROGRESS_STYLE;
    public static String REFRESH_TOKEN;

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
        Property saveSpotifyTokenProperty = configuration.get("General", "saveSpotifyToken", true);
        Property announceTracksProperty = configuration.get("Player", "announceTracks", true);
        Property playerXProperty = configuration.get("Player", "playerX", 5.0);
        Property playerYProperty = configuration.get("Player", "playerY", 5.0);
        Property playerZoomProperty = configuration.get("Player", "playerZoom", 1.0);
        Property browserExtProperty = configuration.get("Player", "useBrowserExtension", true);
        Property progressStyleProperty = configuration.get("Player", "progressStyle", ProgressStyle.BAR_AND_NUMBERS_NEW.name());
        Property refreshTokenProperty = configuration.get("Spotify", "refreshToken", "");

        if (load) SAVE_SPOTIFY_TOKEN = saveSpotifyTokenProperty.getBoolean();
        else saveSpotifyTokenProperty.setValue(SAVE_SPOTIFY_TOKEN);

        if (load) REFRESH_TOKEN = refreshTokenProperty.getString();
        else {
            if(SAVE_SPOTIFY_TOKEN) {
                refreshTokenProperty.setValue(REFRESH_TOKEN);
            } else {
                refreshTokenProperty.setValue("");
            }
        }

        if (load) ENABLED = enabledProperty.getBoolean();
        else enabledProperty.setValue(ENABLED);

        if (load) ANNOUNCE_TRACKS = announceTracksProperty.getBoolean();
        else announceTracksProperty.setValue(ANNOUNCE_TRACKS);

        if (load) PROGRESS_STYLE = ProgressStyle.valueOf(progressStyleProperty.getString());
        else progressStyleProperty.setValue(PROGRESS_STYLE.name());

        if (load) SHOW_PLAYER = showPlayerProperty.getBoolean();
        else showPlayerProperty.setValue(SHOW_PLAYER);

        if (load) MODERN_PLAYER_STYLE = modernPlayerProperty.getBoolean();
        else modernPlayerProperty.setValue(MODERN_PLAYER_STYLE);

        if (load) SHOW_ALBUM_ART = albumArtProperty.getBoolean();
        else albumArtProperty.setValue(SHOW_ALBUM_ART);

        if (load) AUTO_COLOR_SELECTION = autoColorProperty.getBoolean();
        else autoColorProperty.setValue(AUTO_COLOR_SELECTION);

        if (load) PLAYER_X = playerXProperty.getDouble();
        else playerXProperty.setValue(PLAYER_X);

        if (load) PLAYER_Y = playerYProperty.getDouble();
        else playerYProperty.setValue(PLAYER_Y);

        if (load) PLAYER_ZOOM = playerZoomProperty.getDouble();
        else playerZoomProperty.setValue(PLAYER_ZOOM);

        if (load) EXTENSION_ENABLED = browserExtProperty.getBoolean();
        else browserExtProperty.setValue(EXTENSION_ENABLED);
    }
}