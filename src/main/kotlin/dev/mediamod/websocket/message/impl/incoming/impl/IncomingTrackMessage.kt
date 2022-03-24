package dev.mediamod.websocket.message.impl.incoming.impl

import dev.mediamod.data.api.browser.ExtensionTrackInfo
import dev.mediamod.websocket.message.impl.incoming.IncomingSocketMessage
import kotlinx.serialization.Serializable

@Serializable
data class IncomingTrackMessage(
    override val id: String = "TRACK",
    override val data: TrackData,
    override val token: String
) : IncomingSocketMessage() {
    @Serializable
    data class TrackData(
        val track: ExtensionTrackInfo? = null,
        val nonce: String
    ) : Data()
}