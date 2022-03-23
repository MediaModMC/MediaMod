package dev.mediamod.websocket.message.impl.outgoing.impl

import dev.mediamod.websocket.message.impl.outgoing.OutgoingSocketMessage
import kotlinx.serialization.Serializable

@Serializable
data class OutgoingHeartbeatMessage(
    override val id: String = "HEARTBEAT",
    override val data: Data? = null
) : OutgoingSocketMessage() {
}