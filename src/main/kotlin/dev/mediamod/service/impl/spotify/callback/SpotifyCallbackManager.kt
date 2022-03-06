package dev.mediamod.service.impl.spotify.callback

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import dev.mediamod.MediaMod
import dev.mediamod.utils.logger
import java.net.InetSocketAddress
import kotlin.concurrent.thread

class SpotifyCallbackManager {
    fun init() {
        try {
            val server = HttpServer.create(InetSocketAddress("localhost", 9103), 0)
            server.createContext("/callback", CallbackRoute())
            server.start()

            logger.info("Spotify callback server started on localhost:9103!")
        } catch (error: Error) {
            logger.error("Failed to start Spotify callback server: ", error)
        }
    }

    class CallbackRoute : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            val params = exchange.requestURI.query
                .split("&")
                .associate {
                    val (left, right) = it.split("=")
                    left to right
                }

            // TODO: Error handling
            val code = params["code"] ?: return exchange.sendResponse("Invalid oauth code!")

            thread(true) {
                val result = MediaMod.apiManager.exchangeCode(code)
                result.onSuccess {
                    logger.info(it)
                }
                result.onFailure {
                    logger.error(it.message)
                }
            }

            exchange.sendResponse("You can close this window and return to Minecraft.")
        }

        private fun HttpExchange.sendResponse(message: String) {
            sendResponseHeaders(200, message.length.toLong())
            responseBody.write(message.encodeToByteArray())
            responseBody.close()
        }
    }
}