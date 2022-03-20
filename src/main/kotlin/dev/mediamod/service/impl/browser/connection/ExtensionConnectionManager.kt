package dev.mediamod.service.impl.browser.connection

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import dev.mediamod.data.api.browser.ExtensionTrackInfo
import dev.mediamod.utils.json
import dev.mediamod.utils.logger
import kotlinx.serialization.decodeFromString
import java.net.InetSocketAddress

class ExtensionConnectionManager {
    private val trackInfoListeners = mutableSetOf<((ExtensionTrackInfo) -> Unit)>()

    fun init() {
        try {
            val server = HttpServer.create(InetSocketAddress("localhost", 9104), 0)
            server.createContext("/extension/update", ExtensionUpdateRoute(this))
            server.start()

            logger.info("Extension callback server started on localhost:9104!")
        } catch (error: Error) {
            logger.error("Failed to start Spotify callback server: ", error)
        }
    }

    fun onTrack(block: (ExtensionTrackInfo) -> Unit) = trackInfoListeners.add(block)

    class ExtensionUpdateRoute(private val manager: ExtensionConnectionManager) : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            if (exchange.requestMethod != "POST") return exchange.respond()

            try {
                val body = exchange.requestBody.readBytes().decodeToString()
                val info: ExtensionTrackInfo = json.decodeFromString(body)

                manager.trackInfoListeners.forEach { it.invoke(info) }
            } catch (ignored: Error) {
            }

            return exchange.respond()
        }

        private fun HttpExchange.respond() {
            sendResponseHeaders(200, "OK".length.toLong())
            responseBody.write("OK".encodeToByteArray())
            responseBody.close()
        }
    }
}