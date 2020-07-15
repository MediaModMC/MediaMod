package dev.conorthedev.mediamod.media.spotify.api;

import dev.conorthedev.mediamod.MediaMod;
import dev.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;
import dev.conorthedev.mediamod.media.spotify.api.track.Track;
import dev.conorthedev.mediamod.util.*;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * The MediaMod Implementation of the Spotify Web API in Java
 *
 * @author ConorTheDev
 */
public class SpotifyAPI {
    /**
     * The user's access token
     */
    private final String ACCESS_TOKEN;

    /**
     * The user's refresh token
     */
    private final String REFRESH_TOKEN;

    /**
     * Basic constructor for the SpotifyAPI
     *
     * @param accessToken  the value to set ACCESS_TOKEN to
     * @param refreshToken the value to set REFRESH_TOKEN
     */
    public SpotifyAPI(String accessToken, String refreshToken) {
        this.ACCESS_TOKEN = accessToken;
        this.REFRESH_TOKEN = refreshToken;
    }

    /**
     * Get the current track that is playing
     *
     * @return CurrentlyPlayingObject
     */
    public CurrentlyPlayingObject getCurrentTrack() throws IOException {
        // TODO: Remove IOException throws from getCurrentTrack()
        try {
            return WebRequest.makeRequest(WebRequestType.GET, new URL("https://api.spotify.com/v1/me/player/currently-playing"), CurrentlyPlayingObject.class, new HashMap<String, String>() {{
                put("Authorization", "Bearer " + ACCESS_TOKEN);
            }});
        } catch (IOException e) {
            MediaMod.INSTANCE.LOGGER.error("Failed to get current track! " + e.getMessage());
            PlayerMessager.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Failed to get current track!\n" + ChatColor.RED + "An error occurred: " + e.getMessage(), true);
        }

        return null;
    }

    public void setSongFromID(String trackID) {
        if(trackID == null) return;

        try {
            Track object = WebRequest.makeRequest(WebRequestType.GET, new URL("https://api.spotify.com/v1/tracks/" + trackID), Track.class, new HashMap<String, String>() {{
                put("Authorization", "Bearer " + ACCESS_TOKEN);
            }});

            if (object != null) {
                WebRequest.makeRequest(WebRequestType.POST, new URL("https://api.spotify.com/v1/player/queue?uri=" + object.uri), new HashMap<String, String>() {{
                    put("Authorization", "Bearer " + ACCESS_TOKEN);
                }});
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean skipTrack() {
        AtomicBoolean success = new AtomicBoolean(true);

        Multithreading.runAsync(() -> {
            try {
                WebRequest.makeRequest(WebRequestType.POST, new URL("https://api.spotify.com/v1/me/player/next"), null, new HashMap<String, String>() {{
                    put("Accept", "application/json");
                    put("Content-Length", "0");
                    put("Content-Type", "application/json");
                    put("Authorization", "Bearer " + ACCESS_TOKEN);
                }});
            } catch (IOException e) {
                PlayerMessager.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Failed to pause/resume track!\n" + ChatColor.RED + "An error occurred: " + e.getMessage(), true);
            }
        });

        return success.get();
    }

    public boolean pausePlayTrack() {
        AtomicBoolean success = new AtomicBoolean(true);

        Multithreading.runAsync(() -> {
            try {
                if (getCurrentTrack().is_playing) {
                    WebRequest.makeRequest(WebRequestType.PUT, new URL("https://api.spotify.com/v1/me/player/pause"), null, new HashMap<String, String>() {{
                        put("Authorization", "Bearer " + ACCESS_TOKEN);
                        put("Content-Length", "0");
                    }});
                } else {
                    WebRequest.makeRequest(WebRequestType.PUT, new URL("https://api.spotify.com/v1/me/player/play"), null, new HashMap<String, String>() {{
                        put("Authorization", "Bearer " + ACCESS_TOKEN);
                        put("Content-Length", "0");
                    }});
                }
            } catch (IOException e) {
                PlayerMessager.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Failed to pause/resume track!\n" + ChatColor.RED + "An error occurred: " + e.getMessage(), true);
                success.set(false);
            }
        });

        return success.get();
    }

    /**
     * Get the user's access token
     *
     * @return the defined access token
     */
    public String getAccessToken() {
        return ACCESS_TOKEN;
    }

    /**
     * Get the user's refresh token
     *
     * @return the defined refresh token
     */
    public String getRefreshToken() {
        return REFRESH_TOKEN;
    }
}
