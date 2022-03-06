package dev.mediamod.service.impl.spotify.callback

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import dev.mediamod.utils.logger
import java.net.InetSocketAddress
import java.util.concurrent.Executors

class SpotifyCallbackManager {
    private val threadPoolExecutor = Executors.newCachedThreadPool()

    fun init() {
        try {
            val server = HttpServer.create(InetSocketAddress("localhost", 9103), 0)
            server.executor = threadPoolExecutor
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
                .split("=")
                .zipWithNext()
                .associate { it.first to it.second }

            // TODO: Error handling
            val code = params["code"] ?: return
        }
    }
}