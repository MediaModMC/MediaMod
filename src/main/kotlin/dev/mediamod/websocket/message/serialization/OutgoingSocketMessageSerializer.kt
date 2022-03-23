package dev.mediamod.websocket.message.serialization

import dev.mediamod.websocket.message.impl.outgoing.OutgoingSocketMessage
import dev.mediamod.websocket.message.impl.outgoing.impl.OutgoingHandshakeMessage
import dev.mediamod.websocket.message.impl.outgoing.impl.OutgoingHeartbeatMessage
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.*

object OutgoingSocketMessageSerializer : JsonContentPolymorphicSerializer<OutgoingSocketMessage>(
    OutgoingSocketMessage::class
) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<out OutgoingSocketMessage> =
        when (val id = element.contentOrNull("id")) {
            "HANDSHAKE" -> OutgoingHandshakeMessage.serializer()
            "HEARTBEAT" -> OutgoingHeartbeatMessage.serializer()
            else -> error("Unknown outgoing message id: $id")
        }

    private fun JsonElement.contentOrNull(key: String) =
        jsonObject[key]?.jsonPrimitive?.contentOrNull
}