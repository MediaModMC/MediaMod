package dev.mediamod.websocket.message.impl.incoming.impl

import dev.mediamod.websocket.message.impl.incoming.IncomingSocketMessage
import kotlinx.serialization.Serializable

@Serializable
data class IncomingHeartbeatMessage(
    override val id: String = "HEARTBEAT",
    override val data: Data? = null,
    override val token: String
) : IncomingSocketMessage() {
}