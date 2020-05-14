package dev.conorthedev.mediamod.media.spotify;

import com.google.gson.annotations.SerializedName;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dev.conorthedev.mediamod.MediaMod;
import dev.conorthedev.mediamod.config.Settings;
import dev.conorthedev.mediamod.media.base.AbstractMediaHandler;
import dev.conorthedev.mediamod.media.base.exception.HandlerInitializationException;
import dev.conorthedev.mediamod.media.spotify.api.SpotifyAPI;
import dev.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;
import dev.conorthedev.mediamod.util.*;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.client.FMLClientHandler;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * The main class for all Spotify-related things
 */
public class SpotifyHandler extends AbstractMediaHandler {

    public static final SpotifyHandler INSTANCE = new SpotifyHandler();
    public static SpotifyAPI spotifyApi = null;
    public static boolean logged = false;
    private static HttpServer server = null;
    private boolean hasListenedToSong = false;

    private static void handleRequest(String code) {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(() -> {
            if (logged) {
                INSTANCE.refreshSpotify();
            }
        }, 59, 59, TimeUnit.MINUTES);

        PlayerMessager.sendMessage("&7Exchanging authorization code for access token, this may take a moment...");
        try {
            TokenAPIResponse tokenAPIResponse = WebRequest.requestToMediaMod(WebRequestType.GET, "spotify/getToken?code=" + code, TokenAPIResponse.class);
            if (tokenAPIResponse == null) {
                MediaMod.INSTANCE.LOGGER.error("Error: tokenAPIResponse is null");
                PlayerMessager.sendMessage("&c&lERROR: &rFailed to login to Spotify!");
                return;
            }
            spotifyApi = new SpotifyAPI(tokenAPIResponse.accessToken, tokenAPIResponse.refreshToken);

            if (spotifyApi.getRefreshToken() != null) {
                logged = true;
                Settings.REFRESH_TOKEN = spotifyApi.getRefreshToken();
                Settings.saveConfig();
                PlayerMessager.sendMessage("&a&lSUCCESS! &rLogged into Spotify!");
                CurrentlyPlayingObject currentTrack = spotifyApi.getCurrentTrack();

                if (MediaMod.INSTANCE.DEVELOPMENT_ENVIRONMENT && currentTrack != null) {
                    PlayerMessager.sendMessage("&8&lDEBUG: &rCurrent Song: " + currentTrack.item.name + " by " + currentTrack.item.album.artists[0].name);
                }
            }
        } catch (Exception e) {
            logged = false;
            MediaMod.INSTANCE.LOGGER.error("Error: " + e.getMessage());
            PlayerMessager.sendMessage("&c&lERROR: &rFailed to login to Spotify!");
        }
    }

    public void connectSpotify() {
        attemptToOpenAuthURL();
    }

    private void attemptToOpenAuthURL() {
        try {
            if (server == null) {
                initializeHandler();
            }
        } catch (HandlerInitializationException e) {
            e.printStackTrace();
        }

        Desktop desktop = Desktop.getDesktop();
        String spotifyUrl = "https://accounts.spotify.com/authorize?client_id=aafe2607fde64888b9ef5b32bbc3d703&response_type=code&redirect_uri=http%3A%2F%2Flocalhost:9103%2Fcallback%2F&scope=user-read-playback-state%20user-read-currently-playing%20user-modify-playback-state&state=34fFs29kd09";
        try {
            desktop.browse(new URI(spotifyUrl));
        } catch (URISyntaxException e) {
            MediaMod.INSTANCE.LOGGER.fatal("Something has gone terribly wrong... SpotifyHandler:l59");
            e.printStackTrace();
        } catch (Exception e) {
            PlayerMessager.sendMessage("&cFailed to open browser with the Spotify Auth URL!");
            IChatComponent urlComponent = new ChatComponentText(ChatColor.translateAlternateColorCodes('&', "&lOpen URL"));
            urlComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, spotifyUrl));
            urlComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(ChatColor.translateAlternateColorCodes('&',
                    "&7Click this to open the Spotify Auth URL"))));
            PlayerMessager.sendMessage(urlComponent);
        }
    }

    private void refreshSpotify() {
        if (logged && SpotifyHandler.spotifyApi.getRefreshToken() != null) {
            if (FMLClientHandler.instance().getClient().thePlayer != null) {
                PlayerMessager.sendMessage("&8INFO: &9Attempting to refresh access token...");
            }

            try {
                RefreshResponse refreshResponse = WebRequest.requestToMediaMod(WebRequestType.GET, "spotify/refreshToken?token=" + spotifyApi.getAccessToken(), RefreshResponse.class);
                if (refreshResponse == null) {
                    MediaMod.INSTANCE.LOGGER.error("Error: tokenAPIResponse is null");
                    PlayerMessager.sendMessage("&8&lDEBUG: &rFailed to login to Spotify!");
                    return;
                }
                spotifyApi = new SpotifyAPI(refreshResponse.accessToken, spotifyApi.getRefreshToken());
                if (spotifyApi.getRefreshToken() != null) {
                    logged = true;
                    Settings.REFRESH_TOKEN = spotifyApi.getRefreshToken();
                    Settings.saveConfig();
                    PlayerMessager.sendMessage("&a&lSUCCESS! &rLogged into Spotify!");
                    CurrentlyPlayingObject currentTrack = spotifyApi.getCurrentTrack();

                    if (MediaMod.INSTANCE.DEVELOPMENT_ENVIRONMENT && currentTrack != null) {
                        PlayerMessager.sendMessage("&8&lDEBUG: &rCurrent Song: " + currentTrack.item.name + " by " + currentTrack.item.album.artists[0].name);
                    }
                }
            } catch (Exception e) {
                logged = false;
                MediaMod.INSTANCE.LOGGER.error("Error: " + e.getMessage());
                PlayerMessager.sendMessage("&8&lDEBUG: &rFailed to login to Spotify!");
            }
        }
    }

    @Override
    public String getHandlerName() {
        return "Spotify Handler";
    }

    @Override
    public CurrentlyPlayingObject getCurrentTrack() {
        try {
            CurrentlyPlayingObject object = spotifyApi.getCurrentTrack();
            lastProgressUpdate = System.currentTimeMillis();
            if (object != null) {
                lastProgressMs = object.progress_ms;
                paused = !object.is_playing;
                durationMs = object.item.duration_ms;
                hasListenedToSong = true;
            } else {
                durationMs = 0;
                lastProgressMs = 0;
                if (hasListenedToSong) paused = true;
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void initializeHandler() throws HandlerInitializationException {
        if (!Settings.REFRESH_TOKEN.isEmpty()) {
            logged = true;
            spotifyApi = new SpotifyAPI(null, Settings.REFRESH_TOKEN);
            refreshSpotify();
        }

        try {
            server = HttpServer.create(new InetSocketAddress(9103), 0);
        } catch (IOException e) {
            throw new HandlerInitializationException(e);
        }

        server.createContext("/callback", new SpotifyCallbackHandler());
        server.setExecutor(null);
        server.start();
    }

    @Override
    public boolean handlerReady() {
        return logged && !paused;
    }

    private static class SpotifyCallbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            Multithreading.runAsync(() -> handleRequest(t.getRequestURI().toString().replace("/callback/?code=", "").substring(0, t.getRequestURI().toString().replace("/callback/?code=", "").length() - 18)));

            String response = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "  <head>\n" +
                    "    <meta charset=\"utf-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "    <title>MediaMod</title>\n" +
                    "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.5/css/bulma.min.css\">\n" +
                    "    <script defer src=\"https://use.fontawesome.com/releases/v5.3.1/js/all.js\"></script>\n" +
                    "  </head>\n" +
                    "  <body class=\"hero is-dark is-fullheight\">\n" +
                    "  <section class=\"section has-text-centered\">\n" +
                    "    <div class=\"container\">\n" +
                    "      <img src=\"https://raw.githubusercontent.com/MediaModMC/MediaMod/master/src/main/resources/assets/mediamod/header.png\" width=\"400px\">" + "\n" +
                    "      <h1 class=\"title\">\n" +
                    "        Success!\n" +
                    "      </h1>\n" +
                    "      <p class=\"subtitle\">\n" +
                    "        Please close this window and go back into Minecraft!\n" +
                    "      </p>\n" +
                    "    </div>\n" +
                    "  </section>\n" +
                    "  </body>\n" +
                    "</html>";

            t.sendResponseHeaders(200, response.length());

            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private static class TokenAPIResponse {
        @SerializedName("access_token")
        final String accessToken;
        @SerializedName("expires_in")
        final int expiresIn;
        @SerializedName("refresh_token")
        final String refreshToken;

        TokenAPIResponse(String access_token, int expires_in, String refresh_token) {
            this.accessToken = access_token;
            this.expiresIn = expires_in;
            this.refreshToken = refresh_token;
        }
    }

    private static class RefreshResponse {
        @SerializedName("access_token")
        final String accessToken;
        @SerializedName("expires_in")
        final int expiresIn;

        RefreshResponse(String access_token, int expires_in) {
            this.accessToken = access_token;
            this.expiresIn = expires_in;
        }
    }
}
