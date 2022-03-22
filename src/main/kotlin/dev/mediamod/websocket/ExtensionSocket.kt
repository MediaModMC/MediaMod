package dev.mediamod.websocket

import dev.mediamod.utils.json
import dev.mediamod.utils.logger
import dev.mediamod.websocket.message.impl.incoming.IncomingSocketMessage
import dev.mediamod.websocket.message.impl.incoming.impl.IncomingHandshakeMessage
import dev.mediamod.websocket.message.impl.outgoing.OutgoingSocketMessage
import dev.mediamod.websocket.message.impl.outgoing.impl.OutgoingHandshakeMessage
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.util.*

class ExtensionSocket(host: String, port: Int) : WebSocketServer(InetSocketAddress(host, port)) {
    private var token: String? = null

    companion object {
        private val strictJson = Json { encodeDefaults = true }
    }

    override fun onOpen(socket: WebSocket, handshake: ClientHandshake) {
        logger.info("Connection opened!")
    }

    override fun onClose(socket: WebSocket, code: Int, reason: String, remote: Boolean) {
        // Clear the current connection
        logger.warn("Extension connection closed")
        token = null
    }

    override fun onError(socket: WebSocket?, ex: Exception) {
        // Depending on the error, we may need to close the connection
        logger.error("Extension socket encountered an error: ", ex)
    }

    override fun onMessage(socket: WebSocket, message: String) {
        logger.info("Received message: $message")

        val data = runCatching { json.decodeFromString<IncomingSocketMessage>(message) }.getOrNull()
            ?: return logger.error("Failed to parse message from websocket: $message")

        when (data) {
            is IncomingHandshakeMessage -> {
                if (token != null) {
                    // TODO: Terminate connection
                    logger.error("Attempt to create a connection when a client was already connected!")
                }

                UUID.randomUUID().toString().let {
                    token = it
                    sendMessage(OutgoingHandshakeMessage(it))
                }
            }
        }
    }

    override fun onStart() {
        logger.info("Extension websocket server started on localhost:9104!")
    }

    private fun sendMessage(packet: OutgoingSocketMessage) {
        val string = strictJson.encodeToString(packet)
        broadcast(string)
    }
}