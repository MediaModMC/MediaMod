package dev.mediamod.manager

//#if FABRIC!=0
import dev.cbyrne.toasts.impl.builder.BasicToastBuilder
//#elseif MC<=11202
//$$ import gg.essential.api.EssentialAPI
//#endif

class NotificationManager {
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