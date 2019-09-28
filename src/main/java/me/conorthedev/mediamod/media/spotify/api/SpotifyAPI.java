package me.conorthedev.mediamod.media.spotify.api;

import com.google.gson.Gson;
import me.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

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
        ACCESS_TOKEN = accessToken;
        REFRESH_TOKEN = refreshToken;
    }

    /**
     * Get the current track that is playing
     *
     * @return CurrentlyPlayingObject
     */
    public CurrentlyPlayingObject getCurrentTrack() throws Exception {
        String ENDPOINT = "https://api.spotify.com/v1/me/player/currently-playing";

        URL url = new URL(ENDPOINT);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Authorization", "Bearer " + ACCESS_TOKEN);
        con.connect();

        // Read the output
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String content = in.lines().collect(Collectors.joining());

        // Close the input reader & the connection
        in.close();
        con.disconnect();

        // Parse JSON
        Gson g = new Gson();

        return g.fromJson(content, CurrentlyPlayingObject.class);
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