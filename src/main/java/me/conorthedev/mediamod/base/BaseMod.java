package me.conorthedev.mediamod.base;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.conorthedev.mediamod.util.Metadata;
import net.minecraft.util.CryptManager;
import net.minecraftforge.fml.client.FMLClientHandler;

import javax.crypto.SecretKey;
import javax.naming.AuthenticationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.util.Base64;
import java.util.stream.Collectors;

/**
 * A base mod used in all of ConorTheDev's Mods
 *
 * @author ConorTheDev
 */
public class BaseMod {
    public static final String ENDPOINT = "https://api.conorthedev.me";

    /**
     * Registers with api.conorthedev.me
     */
    public static boolean init() {

        try {
            // Execute Mojang authentication, similar to when you join a server
            URL authDataUrl = new URL(ENDPOINT + "/mediamod/analytics/authdata"); // get authentication data to send to Mojang's authentication servers

            HttpURLConnection authDataCon = (HttpURLConnection) authDataUrl.openConnection();
            authDataCon.setRequestMethod("GET");
            authDataCon.setRequestProperty("User-Agent", "MediaMod/1.0");
            authDataCon.connect();

            BufferedReader authDataReader = new BufferedReader(new InputStreamReader(authDataCon.getInputStream()));
            String authDataContent = authDataReader.lines().collect(Collectors.joining());

            authDataReader.close();
            authDataCon.disconnect();

            JsonObject object = new JsonParser().parse(authDataContent).getAsJsonObject();

            SecretKey secretKey = CryptManager.createNewSharedKey();
            String serverId = object.get("server_id").getAsString();
            PublicKey publicKey = CryptManager.decodePublicKey(Base64.getDecoder().decode(object.get("public_key").getAsString()));
            String serverIdHash = new BigInteger(CryptManager.getServerIdHash(serverId, publicKey, secretKey)).toString(16);

            HttpURLConnection authCon = (HttpURLConnection) new URL("https://sessionserver.mojang.com/session/minecraft/join").openConnection();
            authCon.setRequestMethod("POST");
            authCon.setRequestProperty("User-Agent", "MediaMod/1.0");
            authCon.setRequestProperty("Content-Type", "application/json");
            authCon.setDoOutput(true);
            JsonObject data = new JsonObject();
            data.addProperty("accessToken", FMLClientHandler.instance().getClient().getSession().getToken());
            data.addProperty("selectedProfile", FMLClientHandler.instance().getClient().getSession().getPlayerID());
            data.addProperty("serverId", serverIdHash);
            authCon.connect();

            try (OutputStream os = authCon.getOutputStream()) {
                os.write(data.toString().getBytes(StandardCharsets.UTF_8));
            }

            BufferedReader authReader = new BufferedReader(new InputStreamReader(authCon.getInputStream()));
            String authContent = authReader.lines().collect(Collectors.joining());
            authReader.close();
            authCon.disconnect();
            if (authCon.getResponseCode() != 204) {
                throw new AuthenticationException("Failed to authenticate with Mojang: " + authContent);
            }

            // Create a conncetion
            URL url = new URL(ENDPOINT + "/mediamod/analytics/register/" + FMLClientHandler.instance().getClient().getSession().getUsername() + "/" + Metadata.MODID + "/" + Metadata.VERSION);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // Set the request method
            con.setRequestMethod("POST");
            // Set the user agent
            con.setRequestProperty("content-type", "application/json");
            con.setRequestProperty("user-agent", "MediaMod/1.0");
            // Connect to the API
            con.setDoOutput(true);
            con.connect();

            JsonObject newData = new JsonObject();
            newData.addProperty("shared_secret", Base64.getEncoder().encodeToString(CryptManager.encryptData(publicKey, secretKey.getEncoded())));

            try (OutputStream os = con.getOutputStream()) {
                os.write(newData.toString().getBytes(StandardCharsets.UTF_8));
            }

            // Read the output
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String content = in.lines().collect(Collectors.joining());

            // Close the input reader & the conncetion
            in.close();
            con.disconnect();

            // Parse JSON
            JsonObject response = new JsonParser().parse(content).getAsJsonObject();
            if (!(response.has("success") && response.getAsJsonPrimitive("success").getAsBoolean())) {
                throw new AuthenticationException("Failed to register with MediaMod: " + response.getAsJsonPrimitive("error").getAsString());
            }
            return true;
        } catch (IOException | AuthenticationException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
