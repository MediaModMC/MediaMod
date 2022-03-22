package dev.mediamod.websocket.message.impl.incoming

import dev.mediamod.websocket.message.serialization.IncomingSocketMessageSerializer
import kotlinx.serialization.Serializable

@Serializable(with = IncomingSocketMessageSerializer::class)
abstract class IncomingSocketMessage {
    abstract val id: String
    abstract val data: Data?
    abstract val token: String?

    @Serializable
    open class Data
}