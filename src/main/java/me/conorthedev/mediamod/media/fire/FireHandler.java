package me.conorthedev.mediamod.media.fire;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import me.conorthedev.mediamod.MediaMod;
import me.conorthedev.mediamod.config.Settings;
import me.conorthedev.mediamod.media.base.AbstractMediaHandler;
import me.conorthedev.mediamod.media.base.exception.HandlerInitializationException;
import me.conorthedev.mediamod.media.fire.api.FireAPI;
import me.conorthedev.mediamod.media.spotify.api.playing.CurrentlyPlayingObject;
import me.conorthedev.mediamod.util.ChatColor;
import me.conorthedev.mediamod.util.Multithreading;
import me.conorthedev.mediamod.util.PlayerMessager;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.fml.client.FMLClientHandler;

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
 * The main class for all Fire-related things
 */
public class FireHandler extends AbstractMediaHandler {

    public static final FireHandler INSTANCE = new FireHandler();
    public static FireAPI fireApi = null;
    public static boolean logged = false;
    private boolean hasListenedToSong = false;
    private static HttpServer server = null;

    private static void handleRequest(String s) {
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(() -> {
            if (logged) {
                INSTANCE.refreshFire();
            }
        }, 59, 59, TimeUnit.MINUTES);

        PlayerMessager.sendMessage("&7Exchanging authorization code for Discord ID, this may take a moment...");
        try {
            // Create a connection
            //BaseMod.ENDPOINT
            URL url = new URL("https://api.gaminggeek.dev/mediamod/current/" + s);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // Set the request method
            con.setRequestMethod("GET");
            // Set the user agent
            con.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/79.0.3945.88 Safari/537.36");
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
            DiscordProfileResponse discordProfileResponse = g.fromJson(content, DiscordProfileResponse.class);

            // Put into the Fire API
            fireApi = new FireAPI(discordProfileResponse.DISCORD_ID);

            if (fireApi.getDiscordId() != null) {
                logged = true;
                Settings.DISCORD_ID = fireApi.getDiscordId();
                Settings.saveConfig();
                // Tell the user that they were logged in
                PlayerMessager.sendMessage("&a&lSUCCESS! &rLogged into Fire Music!");
                CurrentlyPlayingObject currentTrack = fireApi.getCurrentTrack();

                if (MediaMod.INSTANCE.DEVELOPMENT_ENVIRONMENT && currentTrack != null) {
                    PlayerMessager.sendMessage("&8&lDEBUG: &rCurrent Song: " + currentTrack.item.name + " by " + currentTrack.item.album.artists[0].name);
                }
            }
        } catch (Exception e) {
            MediaMod.INSTANCE.LOGGER.error("Error: ", e);
        }
    }

    public void connectFire() {
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
        String URL = "https://api.gaminggeek.dev/mediamod/auth";
        try {
            desktop.browse(new URI(URL));
        } catch (URISyntaxException e) {
            MediaMod.INSTANCE.LOGGER.fatal("Something has gone terribly wrong... FireHandler:L118");
            e.printStackTrace();
        } catch (Exception e) {
            PlayerMessager.sendMessage("&cFailed to open browser with the Fire Auth URL!");
            IChatComponent urlComponent = new ChatComponentText(ChatColor.translateAlternateColorCodes('&', "&lOpen URL"));
            urlComponent.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://auth.gaminggeek.dev/mediamod/auth"));
            urlComponent.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ChatComponentText(ChatColor.translateAlternateColorCodes('&',
                    "&7Click this to open the Fire Auth URL"))));
            PlayerMessager.sendMessage(urlComponent);
        }
    }

    private void refreshFire() {
        Minecraft mc = FMLClientHandler.instance().getClient();

        if (logged && FireHandler.fireApi.getDiscordId() != null) {
            if (FMLClientHandler.instance().getClient().thePlayer != null) {
                PlayerMessager.sendMessage("&8INFO: &9Attempting to refresh the Discord ID");
            }

            try {
                URL url = new URL("https://api.gaminggeek.dev/mediamod/current/" + fireApi.getDiscordId());
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

                DiscordProfileResponse discordProfileResponse = new Gson().fromJson(content, DiscordProfileResponse.class);
                // Put into the Fire API
                fireApi = new FireAPI("366118780293611520");

                if (fireApi.getDiscordId() != null) {
                    logged = true;
                    // Tell the user that they were logged in
                    if (mc.thePlayer != null) {
                        PlayerMessager.sendMessage("&a&lSUCCESS! &rLogged into Fire!");
                        CurrentlyPlayingObject currentTrack = fireApi.getCurrentTrack();

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
        return "Fire Handler";
    }

    @Override
    public CurrentlyPlayingObject getCurrentTrack() {
        try {
            CurrentlyPlayingObject object = fireApi.getCurrentTrack();
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
        // If the discord id is stored, try to refresh
        if (!Settings.DISCORD_ID.isEmpty()) {
            logged = true;
            fireApi = new FireAPI(Settings.DISCORD_ID);
            refreshFire();
        }
        // Create a HTTP Server for the Fire API to call back to (http://localhost:9104)
        try {
            server = HttpServer.create(new InetSocketAddress(9104), 0);
        } catch (IOException e) {
            throw new HandlerInitializationException(e);
        }

        server.createContext("/callback", new FireCallbackHandler());
        server.setExecutor(null);

        // Start the server
        server.start();
    }

    @Override
    public boolean handlerReady() {
        return logged && !paused;
    }

    private static class FireCallbackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            // Handle the req
            Multithreading.runAsync(() -> handleRequest(t.getRequestURI().toString().replace("/callback?code=", "")));
            String response = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "  <head>\n" +
                    "    <meta charset=\"utf-8\">\n" +
                    "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "    <title>MediaMod x Fire</title>\n" +
                    "    <link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/bulma/0.7.5/css/bulma.min.css\">\n" +
                    "    <script defer src=\"https://use.fontawesome.com/releases/v5.3.1/js/all.js\"></script>\n" +
                    "  </head>\n" +
                    "  <body class=\"hero is-dark is-fullheight\">\n" +
                    "  <section class=\"section has-text-centered\">\n" +
                    "    <div class=\"container\">\n" +
                    "      <img src=\"https://cdn.discordapp.com/avatars/444871677176709141/4018e826ad14e2564ab771ad0d89d2d5.png\" width=\"100px\">" + "\n" +
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

    private static class DiscordProfileResponse {
        final String DISCORD_ID;

        DiscordProfileResponse(String discordId) {
            this.DISCORD_ID = discordId;
        }
    }
}
