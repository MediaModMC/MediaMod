package dev.cbyrne.mediamod.services.impl

import club.sk1er.mods.core.universal.ChatColor
import dev.cbyrne.mediamod.apiEndpoint
import dev.cbyrne.mediamod.services.IServiceHandler
import dev.cbyrne.mediamod.services.media.Track
import dev.cbyrne.mediamod.utils.PlayerMessager
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.Json
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.get
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import net.minecraft.event.ClickEvent
import net.minecraft.event.HoverEvent
import net.minecraft.util.ChatComponentText
import net.minecraft.util.IChatComponent
import net.minecraftforge.fml.client.FMLClientHandler
import java.awt.Desktop
import java.net.URI

data class MediaModAPIResponse(val accessToken: String, val expiresIn: Int, val refreshToken: String)

class SpotifyAuthHandler {
    private lateinit var wrapper: SpotifyAPIWrapper

    val client = HttpClient(Apache) {
        Json {
            serializer = GsonSerializer()
        }
    }

    suspend fun handleAuthRequest(code: String) {
        PlayerMessager.sendMessage(
            "${ChatColor.GRAY} Exchanging authorization code for access token, this may take a moment...",
            true
        )

        val response: MediaModAPIResponse = client.get("$apiEndpoint/api/spotify/getToken") {
            header("user-agent", "MediaMod/2.0")
            parameter("code", code)
        }

        wrapper = SpotifyAPIWrapper(response.accessToken, response.refreshToken)
        PlayerMessager.sendMessage("${ChatColor.GREEN}SUCCESS! ${ChatColor.RESET}Logged into Spotify!", true)
    }

    suspend fun handleRefreshRequest() {
        if (this::wrapper.isInitialized) {
            if (FMLClientHandler.instance().client.thePlayer != null) {
                PlayerMessager.sendMessage("${ChatColor.DARK_GRAY}INFO: ${ChatColor.RESET}Attempting to refresh access token...")
            }

            val response: MediaModAPIResponse = client.get("$apiEndpoint/api/spotify/refreshToken") {
                header("user-agent", "MediaMod/2.0")
                parameter("refreshToken", wrapper.refreshToken)
            }

            wrapper = SpotifyAPIWrapper(response.accessToken, response.refreshToken)
            PlayerMessager.sendMessage("${ChatColor.GREEN}SUCCESS! ${ChatColor.RESET}Logged into Spotify!", true)
        }

    }

    fun openAuthURL() {
        val desktop: Desktop = Desktop.getDesktop()

        try {
            desktop.browse(URI("https://accounts.spotify.com/authorize?client_id=4d33df7152bb4e2dac57167eeaafdf45&response_type=code&redirect_uri=http%3A%2F%2Flocalhost:9103%2Fcallback%2F&scope=user-read-playback-state%20user-read-currently-playing%20user-modify-playback-state&state=34fFs29kd09"))
        } catch (e: Exception) {
            PlayerMessager.sendMessage("&cFailed to open browser with the Spotify Auth URL!")

            val urlComponent: IChatComponent =
                ChatComponentText(ChatColor.translateAlternateColorCodes('&', "&lOpen URL"))

            urlComponent.chatStyle.chatClickEvent = ClickEvent(
                ClickEvent.Action.OPEN_URL,
                "https://accounts.spotify.com/authorize?client_id=4d33df7152bb4e2dac57167eeaafdf45&response_type=code&redirect_uri=http%3A%2F%2Flocalhost:9103%2Fcallback%2F&scope=user-read-playback-state%20user-read-currently-playing%20user-modify-playback-state&state=34fFs29kd09"
            )

            urlComponent.chatStyle.chatHoverEvent = HoverEvent(
                HoverEvent.Action.SHOW_TEXT, ChatComponentText(
                    ChatColor.translateAlternateColorCodes(
                        '&',
                        "&7Click this to open the Spotify Auth URL"
                    )
                )
            )
            PlayerMessager.sendMessage(urlComponent)
        }
    }
}

class SpotifyService : IServiceHandler {
    lateinit var wrapper: SpotifyAPIWrapper

    override val handlerName: String
        get() = "Spotify"
    override var handlerReady: Boolean
        get() = this::wrapper.isInitialized
        set(value) {}
    override var estimatedProgress: Int
        get() = TODO("Not yet implemented")
        set(value) {}
    override var currentTrack: Track
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun initialize() {
        SpotifyLocalServerImpl.initialize()
    }

}

object SpotifyLocalServerImpl {
    val server = embeddedServer(Netty, port = 8080) {
        routing {
            get("/callback") {
                call.parameters["code"]?.let { it1 -> SpotifyAuthHandler().handleAuthRequest(it1) }

                call.respond(
                    """
                    <!DOCTYPE html>
                    <html>
                    <head>
                    <meta charset=\utf-8\>
                    <meta name=\viewport\ content=\width=device-width, initial-scale=1\>
                    <title>MediaMod</title>
                    <link rel=\stylesheet\ href=\https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.5/css/bulma.m${'$'}                            <script defer src=\https://use.fontawesome.com/releases/v5.3.1/js/all.js\></script>
                    </head>
                    <body class=\hero is-dark is-fullheight\>
                    <section class=\section has-text-centered\>
                    <div class=\container\>
                    <img src=\https://raw.githubusercontent.com/MediaModMC/MediaMod/master/src/main/resources${'$'}                              
                    <h1 class=\title\>
                    Success!
                    </h1>
                    <p class=\subtitle\>
                    Please close this window and go back into Minecraft!
                    </p>
                    </div>
                    </section>
                    </body>
                    </html>
                    """
                )
            }
        }
    }

    fun initialize() {
        server.start(wait = false)
    }
}

class SpotifyAPIWrapper(val accessToken: String, val refreshToken: String) {
    val client = HttpClient(Apache) {
        Json {
            serializer = GsonSerializer()
        }
    }

    suspend fun getCurrentTrack() = client.get<Track>("https://api.spotify.com/v1/me/player/currently-playing") {
        header("Authorization", "Bearer $accessToken")
    }
}