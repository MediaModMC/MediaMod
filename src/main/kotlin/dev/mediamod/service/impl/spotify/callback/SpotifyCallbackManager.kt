package dev.mediamod.service.impl.spotify.callback

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.result.Result
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import dev.mediamod.MediaMod
import dev.mediamod.data.api.mediamod.APIResponse
import dev.mediamod.utils.logger
import java.net.InetSocketAddress
import kotlin.concurrent.thread

class SpotifyCallbackManager {
    private val callbackResultListeners = mutableSetOf<(Result<APIResponse, FuelError>.() -> Unit)>()

    fun init() {
        try {
            val server = HttpServer.create(InetSocketAddress("localhost", 9103), 0)
            server.createContext("/callback", CallbackRoute(this))
            server.start()

            logger.info("Spotify callback server started on localhost:9103!")
        } catch (error: Error) {
            logger.error("Failed to start Spotify callback server: ", error)
        }
    }

    fun onCallback(callback: Result<APIResponse, FuelError>.() -> Unit) =
        callbackResultListeners.add(callback)

    class CallbackRoute(private val manager: SpotifyCallbackManager) : HttpHandler {
        override fun handle(exchange: HttpExchange) {
            val params = exchange.requestURI.query
                .split("&")
                .associate {
                    val (left, right) = it.split("=")
                    left to right
                }

            val code = params["code"] ?: return exchange.sendResponse("Invalid oauth code!")

            thread(true) {
                val result = MediaMod.apiManager.exchangeCode(code)
                manager.callbackResultListeners.forEach { it.invoke(result) }
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