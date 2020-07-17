package dev.conorthedev.mediamod.event;

import dev.conorthedev.mediamod.media.core.api.MediaInfo;
import net.minecraftforge.fml.common.eventhandler.Event;

public class MediaInfoUpdateEvent extends Event {
    public MediaInfo mediaInfo;

    public MediaInfoUpdateEvent(MediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
    }
}
