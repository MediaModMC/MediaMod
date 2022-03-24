package dev.mediamod.websocket

import dev.mediamod.utils.json
import dev.mediamod.utils.logger
import dev.mediamod.websocket.message.impl.incoming.IncomingSocketMessage
import dev.mediamod.websocket.message.impl.incoming.impl.IncomingHandshakeMessage
import dev.mediamod.websocket.message.impl.incoming.impl.IncomingHeartbeatMessage
import dev.mediamod.websocket.message.impl.outgoing.OutgoingSocketMessage
import dev.mediamod.websocket.message.impl.outgoing.impl.OutgoingHandshakeMessage
import dev.mediamod.websocket.message.impl.outgoing.impl.OutgoingHeartbeatMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.util.*
import kotlin.concurrent.fixedRateTimer

class ExtensionSocket(
    host: String,
    port: Int,
    private val messageFlow: MutableSharedFlow<IncomingSocketMessage>
) : WebSocketServer(InetSocketAddress(host, port)) {
    private var token: String? = null
    private var lastHeartbeat: Long = 0

    companion object {
        private val strictJson = Json { encodeDefaults = true }
    }

    init {
        fixedRateTimer("ExtensionSocket - Heartbeat", false, 0, 5000) {
            if (connections.isEmpty() || token == null) return@fixedRateTimer

            val difference = System.currentTimeMillis() - lastHeartbeat
            if (difference >= 3000) {
                logger.warn("Haven't received a heartbeat from the extension in 5 seconds! Invalidating connection.")
                token = null
            }
        }
    }

    override fun onStart() {
        logger.info("Extension websocket server started on localhost:9104!")
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

    override fun onMessage(socket: WebSocket, data: String) {
        val message = runCatching { json.decodeFromString<IncomingSocketMessage>(data) }.getOrNull()
            ?: return logger.error("Failed to parse message from websocket: $data")

        if (token != message.token) {
            logger.error("Received mismatched token!")
            return
        }

        when (message) {
            is IncomingHandshakeMessage -> {
                UUID.randomUUID().toString().let {
                    token = it
                    sendMessage(OutgoingHandshakeMessage(it))
                }
            }

            is IncomingHeartbeatMessage -> {
                lastHeartbeat = System.currentTimeMillis()
                sendMessage(OutgoingHeartbeatMessage())
            }
        }

        runBlocking { messageFlow.emit(message) }
    }

    internal fun sendMessage(packet: OutgoingSocketMessage) {
        val string = strictJson.encodeToString(packet)
        broadcast(string)
    }
}