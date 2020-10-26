package me.dreamhopping.mediamod.gui;

import me.dreamhopping.mediamod.event.MediaInfoUpdateEvent;
import me.dreamhopping.mediamod.media.core.api.track.Track;
import me.dreamhopping.mediamod.util.Multithreading;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class GuiMediaPlayerManager {
    public final ArrayList<Track> previousTracks = new ArrayList<>();
    public volatile Track currentTrack = null;

    public static final GuiMediaPlayerManager instance = new GuiMediaPlayerManager();

    @SubscribeEvent
    public void onMediaInfoChange(MediaInfoUpdateEvent e) {
        Multithreading.runAsync(() -> {
            if (e.mediaInfo != null) {
                previousTracks.add(previousTracks.size(), currentTrack);
                Collections.reverse(previousTracks);
                GuiMediaPlayerManager.instance.previousTracks.removeIf(Objects::isNull);

                currentTrack = e.mediaInfo.track;
            } else {
                currentTrack = null;
            }
        });

    }
}
