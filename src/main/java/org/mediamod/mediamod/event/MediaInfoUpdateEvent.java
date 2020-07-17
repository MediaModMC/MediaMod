package org.mediamod.mediamod.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import org.mediamod.mediamod.media.core.api.MediaInfo;

public class MediaInfoUpdateEvent extends Event {
    public MediaInfo mediaInfo;

    public MediaInfoUpdateEvent(MediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
    }
}
