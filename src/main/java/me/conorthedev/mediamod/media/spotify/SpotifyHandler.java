package me.conorthedev.mediamod.media.spotify;

import cc.hyperium.Hyperium;
import cc.hyperium.mods.sk1ercommon.Multithreading;
import cc.hyperium.utils.ChatColor;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import me.conorthedev.mediamod.MediaMod;
import me.conorthedev.mediamod.base.BaseMod;
import me.conorthedev.mediamod.config.Settings;
import me.conorthedev.mediamod.media.base.AbstractMediaHandler;
import me.conorthedev.mediamod.media.base.exception.HandlerInitializationException;
import me.conorthedev.mediamod.media.spotify.api.SpotifyAPI;
import me.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;
import me.conorthedev.mediamod.util.PlayerMessager;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * The main class for all Spotify-related things
 */
public class SpotifyHandler extends AbstractMediaHandler {

    public static final SpotifyHandler INSTANCE = new SpotifyHandler();
    public static SpotifyAPI spotifyApi = null;
    public static boolean logged = false;
    private boolean hasListenedToSong;
    private static HttpServer server = null;

    private static void handleRequest(String code) {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(() -> {
            if (logged) {
                INSTANCE.refreshSpotify();
            }
        }, 59, 59, TimeUnit.MINUTES);

        PlayerMessager.sendMessage("&7Exchanging authorization code for access token, this may take a moment...");
        try {
            // Create a connection
            //BaseMod.ENDPOINT
            URL url = new URL(BaseMod.ENDPOINT + "/api/mediamod/spotify/token/" + code);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // Set the request method
            con.setRequestMethod("GET");
            // Set the user agent
            con.setRequestProperty("user-agent", "MediaMod/1.0");
            // Connect to the API
            con.connect();

            // Read the output
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String content = in.lines().collect(Collectors.joining());

            // Close the input reader & the connection
            in.close();
            con.disconnect();

            // Parse JSON
            Gson g = new Gson();
            TokenAPIResponse tokenAPIResponse = g.fromJson(content, TokenAPIResponse.class);

            // Put into the Spotify API
            spotifyApi = new SpotifyAPI(tokenAPIResponse.accessToken, tokenAPIResponse.refreshToken);

            if (spotifyApi.getRefreshToken() != null) {
                logged = true;
                Settings.REFRESH_TOKEN = spotifyApi.getRefreshToken();
                Hyperium.CONFIG.save();
                // Tell the user that they were logged in
                PlayerMessager.sendMessage("&a&lSUCCESS! &rLogged into Spotify!");
                CurrentlyPlayingObject currentTrack = spotifyApi.getCurrentTrack();

                if (MediaMod.INSTANCE.DEVELOPMENT_ENVIRONMENT && currentTrack != null) {
                    PlayerMessager.sendMessage("&8&lDEBUG: &rCurrent Song: " + currentTrack.item.name + " by " + currentTrack.item.album.artists[0].name);
                }
            }
        } catch (Exception e) {
            MediaMod.INSTANCE.LOGGER.error("Error: ", e);
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
        String URL = "https://accounts.spotify.com/authorize?client_id=4d33df7152bb4e2dac57167eeaafdf45&response_type=code&redirect_uri=http%3A%2F%2Flocalhost:9103%2Fcallback%2F&scope=user-read-playback-state%20user-read-currently-playing%20user-modify-playback-state&state=34fFs29kd09";
        try {
            desktop.browse(new URI(URL));
        } catch (URISyntaxException e) {
            MediaMod.INSTANCE.LOGGER.fatal("Something has gone terribly wrong... SpotifyHandler:l59");
            e.printStackTrace();
        } catch (Exception e) {
            PlayerMessager.sendMessage("&cFailed to open browser with the Spotify Auth URL!");
            IChatComponent urlComponent = new ChatComponentText(ChatColor.translateAlternateColorCodes('&', "&lOpen URL"));
            urlComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://accounts.spotify.com/authorize?client_id=4d33df7152bb4e2dac57167eeaafdf45&response_type=code&redirect_uri=http%3A%2F%2Flocalhost:9103%2Fcallback%2F&scope=user-read-playback-state%20user-read-currently-playing%20user-modify-playback-state&state=34fFs29kd09"));
            urlComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(ChatColor.translateAlternateColorCodes('&',
                    "&7Click this to open the Spotify Auth URL"))));
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(urlComponent);
        }
    }

    public void refreshSpotify() {
        Minecraft mc = Minecraft.getMinecraft();

        if (logged && SpotifyHandler.spotifyApi.getRefreshToken() != null) {
            if (mc.thePlayer != null) {
                PlayerMessager.sendMessage("&8INFO: &9Attempting to refresh access token...");
            }

            try {
                URL url = new URL(BaseMod.ENDPOINT + "/api/mediamod/spotify/refresh/" + spotifyApi.getRefreshToken());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                // Set the request method
                con.setRequestMethod("GET");
                // Set the user agent
                con.setRequestProperty("user-agent", "MediaMod/1.0");
                // Connect to the API
                con.connect();

                // Read the output
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String content = in.lines().collect(Collectors.joining());

                // Close the input reader & the connection
                in.close();
                con.disconnect();

                // Parse JSON
                Gson g = new Gson();
                RefreshResponse refreshResponse = g.fromJson(content, RefreshResponse.class);

                // Put into the Spotify API
                spotifyApi = new SpotifyAPI(refreshResponse.accessToken, spotifyApi.getRefreshToken());

                if (spotifyApi.getRefreshToken() != null) {
                    logged = true;
                    // Tell the user that they were logged in
                    if (mc.thePlayer != null) {
                        PlayerMessager.sendMessage("&a&lSUCCESS! &rLogged into Spotify!");
                        CurrentlyPlayingObject currentTrack = spotifyApi.getCurrentTrack();

                        if (MediaMod.INSTANCE.DEVELOPMENT_ENVIRONMENT && currentTrack != null) {
                            PlayerMessager.sendMessage("&8DEBUG: &rCurrent Song: " + currentTrack.item.name + " by " + currentTrack.item.album.artists[0].name);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
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
        // If the refresh token is stored, try to refresh
        if (!Settings.REFRESH_TOKEN.isEmpty()) {
            logged = true;
            spotifyApi = new SpotifyAPI(null, Settings.REFRESH_TOKEN);
            refreshSpotify();
        }
        // Create a HTTP Server for the Spotify API to call back to (http://localhost:9103)
        try {
            server = HttpServer.create(new InetSocketAddress(9103), 0);
        } catch (IOException e) {
            throw new HandlerInitializationException(e);
        }

        server.createContext("/callback", new SpotifyCallbackHandler());
        server.setExecutor(null);

        // Start the server
        server.start();
    }

    @Override
    public boolean handlerReady() {
        return logged && !paused;
    }

    private static class SpotifyCallbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // Handle the req
            Multithreading.runAsync(() -> handleRequest(t.getRequestURI().toString().replace("/callback/?code=",
                    "").substring(0, t.getRequestURI().toString().replace("/callback/?code=", "").length() - 18)));

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

            server.stop(0);
        }
    }

    private static class TokenAPIResponse {
        final String accessToken;
        final int expiresIn;
        final String refreshToken;

        TokenAPIResponse(String access_token, int expires_in, String refresh_token) {
            this.accessToken = access_token;
            this.expiresIn = expires_in;
            this.refreshToken = refresh_token;
        }
    }

    private static class RefreshResponse {
        final String accessToken;
        final int expiresIn;

        RefreshResponse(String access_token, int expires_in) {
            this.accessToken = access_token;
            this.expiresIn = expires_in;
        }
    }
}