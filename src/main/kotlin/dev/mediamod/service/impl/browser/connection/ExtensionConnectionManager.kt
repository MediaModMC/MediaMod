package dev.mediamod.service.impl.browser.connection

import dev.mediamod.utils.logger
import dev.mediamod.websocket.ExtensionSocket

class ExtensionConnectionManager {
    private val server = ExtensionSocket("localhost", 9104)

    fun init() {
        try {
            server.start()
        } catch (error: Error) {
            logger.error("Failed to start extension websocket server: ", error)
        }
    }
}