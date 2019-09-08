package me.conorthedev.mediamod.gui;

import me.conorthedev.mediamod.Settings;
import me.conorthedev.mediamod.gui.util.DynamicTextureWrapper;
import me.conorthedev.mediamod.media.base.ServiceHandler;
import me.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;
import me.conorthedev.mediamod.media.spotify.api.track.Track;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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

public class PlayerOverlay {
    /**
     * An instance of this class
     */
    public static final PlayerOverlay INSTANCE = new PlayerOverlay();

    /**
     * Average Color Cache
     */
    private static final HashMap<BufferedImage, Color> avgColorCache = new HashMap<>();

    // If the tick is the first one
    private boolean first = true;
    /**
     * The current song
     *
     * @see me.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject
     */
    private CurrentlyPlayingObject currentlyPlayingObject = null;
    // The concatenated name length
    private int concatNameCount = 0;
    // If the tick is the first one for renderPlayer
    private boolean ifirst = true;
    // The concatenated artist name length
    private int concatArtistCount = 0;

    public static Color averageColor(BufferedImage bi, int w, int h) {
        if (avgColorCache.containsKey(bi)) {
            return avgColorCache.get(bi);
        } else {
            long sumr = 0, sumg = 0, sumb = 0;
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
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
     * Fired when a game overlay is being rendered
     *
     * @param event - RenderGameOverlayEvent
     * @see RenderGameOverlayEvent
     */
    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent event) {
        if (first) {
            // Make sure that this is never ran again
            first = false;

            // Setup a ScheduledExecutorService to run every 3 seconds
            ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
            exec.scheduleAtFixedRate(() -> {
                try {
                    // Check if we are logged in
                    if (ServiceHandler.INSTANCE.getCurrentMediaHandler().handlerReady()) {
                        // Set the currentlyPlayingContext to the current song
                        currentlyPlayingObject = ServiceHandler.INSTANCE.getCurrentMediaHandler().getCurrentTrack();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 0, 3, TimeUnit.SECONDS);
        }

        // Check if we're logged in & the hotbar is being rendered
        if (ServiceHandler.INSTANCE.getCurrentMediaHandler().handlerReady() && currentlyPlayingObject != null && event.type == RenderGameOverlayEvent.ElementType.HOTBAR) {
            // Make sure there's no GUI screen being displayed
            if (Minecraft.getMinecraft().currentScreen == null) {
                this.renderPlayer();
            }
        }
    }

    /**
     * Renders the Player HUD
     */
    private void renderPlayer() {
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

        Color color = Color.gray;
        URL url = null;
        try {
            url = new URL(track.album.images[0].url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // Draw the outline of the player
        Gui.drawRect(151, 4, 4, 51, new Color(0, 0, 0, 75).getRGB());

        if (Settings.AUTO_COLOR_SELECTION && Settings.SHOW_ALBUM_ART) {
            BufferedImage image = DynamicTextureWrapper.getImage(url);
            color = averageColor(image, image.getWidth(), image.getHeight());

            // Draw the background of the player
            Gui.drawRect(150, 5, 5, 50, averageColor(image, image.getWidth(), image.getHeight()).darker().getRGB());
        } else {
            // Draw the background of the player
            Gui.drawRect(150, 5, 5, 50, Color.darkGray.getRGB());
        }

        // Establish track metadata (name, artist, etc)
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
        // Draw outline
        //Gui.drawRect(textX + 11, 9, textX + 101, 42, new Color(0, 0, 0, 75).getRGB());

        Gui.drawRect(textX - 1, 32, textX + 91, 42, new Color(0, 0, 0, 75).getRGB());
        Gui.drawRect(textX, 33, textX + 90, 41, Color.darkGray.darker().getRGB());

        if (Settings.AUTO_COLOR_SELECTION && Settings.SHOW_ALBUM_ART) {
            drawGradientRect(textX, 33, (int) (textX + (90 * percentComplete)), 41, color.getRGB(), color.darker().getRGB());
        } else {
            Gui.drawRect(textX, 33, (int) (textX + (90 * percentComplete)), 41, Color.green.getRGB());
        }

        if (Settings.SHOW_ALBUM_ART) {
            // Draw outline
            Gui.drawRect(46, 9, 9, 46, new Color(0, 0, 0, 75).getRGB());

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
     * Draws a rectangle with a vertical gradient between the specified colors (ARGB format). Args : x1, y1, x2, y2,
     * topColor, bottomColor
     */
    protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
    {
        float f = (float)(startColor >> 24 & 255) / 255.0F;
        float f1 = (float)(startColor >> 16 & 255) / 255.0F;
        float f2 = (float)(startColor >> 8 & 255) / 255.0F;
        float f3 = (float)(startColor & 255) / 255.0F;
        float f4 = (float)(endColor >> 24 & 255) / 255.0F;
        float f5 = (float)(endColor >> 16 & 255) / 255.0F;
        float f6 = (float)(endColor >> 8 & 255) / 255.0F;
        float f7 = (float)(endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        worldrenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
        worldrenderer.pos(right, top, 1).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, top, 1).color(f1, f2, f3, f).endVertex();
        worldrenderer.pos(left, bottom, 1).color(f5, f6, f7, f4).endVertex();
        worldrenderer.pos(right, bottom, 1).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
}
