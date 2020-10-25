package me.dreamhopping.mediamod.event;

import me.dreamhopping.mediamod.media.core.api.MediaInfo;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nullable;

public class MediaInfoUpdateEvent extends Event {
    public MediaInfo mediaInfo;

    public MediaInfoUpdateEvent(@Nullable MediaInfo mediaInfo) {
        this.mediaInfo = mediaInfo;
    }
}
