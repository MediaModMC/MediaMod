package org.mediamod.mediamod.levelhead;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.mediamod.mediamod.MediaMod;
import org.mediamod.mediamod.api.APIHandler;
import org.mediamod.mediamod.config.Settings;
import org.mediamod.mediamod.event.MediaInfoUpdateEvent;
import org.mediamod.mediamod.util.WebRequest;
import org.mediamod.mediamod.util.WebRequestType;

import javax.swing.text.html.parser.Entity;
import java.io.IOException;

/**
 * The class that manages the Levelhead integration
 */
public class LevelheadIntegration {
    @SubscribeEvent
    public void onMediaChange(MediaInfoUpdateEvent event) {
        EntityPlayer player = FMLClientHandler.instance().getClient().thePlayer;
        if (player != null && event.mediaInfo != null && Settings.LEVELHEAD_ENABLED && MediaMod.INSTANCE.authenticatedWithAPI) {
            try {
                JsonObject body = new JsonObject();
                body.addProperty("uuid", player.getUniqueID().toString());
                body.addProperty("secret", APIHandler.instance.requestSecret);
                body.addProperty("track", new Gson().toJson(new LevelheadTrack(event.mediaInfo.track.name, event.mediaInfo.track.artists[0].name)));

                WebRequest.requestToMediaMod(WebRequestType.POST, "api/levelhead/update", body);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static class LevelheadTrack {
        String name;
        String artist;

        LevelheadTrack(String name, String artist) {
            this.name = name;
            this.artist = artist;
        }
    }
}
