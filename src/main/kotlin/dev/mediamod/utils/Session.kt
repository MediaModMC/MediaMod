package dev.mediamod.utils

import gg.essential.universal.UMinecraft

// This weird class exists due to an issue with remapping that I'm too lazy to figure out right now
object Session {
    val username: String
        get() {
            //#if MC>=11801
            return UMinecraft.getMinecraft().session.username
            //#elseif MC<=11202
            //$$ return UMinecraft.getMinecraft().session.username
            //#endif
        }

    val accessToken: String
        get() {
            //#if MC>=11801
            return UMinecraft.getMinecraft().session.accessToken
            //#elseif MC<=11202
            //$$ return UMinecraft.getMinecraft().session.token
            //#endif
        }

    val uuid: String
        get() {
            //#if MC>=11801
            return UMinecraft.getMinecraft().session.uuid
            //#elseif MC<=11202
            //$$ return UMinecraft.getMinecraft().session.playerID
            //#endif
        }
}