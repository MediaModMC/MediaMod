package org.mediamod.mediamod.theming;

import com.moandjiezana.toml.Toml;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mediamod.mediamod.MediaMod;
import org.mediamod.mediamod.config.Settings;
import org.mediamod.mediamod.theming.types.PlayerThemingColors;
import org.mediamod.mediamod.theming.types.PlayerThemeMetadata;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Class that handles themes
 *
 * @author ChachyDev
 */
public class PlayerThemeHandler {
    private final Logger logger = LogManager.getLogger(this.getClass());
    public static PlayerThemeHandler INSTANCE = new PlayerThemeHandler();

    /**
     * Iterates through the themes directory and gets all of the .toml files, then attempts to parse them.
     *
     * @return Metadata and Colours from File
     */
    public ArrayList<Pair<PlayerThemeMetadata, PlayerThemingColors>> getThemes() {
        File[] files = MediaMod.INSTANCE.mediamodThemeDirectory.listFiles();
        ArrayList<Pair<PlayerThemeMetadata, PlayerThemingColors>> themes = new ArrayList<>();
        if (files != null) {
            for (File theme : files) {
                if (theme.getName().endsWith(".toml")) {
                    try {
                        Toml toml = new Toml().read(theme);
                        themes.add(
                                Pair.of(
                                        toml.getTable("metadata").to(PlayerThemeMetadata.class),
                                        toml.getTable("colours").to(PlayerThemingColors.class)
                                )
                        );
                    } catch (Exception e) {
                        logger.error("Failed to read toml file!");
                    }
                }
            }
        }
        return themes;
    }

    /**
     * Grabs the currently selected theme's background opts from the colour table
     *
     * @return Metadata and Colours from File
     */
    public Color getPlayerColour() {
        PlayerThemingColors colourBlock = new Toml().read(Settings.THEME_FILE).getTable("colours").to(PlayerThemingColors.class);
        if (colourBlock == null) {
            return Color.darkGray.brighter();
        } else {
            return new Color(colourBlock.getPlayerRed(), colourBlock.getPlayerGreen(), colourBlock.getPlayerBlue());
        }
    }

    /**
     * Grabs the currently selected theme's text opts from the colour table
     *
     * @return Metadata and Colours from File
     */
    public Color getPlayerTextColour() {
        PlayerThemingColors colourBlock = new Toml().read(Settings.THEME_FILE).getTable("colours").to(PlayerThemingColors.class);
        if (colourBlock == null) {
            return Color.white;
        } else {
            return new Color(colourBlock.getTextRed(), colourBlock.getTextGreen(), colourBlock.getTextBlue());
        }
    }
}
