package dev.mediamod.websocket.message.impl.outgoing.impl

import dev.mediamod.websocket.message.impl.outgoing.OutgoingSocketMessage
import kotlinx.serialization.Serializable

@Serializable
data class OutgoingTrackMessage(
    override val id: String = "TRACK",
    override val data: RequestData
) : OutgoingSocketMessage() {
    constructor(nonce: String) : this(data = RequestData(nonce))

    @Serializable
    data class RequestData(
        val nonce: String
    ) : Data()
}