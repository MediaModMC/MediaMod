package me.conorthedev.mediamod.media.fire.api;

import com.google.gson.Gson;
import me.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

/**
 * The MediaMod Implementation of the Fire's Music API in Java.
 *
 * @author chachy
 */
public class FireAPI {
    /**
     * The user's Discord ID.
     */
    private final String DISCORD_ID;

    /**
     * Basic constructor for the FireAPI
     *
     * @param discordId the value to set DISCORD_ID to
     */
    public FireAPI(String discordId) {
        this.DISCORD_ID = discordId;
    }

    /**
     * Get the current track that is playing
     *
     * @return CurrentlyPlayingObject
     */
    public CurrentlyPlayingObject getCurrentTrack() throws Exception {
        String ENDPOINT = "https://api.gaminggeek.dev/mediamod/current/" + DISCORD_ID;

        URL url = new URL(ENDPOINT);

        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.addRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
        con.setRequestMethod("GET");
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
     * Get the user's discord id
     *
     * @return the defined discord id.
     */
    public String getDiscordId() {
        return DISCORD_ID;
    }
}
