package dev.mediamod.websocket.message.impl.outgoing.impl

import dev.mediamod.websocket.message.impl.outgoing.OutgoingSocketMessage
import kotlinx.serialization.Serializable

@Serializable
data class OutgoingHandshakeMessage(
    override val id: String = "HANDSHAKE",
    override val data: HandshakeData
) : OutgoingSocketMessage() {
    constructor(token: String) : this(data = HandshakeData(token))

    @Serializable
    data class HandshakeData(
        val token: String
    ) : Data()
}