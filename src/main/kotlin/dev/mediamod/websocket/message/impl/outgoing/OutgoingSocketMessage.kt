package dev.mediamod.websocket.message.impl.outgoing

import dev.mediamod.websocket.message.serialization.OutgoingSocketMessageSerializer
import kotlinx.serialization.Serializable

@Serializable(with = OutgoingSocketMessageSerializer::class)
abstract class OutgoingSocketMessage {
    abstract val id: String
    abstract val data: Data

    @Serializable
    open class Data
}