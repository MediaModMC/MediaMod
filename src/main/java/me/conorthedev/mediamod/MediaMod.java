package me.conorthedev.mediamod;

import me.conorthedev.mediamod.base.BaseMod;
import me.conorthedev.mediamod.command.MediaModCommand;
import me.conorthedev.mediamod.gui.util.DynamicTextureWrapper;
import me.conorthedev.mediamod.media.base.ServiceHandler;
import me.conorthedev.mediamod.media.browser.BrowserHandler;
import me.conorthedev.mediamod.media.spotify.SpotifyHandler;
import me.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;
import me.conorthedev.mediamod.media.spotify.api.track.Track;
import me.conorthedev.mediamod.util.Metadata;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.awt.Color.white;

/**
 * The main class for MediaMod
 *
 * @author ConorTheDev
 * @see net.minecraftforge.fml.common.Mod
 */
@Mod(name = Metadata.NAME, modid = Metadata.MODID, version = Metadata.VERSION)
public class MediaMod {
    /**
     * Average Color Cache
     */
    private static final HashMap<BufferedImage, Color> avgColorCache = new HashMap<>();
    /**
     * An instance of this class to access non-static methods from other classes
     */
    @Mod.Instance(Metadata.MODID)
    public static MediaMod INSTANCE;

    /**
     * Logger used to log info messages, debug messages, error messages & more
     *
     * @see org.apache.logging.log4j.Logger
     */
    public final Logger LOGGER = LogManager.getLogger("MediaMod");

    /**
     * Check if the user is in a development environment, this is used for DEBUG messages
     */
    public boolean DEVELOPMENT_ENVIRONMENT = classExists("net.minecraft.client.Minecraft");

    /**
     * The current song
     *
     * @see me.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject
     */
    private CurrentlyPlayingObject currentlyPlayingObject = null;

    // If the tick is the first one
    private boolean first = true;

    /**
     * Fired when a game overlay is being rendered
     *
     * @param event - RenderGameOverlayEvent
     * @see RenderGameOverlayEvent
     */

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        if (first) {
            // This is the first tick of rendering game overlays

            // Make sure that this is never run again
            first = false;

            // Setup a ScheduledExecutorService to run every 3 seconds
            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.scheduleAtFixedRate(() -> {
                try {
                    // Check if we are logged in
                    if (SpotifyHandler.logged) {
                        // Set the currentlyPlayingContext to the current song
                        currentlyPlayingObject = SpotifyHandler.spotifyApi.getCurrentTrack();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 0, 3, TimeUnit.SECONDS);
        }

        // Check if we're logged in & the hotbar is being rendered
        if (SpotifyHandler.logged && currentlyPlayingObject != null && event.type == RenderGameOverlayEvent.ElementType.HOTBAR) {
            // Make sure there's no GUI screen being displayed
            if (Minecraft.getMinecraft().currentScreen == null) {
                this.renderSpotify();
            }
        }
    }

    // The concatenated name length
    private int concatNameCount = 0;
    // If the tick is the first one for renderSpotify
    private boolean ifirst = true;
    // The concatenated artist name length
    private int concatArtistCount = 0;

    private static Color averageColor(BufferedImage bi, int x0, int y0, int w, int h) {
        if (avgColorCache.containsKey(bi)) {
            return avgColorCache.get(bi);
        } else {
            int x1 = x0 + w;
            int y1 = y0 + h;
            long sumr = 0, sumg = 0, sumb = 0;
            for (int x = x0; x < x1; x++) {
                for (int y = y0; y < y1; y++) {
                    Color pixel = new Color(bi.getRGB(x, y));
                    sumr += pixel.getRed();
                    sumg += pixel.getGreen();
                    sumb += pixel.getBlue();
                }
            }
            int num = w * h;
            Color color = new Color((int) sumr / num, (int) sumg / num, (int) sumb / num);

            avgColorCache.put(bi, color);
            return color;
        }
    }

    /**
     * Renders the Spotify HUD
     */

    private void renderSpotify() {
        // Initialize a font renderer
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRendererObj;

        int textX = 10;

        if (Settings.SHOW_ALBUM_ART) {
            textX = 50;
        }

        // Track Metadata
        Track track = currentlyPlayingObject.item;

        if (track == null) {
            Gui.drawRect(100, 5, 5, 25, Color.darkGray.getRGB());
            fontRenderer.drawString("Not Playing", 50, 11, -1);
            return;
        }
        int color = -1;
        URL url = null;
        try {
            url = new URL(track.album.images[0].url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (Settings.AUTO_COLOR_SELECTION && Settings.SHOW_ALBUM_ART) {
            BufferedImage image = DynamicTextureWrapper.getImage(url);
            color = averageColor(image, 0, 0, image.getWidth(), image.getHeight()).getRGB();

            // Draw the background of the player
            Gui.drawRect(150, 5, 5, 50, averageColor(image, 0, 0, image.getWidth(), image.getHeight()).darker().getRGB());
        } else {
            // Draw the background of the player
            Gui.drawRect(150, 5, 5, 50, Color.darkGray.getRGB());
        }

        // Establish track metadata (name, artist, spotify id)
        String title = track.name;
        ArrayList<String> artistSimplifiedList = Arrays.stream(track.album.artists).map(artist ->
                artist.name).collect(Collectors.toCollection(ArrayList::new));

        String artists = String.join(", ", artistSimplifiedList);

        if (title.length() > 17) {
            // Concatenate the string if the length is larger than 17, it will appear like this:
            // Initial String: HELLO WORLD!

            // Set the concatenated title to the title + 3 spaces + the title
            AtomicInteger concatNameCount2 = new AtomicInteger(concatNameCount + 17);
            String concatName = title + "    " + title;

            // If the length of the next concatenated title is larger or equal to the current concatenated title, reset the
            // name count to 0
            if ((concatNameCount + 16) >= concatName.length())
                concatNameCount = 0;

            // Draw the string
            Minecraft.getMinecraft().fontRendererObj.drawString(concatName.substring(concatNameCount, concatNameCount + 16), textX, 11, -1, false);

            // Every 500ms add the
            if (ifirst) {
                ifirst = false;
                ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
                exec.scheduleAtFixedRate(() -> {
                    concatNameCount++;
                    concatNameCount2.set(concatNameCount + 16);
                }, 0, 500, TimeUnit.MILLISECONDS);
            }
        } else {
            // The string is less than 17 characters, draw it normally
            fontRenderer.drawString(title, textX, 11, -1);
        }

        if (artists.length() > 17) {
            // Concatenate the string if the length is larger than 17, it will appear like this:
            // Initial String: HELLO WORLD!

            // Set the concatenated title to the title + 3 spaces + the title
            AtomicInteger concatNameCount2 = new AtomicInteger(concatArtistCount + 17);
            String concatName = "by " + artists + "    by " + artists;

            // If the length of the next concatenated title is larger or equal to the current concatenated title, reset the
            // name count to 0
            if ((concatArtistCount + 16) >= concatName.length())
                concatArtistCount = 0;

            // Draw the string
            Minecraft.getMinecraft().fontRendererObj.drawString(concatName.substring(concatArtistCount, concatArtistCount + 16), textX, 20, white.darker().getRGB(), false);

            // Every 500ms add the
            if (ifirst) {
                ifirst = false;
                ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
                exec.scheduleAtFixedRate(() -> {
                    concatArtistCount++;
                    concatNameCount2.set(concatArtistCount + 16);
                }, 0, 500, TimeUnit.MILLISECONDS);
            }
        } else {
            // The string is less than 17 characters, draw it normally
            fontRenderer.drawString("by " + artists, textX, 20, white.darker().getRGB());
        }

        // Get progress and duration in the Duration class
        float percentComplete = (float) currentlyPlayingObject.progress_ms / (float) track.duration_ms;

        // Draw Progress Bar
        Gui.drawRect(textX, 33, textX + 90, 41, Color.darkGray.darker().getRGB());
        if (Settings.AUTO_COLOR_SELECTION && Settings.SHOW_ALBUM_ART) {
            Gui.drawRect(textX, 33, (int) (textX + (90 * percentComplete)), 41, color);
        } else {
            Gui.drawRect(textX, 33, (int) (textX + (90 * percentComplete)), 41, Color.green.getRGB());
        }

        if (Settings.SHOW_ALBUM_ART) {
            GlStateManager.pushMatrix();
            GlStateManager.color(1, 1, 1, 1);

            // Bind the texture for rendering
            Minecraft.getMinecraft().getTextureManager().bindTexture(DynamicTextureWrapper.getTexture(url));

            // Render the album art as 35x35
            Gui.drawModalRectWithCustomSizedTexture(10, 10, 0, 0, 35, 35, 35, 35);
            GlStateManager.popMatrix();
        }
    }

    /**
     * Fired when Minecraft is starting
     *
     * @param event - FMLInitializationEvent
     * @see FMLInitializationEvent
     */
    @EventHandler
    public void init(FMLInitializationEvent event) {
        LOGGER.info("MediaMod starting...");

        // Register event subscribers and commands
        MinecraftForge.EVENT_BUS.register(this);
        ClientCommandHandler.instance.registerCommand(new MediaModCommand());

        LOGGER.info("Attempting to register with analytics...");

        // Register with analytics
        if (Minecraft.getMinecraft().gameSettings.snooperEnabled) {
            boolean successful = BaseMod.init();

            if (successful) {
                LOGGER.info("Successfully registered with analytics!");
            } else {
                LOGGER.error("Failed to register with analytics...");
            }
        }

        // Load the config
        LOGGER.info("Loading configuration...");
        Settings.loadConfig();

        // Load Media Handlers
        ServiceHandler serviceHandler = ServiceHandler.INSTANCE;
        serviceHandler.registerHandler(new BrowserHandler());
        serviceHandler.registerHandler(new SpotifyHandler());

        serviceHandler.initializeHandlers();
    }

    /**
     * Checks if a class exists by the class name
     *
     * @param className - the class name including package (i.e. net.minecraft.client.Minecraft)
     * @return boolean
     */
    private boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}