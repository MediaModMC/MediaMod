package me.conorthedev.mediamod.config;

import cc.hyperium.config.ConfigOpt;

public class Settings {
    @ConfigOpt public static boolean ENABLED = true;
    @ConfigOpt public static boolean SHOW_PLAYER = true;
    @ConfigOpt public static boolean MODERN_PLAYER_STYLE = true;
    @ConfigOpt public static boolean SHOW_ALBUM_ART = true;
    @ConfigOpt public static boolean AUTO_COLOR_SELECTION = true;
    @ConfigOpt public static double PLAYER_X = 5.0;
    @ConfigOpt public static double PLAYER_Y = 5.0;
    @ConfigOpt public static double PLAYER_ZOOM = 1;
    @ConfigOpt public static boolean EXTENSION_ENABLED;
    @ConfigOpt public static ProgressStyle PROGRESS_STYLE = ProgressStyle.BAR_AND_NUMBERS_NEW;
}
