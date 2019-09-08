package me.conorthedev.mediamod.base;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.conorthedev.mediamod.media.spotify.SpotifyHandler;
import me.conorthedev.mediamod.util.Metadata;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.ForgeVersion;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * A base mod used in all of ConorTheDev's Mods
 *
 * @author ConorTheDev
 */
public class BaseMod {
    public static final String ENDPOINT = "http://localhost:8080";

    /**
     * Registers with api.conorthedev.me
     */
    public static boolean init() {
        try {
            // Create a conncetion
            URL url = new URL(ENDPOINT + "/api/register/" + Minecraft.getMinecraft().getSession().getProfile().getId().toString() + "/" + Metadata.MODID + "/" + Metadata.VERSION);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // Set the request method
            con.setRequestMethod("POST");
            // Connect to the API
            con.connect();

            // Read the output
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String content = in.lines().collect(Collectors.joining());

            // Close the input reader & the conncetion
            in.close();
            con.disconnect();

            // Parse JSON
            Gson g = new Gson();
            RegisterReponse registerReponse = g.fromJson(content, RegisterReponse.class);

            return registerReponse.uuid != null;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private static class RegisterReponse {
        String uuid;
        ModResponse[] mods;

        RegisterReponse(String uuid, ModResponse[] mods) {
            this.uuid = uuid;
            this.mods = mods;
        }
    }

    private static class ModResponse {
        String id;
        String version;

        ModResponse(String id, String version) {
            this.id = id;
            this.version = version;
        }
    }
}
