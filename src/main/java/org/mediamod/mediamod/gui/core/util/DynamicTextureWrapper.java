package org.mediamod.mediamod.gui.core.util;


import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
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
 */
public class DynamicTextureWrapper {

    /**
     * Hashmap of album art textures
     */
    private static final Map<URL, WrappedResource> URL_TEXTURES = new HashMap<>();

    /**
     * Hash Map of album art images
     */
    private static final Map<URL, WrappedImage> URL_IMAGES = new HashMap<>();

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
    private static boolean INITIALIZED;

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
        if (!INITIALIZED) {
            try {
                GLContext.getCapabilities();
            } catch (RuntimeException ignored) {
                return;
            }
            FULLY_TRANSPARENT_TEXTURE = FMLClientHandler.instance().getClient().getTextureManager().getDynamicTextureLocation(
                    "mediamod", new DynamicTexture(FULLY_TRANSPARENT_IMAGE));
            INITIALIZED = true;
        }
    }

    public static BufferedImage getImage(URL url) {
        init();
        if (!URL_IMAGES.containsKey(url)) {
            queueImage(url);
        }
        WrappedImage image = URL_IMAGES.get(url);
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
        if (!URL_TEXTURES.containsKey(url)) {
            URL_TEXTURES.put(url, new WrappedResource(null));
            queueImage(url);
        }

        WrappedResource wr = URL_TEXTURES.get(url);
        if (wr.location == null) {
            if (URL_IMAGES.get(url) != null && URL_IMAGES.get(url).image != null) {
                DynamicTexture texture = new DynamicTexture(URL_IMAGES.get(url).image);
                WrappedResource wr2 = new WrappedResource(FMLClientHandler.instance().getClient().getTextureManager().getDynamicTextureLocation(url.toString(), texture));
                URL_TEXTURES.put(url, wr2);
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
        URL_IMAGES.put(url, new WrappedImage(null));
        new Thread(() -> {
            try {
                BufferedImage image = ImageIO.read(url);
                URL_IMAGES.put(url, new WrappedImage(image));
            } catch (IOException e) {
                URL_IMAGES.put(url, new WrappedImage(MISSING_IMAGE));
            }
        }).start();
    }

    static class WrappedResource {
        final ResourceLocation location;

        WrappedResource(ResourceLocation location) {
            this.location = location;
        }
    }

    static class WrappedImage {
        final BufferedImage image;

        WrappedImage(BufferedImage image) {
            this.image = image;
        }
    }
}
