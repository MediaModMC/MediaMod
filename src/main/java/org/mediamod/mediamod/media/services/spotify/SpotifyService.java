package org.mediamod.mediamod.media.services.spotify;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.SerializedName;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.mediamod.mediamod.MediaMod;
import org.mediamod.mediamod.api.APIHandler;
import org.mediamod.mediamod.config.Settings;
import org.mediamod.mediamod.media.core.IServiceHandler;
import org.mediamod.mediamod.media.core.api.MediaInfo;
import org.mediamod.mediamod.parties.PartyManager;
import org.mediamod.mediamod.parties.meta.PartyMediaInfo;
import org.mediamod.mediamod.util.*;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * The Spotify Service Handler
 */
public class SpotifyService implements IServiceHandler {
    /**
     * An instance of our Spotify API Wrapper
     */
    public static SpotifyAPI spotifyAPI;

    /**
     * The client id to use for authorisation
     * Retrieved from the MediaMod API
     */
    public volatile static String spotifyClientID;

    /**
     * The last timestamp that the progress was estimated
     */
    private int lastTimestamp = 0;

    /**
     * The last date that the progress was estimated
     */
    private long lastEstimationUpdate = 0;

    /**
     * A cached version of the current media info
     */
    private MediaInfo cachedMediaInfo = null;

    /**
     * A cached version of the party's media info
     */
    private PartyMediaInfo cachedPartyMediaInfo = null;

    /**
     * A pass through boolean for SpotifyAPI#isLoggedIn as that doesn't need to be public
     *
     * @see SpotifyAPI#isLoggedIn()
     */
    public static boolean isLoggedOut() {
        return spotifyAPI == null || !spotifyAPI.isLoggedIn();
    }

    /**
     * A pass through method for SpotifyAPI#logout as that doesn't need to be public
     *
     * @see SpotifyAPI#logout()
     */
    public static void logout() {
        spotifyAPI.logout();
    }

    /**
     * The name to be shown in MediaMod Menus
     */
    public String displayName() {
        return "Spotify";
    }

    /**
     * Initialises our API Wrapper if we have authenticated with the MediaMod API
     */
    public boolean load() {
        if (MediaMod.INSTANCE.authenticatedWithAPI) {
            spotifyAPI = new SpotifyAPI();

            Multithreading.runAsync(this::attemptToGetClientID);
            return true;
        }

        return false;
    }

    /**
     * Retrieves the Spotify Client ID from the MediaMod API
     */
    private void attemptToGetClientID() {
        try {
            JsonObject object = new JsonObject();
            object.addProperty("secret", APIHandler.instance.requestSecret);
            object.addProperty("uuid", Minecraft.getMinecraft().getSession().getProfile().getId().toString());

            ClientIDResponse clientIDResponse = WebRequest.requestToMediaMod(WebRequestType.POST, "api/spotify/clientid", object, ClientIDResponse.class);
            if (clientIDResponse != null) {
                spotifyClientID = clientIDResponse.clientID;
            }
        } catch (IOException e) {
            MediaMod.INSTANCE.logger.warn("Failed to get Spotify Client ID: ", e);
        }
    }

    /**
     * Retrieves the current track information from Spotify's Web API
     */
    @Nullable
    public MediaInfo getCurrentMediaInfo() {
        Multithreading.runAsync(() -> {
            PartyManager partyManager = PartyManager.instance;

            // Check if the user is participating in a party but is not the host
            if (partyManager.isInParty() && !partyManager.isPartyHost()) {
                PartyMediaInfo info = partyManager.getPartyMediaInfo();

                if (info != null) {
                    // If there is a track, check if the cached information is equal to the received information
                    if (cachedPartyMediaInfo == null || (cachedMediaInfo.track != null && cachedMediaInfo.track.identifier != null && !cachedMediaInfo.track.identifier.equals(info._id))) {
                        cachedPartyMediaInfo = info;

                        if (!spotifyAPI.playTrack(info._id, info.timestamp)) {
                            PlayerMessenger.sendMessage(ChatColor.RED + "Failed to play new track. Do you have Spotify Premium?", true);
                        }
                    }
                }
            }
        });

        MediaInfo info = spotifyAPI.getUserPlaybackInfo();
        cachedMediaInfo = info;

        if (info != null) {
            this.lastEstimationUpdate = System.currentTimeMillis();
            this.lastTimestamp = info.timestamp;
        }

        return info;
    }

    /**
     * Returns an estimation of the current progress of the track
     */
    public int getEstimatedProgress() {
        if (cachedMediaInfo == null) return 0;

        if (cachedMediaInfo.isPlaying) {
            int estimate = (int) (lastTimestamp + (System.currentTimeMillis() - lastEstimationUpdate));
            if (estimate > cachedMediaInfo.track.duration) {
                estimate = cachedMediaInfo.track.duration;
            }
            return estimate;
        } else {
            return lastTimestamp;
        }
    }

    /**
     * This indicates if the handler is ready for usage
     */
    public boolean isReady() {
        return spotifyAPI != null && spotifyAPI.isLoggedIn();
    }

    /**
     * The priority of the service, this indicates if the mod should use this service instead of another if they are both ready
     */
    public int getPriority() {
        return 0;
    }

    @Override
    public boolean supportsSkipping() {
        return true;
    }

    @Override
    public boolean supportsPausing() {
        return true;
    }

    @Override
    public boolean skipTrack() {
        return spotifyAPI.nextTrack();
    }

    @Override
    public boolean pausePlayTrack() {
        MediaInfo info = getCurrentMediaInfo();
        if (info == null) return false;

        return info.isPlaying ? spotifyAPI.pausePlayback() : spotifyAPI.resumePlayback();
    }

    /**
     * The response recieved when querying the MediaMod API for the Spotify Client ID
     */
    static class ClientIDResponse {
        String clientID;
    }
}

/**
 * A simple Spotify Web API wrapper
 */
class SpotifyAPI {
    private String accessToken = "";
    private String refreshToken;

    SpotifyAPI() {
        // Initialise callback server
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 9103), 0);
            server.createContext("/callback", new SpotifyCallbackHandler());
            server.start();
        } catch (IOException ignored) {
            MediaMod.INSTANCE.logger.warn("Failed to create Spotify callback server! Is the port already in use?");
        }

        // Refresh existing token
        refreshToken = Settings.REFRESH_TOKEN;

        if (!refreshToken.equals("")) {
            refresh();
        }
    }

    /**
     * Completes the authorisation flow by contacting the MediaMod API with an authorisation code which will be exchanged for an access and refresh code
     *
     * @param authCode: The code provided by the Spotify callback
     * @see "https://developer.spotify.com/documentation/general/guides/authorization-guide/"
     */
    public void login(String authCode) {
        MediaMod.INSTANCE.logger.info("Logging into Spotify...");
        PlayerMessenger.sendMessage(ChatColor.GRAY + "Logging into Spotify...", true);

        JsonObject body = new JsonObject();
        body.addProperty("code", authCode);
        body.addProperty("uuid", FMLClientHandler.instance().getClient().thePlayer.getUniqueID().toString());
        body.addProperty("secret", APIHandler.instance.requestSecret);

        try {
            SpotifyTokenResponse response = WebRequest.requestToMediaMod(WebRequestType.POST, "api/spotify/token", body, SpotifyTokenResponse.class);

            if (response == null) {
                MediaMod.INSTANCE.logger.error("An error occurred when exchanging authorisation code for a token: response was null");
                return;
            }

            accessToken = response.accessToken;
            refreshToken = response.refreshToken;

            Settings.REFRESH_TOKEN = refreshToken;
            Multithreading.runAsync(Settings::saveConfig);

            MediaMod.INSTANCE.logger.info("Logged in!");
            PlayerMessenger.sendMessage(ChatColor.GREEN + "Logged in!", true);
        } catch (IOException e) {
            MediaMod.INSTANCE.logger.error("An error occurred when exchanging authorisation code for a token: ", e);
        }
    }

    /**
     * Contacts the MediaMod API to exchange a refresh token for a new access token
     *
     * @see "https://developer.spotify.com/documentation/general/guides/authorization-guide/"
     */
    public void refresh() {
        MediaMod.INSTANCE.logger.info("Refreshing token...");
        PlayerMessenger.sendMessage(ChatColor.GRAY + "Refreshing token...", true);

        if (refreshToken == null) return;

        JsonObject body = new JsonObject();
        body.addProperty("refresh_token", refreshToken);
        body.addProperty("uuid", Minecraft.getMinecraft().getSession().getProfile().getId().toString());
        body.addProperty("secret", APIHandler.instance.requestSecret);

        try {
            SpotifyTokenResponse response = WebRequest.requestToMediaMod(WebRequestType.POST, "api/spotify/refresh", body, SpotifyTokenResponse.class);

            if (response == null) {
                MediaMod.INSTANCE.logger.error("An error occurred when exchanging refresh token for a new token: response was null");
                return;
            }

            accessToken = response.accessToken;
            refreshToken = response.refreshToken;

            Settings.REFRESH_TOKEN = refreshToken;
            Multithreading.runAsync(Settings::saveConfig);

            MediaMod.INSTANCE.logger.info("Refreshed token");
            PlayerMessenger.sendMessage(ChatColor.GREEN + "Refreshed token", true);
        } catch (IOException e) {
            MediaMod.INSTANCE.logger.error("An error occurred when exchanging refresh token for a new auth token: ", e);
        }
    }

    /**
     * Discards of the current tokens and saves the configuration file
     */
    public void logout() {
        accessToken = null;
        refreshToken = null;
        Settings.REFRESH_TOKEN = "";

        Multithreading.runAsync(Settings::saveConfig);
    }

    /**
     * Queries the Spotify API for the current playback information
     *
     * @return a MediaInfo instance
     * @see "https://developer.spotify.com/documentation/web-api/reference/player/get-information-about-the-users-current-playback/"
     */
    @Nullable
    public MediaInfo getUserPlaybackInfo() {
        MediaInfo info = null;
        try {
            info = WebRequest.makeRequest(WebRequestType.GET, new URL("https://api.spotify.com/v1/me/player/currently-playing"), MediaInfo.class, new HashMap<String, String>() {{
                put("Authorization", "Bearer " + accessToken);
            }});
        } catch (IOException e) {
            MediaMod.INSTANCE.logger.error("An error occurred when getting playback info: ", e);
        }

        return info;
    }

    /**
     * @return if the client is logged in or not
     */
    public boolean isLoggedIn() {
        return accessToken != null && !accessToken.equals("");
    }

    /**
     * Skips the playback to the next track
     *
     * @return if the operation was successful
     * @see "https://developer.spotify.com/documentation/web-api/reference/player/skip-users-playback-to-next-track/"
     */
    public boolean nextTrack() {
        try {
            int status = WebRequest.makeRequest(WebRequestType.POST, new URL("https://api.spotify.com/v1/me/player/next"), new HashMap<String, String>() {{
                put("Authorization", "Bearer " + accessToken);
            }});

            return status == 204;
        } catch (IOException ignored) {
            return false;
        }
    }

    /**
     * Resumes the playback
     *
     * @return if the operation was successful
     * @see "https://developer.spotify.com/documentation/web-api/reference/player/start-a-users-playback/"
     */
    public boolean resumePlayback() {
        try {
            int status = WebRequest.makeRequest(WebRequestType.PUT, new URL("https://api.spotify.com/v1/me/player/play"), new HashMap<String, String>() {{
                put("Authorization", "Bearer " + accessToken);
            }});

            return status == 204;
        } catch (IOException ignored) {
            return false;
        }
    }

    /**
     * Tell Spotify to play a track identifier at a certain timestamp
     *
     * @return if the operation was successful
     * @see "https://developer.spotify.com/documentation/web-api/reference/player/start-a-users-playback/"
     */
    public boolean playTrack(String trackID, int timestamp) {
        try {
            JsonArray array = new JsonArray();
            array.add(new JsonPrimitive("spotify:track:" + trackID));

            JsonObject body = new JsonObject();
            body.add("uris", array);
            body.addProperty("position_ms", timestamp);

            int status = WebRequest.makeRequest(WebRequestType.PUT, new URL("https://api.spotify.com/v1/me/player/play"), body, new HashMap<String, String>() {{
                put("Authorization", "Bearer " + accessToken);
            }});

            return status == 204;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Pauses the playback
     *
     * @return if the operation was successful
     * @see "https://developer.spotify.com/documentation/web-api/reference/player/skip-users-playback-to-next-track/"
     */
    public boolean pausePlayback() {
        try {
            int status = WebRequest.makeRequest(WebRequestType.PUT, new URL("https://api.spotify.com/v1/me/player/pause"), new HashMap<String, String>() {{
                put("Authorization", "Bearer " + accessToken);
            }});

            return status == 204;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

class SpotifyCallbackHandler implements HttpHandler {
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            Map<String, String> query = queryToMap(exchange.getRequestURI().getQuery());
            String code = query.get("code");

            String title = "Success";
            String message = "You can now close this window and go back into Minecraft!";

            if (!code.equals("")) {
                SpotifyService.spotifyAPI.login(code);
            } else {
                MediaMod.INSTANCE.logger.warn("Received null code from Spotify callback?");
                title = "Failure";
                message = "Please go back to Minecraft and attempt login again!";
            }

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
                    "        " + title + "\n" +
                    "      </h1>\n" +
                    "      <p class=\"subtitle\">\n" +
                    "        " + message + "\n" +
                    "      </p>\n" +
                    "    </div>\n" +
                    "  </section>\n" +
                    "  </body>\n" +
                    "</html>";

            exchange.sendResponseHeaders(200, response.length());

            OutputStream outputStream = exchange.getResponseBody();
            outputStream.write(response.getBytes());
            outputStream.close();
        }
    }

    /**
     * Parses a http query string into a java Map
     *
     * @param query: http query
     * @see SpotifyCallbackHandler#handle(HttpExchange)
     */
    Map<String, String> queryToMap(String query) {
        Map<String, String> result = new HashMap<>();

        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            result.put(entry[0], entry.length > 1 ? entry[1] : "");
        }

        return result;
    }
}

class SpotifyTokenResponse {
    @SerializedName("access_token")
    final String accessToken;
    @SerializedName("refresh_token")
    final String refreshToken;

    SpotifyTokenResponse(String access_token, String refresh_token) {
        this.accessToken = access_token;
        this.refreshToken = refresh_token;
    }
}
