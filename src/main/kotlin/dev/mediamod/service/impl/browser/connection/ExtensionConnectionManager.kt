package dev.mediamod.service.impl.browser.connection

import dev.mediamod.data.api.browser.ExtensionTrackInfo
import dev.mediamod.utils.logger
import dev.mediamod.websocket.ExtensionSocket
import dev.mediamod.websocket.message.impl.incoming.IncomingSocketMessage
import dev.mediamod.websocket.message.impl.incoming.impl.IncomingTrackMessage
import dev.mediamod.websocket.message.impl.outgoing.impl.OutgoingTrackMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withTimeoutOrNull
import java.util.*

class ExtensionConnectionManager {
    private val flow = MutableSharedFlow<IncomingSocketMessage>()
    private val server = ExtensionSocket("localhost", 9104, flow)

    fun init() {
        try {
            server.start()
        } catch (error: Error) {
            logger.error("Failed to start extension websocket server: ", error)
        }
    }

    suspend fun requestTrack(): ExtensionTrackInfo? {
        val nonce = UUID.randomUUID().toString()
        server.sendMessage(OutgoingTrackMessage(nonce))

        return withTimeoutOrNull(3000) {
            flow
                .filterIsInstance<IncomingTrackMessage>()
                .firstOrNull { it.data.nonce == nonce }
                ?.data?.track
        }
    }
}