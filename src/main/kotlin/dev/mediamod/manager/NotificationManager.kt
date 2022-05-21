package dev.mediamod.manager

//#if FABRIC!=0
import dev.cbyrne.toasts.impl.builder.BasicToastBuilder
//#elseif MC<=11202
//$$ import gg.essential.api.EssentialAPI
//#endif

import dev.mediamod.MediaMod
import dev.mediamod.config.Configuration
import dev.mediamod.data.Track

class NotificationManager {
    private var previousTrack: Track? = null

    fun init() {
        MediaMod.serviceManager.currentTrack.onSetValue { track ->
            if (!Configuration.trackNotifications || track == null) return@onSetValue
            if (previousTrack?.name == track.name || previousTrack?.artist == track.artist) return@onSetValue

            showNotification("Now playing", "${track.name} by ${track.artist}")
            previousTrack = track
        }
    }

    fun showNotification(title: String, message: String) {
        //#if FABRIC!=0
        BasicToastBuilder()
            .title(title)
            .description(message)
            .build()
            .show()
        //#elseif MC<=11202
        //$$ EssentialAPI.getNotifications().push(title, message)
        //#endif
    }
}