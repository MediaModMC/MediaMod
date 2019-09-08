package me.conorthedev.mediamod.gui.util;


import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GLContext;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Multithreading resource getting
 *
 * @author Amplifiable
 */
public class DynamicTextureWrapper {

    /**
     * Hashmap of album art textures
     */
    private static final Map<URL, WrappedResource> urlTextures = new HashMap<>();

    /**
     * Hash Map of album art images
     */
    private static final Map<URL, WrappedImage> urlImages = new HashMap<>();

    /**
     * A fully transparent image
     */
    private static final BufferedImage FULLY_TRANSPARENT_IMAGE;

    /**
     * The missing image image
     */
    private static final BufferedImage MISSING_IMAGE;

    /**
     * The resource location of the fully transparent texture
     */
    private static ResourceLocation FULLY_TRANSPARENT_TEXTURE;

    /**
     * If the DynamicTextureWrapper was initialized already
     */
    private static boolean initialized;

    static {
        FULLY_TRANSPARENT_IMAGE = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        FULLY_TRANSPARENT_IMAGE.setRGB(0, 0, new Color(0, 0, 0, 0).getRGB());
        MISSING_IMAGE = new BufferedImage(2, 2, BufferedImage.TYPE_INT_ARGB);
        MISSING_IMAGE.setRGB(0, 0, -524040);
        MISSING_IMAGE.setRGB(1, 1, -524040);
        MISSING_IMAGE.setRGB(0, 1, -16777216);
        MISSING_IMAGE.setRGB(1, 0, -16777216);
    }

    /**
     * Initialize key components
     */
    private static void init() {
        if (!initialized) {
            try {
                GLContext.getCapabilities();
            } catch (RuntimeException ignored) {
                return;
            }
            FULLY_TRANSPARENT_TEXTURE = Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation("no_album_art", new DynamicTexture(FULLY_TRANSPARENT_IMAGE));
            initialized = true;
        }
    }

    public static BufferedImage getImage(URL url) {
        init();
        if (!urlImages.containsKey(url)) {
            queueImage(url);
        }
        WrappedImage image = urlImages.get(url);
        if (image.image == null) {
            return FULLY_TRANSPARENT_IMAGE;
        }
        return image.image;
    }

    /**
     * Get resource location from url
     *
     * @param url - url to get the file from
     * @return ResourceLocation
     */
    public static ResourceLocation getTexture(URL url) {
        init();
        if (!urlTextures.containsKey(url)) {
            urlTextures.put(url, new WrappedResource(null));
            queueImage(url);
        }

        WrappedResource wr = urlTextures.get(url);
        if (wr.location == null) {
            if (urlImages.get(url) != null && urlImages.get(url).image != null) {
                DynamicTexture texture = new DynamicTexture(urlImages.get(url).image);
                WrappedResource wr2 = new WrappedResource(Minecraft.getMinecraft().getTextureManager().getDynamicTextureLocation(url.toString(), texture));
                urlTextures.put(url, wr2);
                return wr2.location;
            } else {
                return FULLY_TRANSPARENT_TEXTURE;
            }
        }

        return wr.location;
    }

    /**
     * Get BufferedImage from URL
     *
     * @param url - url to get BufferedImage from
     */
    private static void queueImage(URL url) {
        init();
        urlImages.put(url, new WrappedImage(null));
        new Thread(() -> {
            try {
                BufferedImage image = ImageIO.read(url);
                urlImages.put(url, new WrappedImage(image));
            } catch (IOException e) {
                urlImages.put(url, new WrappedImage(MISSING_IMAGE));
            }
        }).start();
    }

    public static class WrappedResource {
        final ResourceLocation location;

        WrappedResource(ResourceLocation location) {
            this.location = location;
        }
    }

    public static class WrappedImage {
        final BufferedImage image;

        WrappedImage(BufferedImage image) {
            this.image = image;
        }
    }
}