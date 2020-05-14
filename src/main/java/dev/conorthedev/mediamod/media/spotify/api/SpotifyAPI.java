package dev.conorthedev.mediamod.media.spotify.api;

import dev.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;
import dev.conorthedev.mediamod.util.WebRequest;
import dev.conorthedev.mediamod.util.WebRequestType;

import java.net.URL;
import java.util.HashMap;

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
    public CurrentlyPlayingObject getCurrentTrack() throws Exception {
        return WebRequest.makeRequest(WebRequestType.GET, new URL("https://api.spotify.com/v1/me/player/currently-playing"), CurrentlyPlayingObject.class, new HashMap<String, String>() {{
            put("Authorization", "Bearer " + ACCESS_TOKEN);
        }});
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
