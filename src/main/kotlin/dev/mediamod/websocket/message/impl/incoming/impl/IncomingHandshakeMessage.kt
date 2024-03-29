package dev.mediamod.websocket.message.impl.incoming.impl

import dev.mediamod.websocket.message.impl.incoming.IncomingSocketMessage
import kotlinx.serialization.Serializable

@Serializable
data class IncomingHandshakeMessage(
    override val id: String = "HANDSHAKE",
    override val data: Data? = null,
    override val token: String? = null
) : IncomingSocketMessage() {
}