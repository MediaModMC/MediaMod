package org.mediamod.mediamod.event;

import net.minecraftforge.fml.common.eventhandler.Event;
import org.mediamod.mediamod.media.core.api.MediaInfo;

import javax.annotation.Nullable;

public class MediaInfoUpdateEvent extends Event {
    public MediaInfo mediaInfo;

    public MediaInfoUpdateEvent(@Nullable MediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
    }
}
