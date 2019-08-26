package me.conorthedev.mediamod.base;

import com.google.gson.JsonObject;
import me.conorthedev.mediamod.MediaMod;
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

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A base mod used in all of ConorTheDev's Mods
 *
 * @author ConorTheDev
 */
public class BaseMod {
    /**
     * An instance of the mod class
     *
     * @see me.conorthedev.mediamod.MediaMod
     */
    public static final MediaMod MOD = MediaMod.INSTANCE;

    /**
     * Connects to analytics server
     */
    @SuppressWarnings("unchecked")
    public static boolean init() {
        try {
            AtomicBoolean loopActive = new AtomicBoolean(true);
            AtomicBoolean successful = new AtomicBoolean(false);
            JsonObject data = new JsonObject();
            data.addProperty("mod_id", Metadata.MODID);
            data.addProperty("mod_version", Metadata.VERSION);
            data.addProperty("mc_version", ForgeVersion.mcVersion);
            NetworkManager manager = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName("auth.guildcapes.club"), 45918, true);
            manager.sendPacket(new C00Handshake(89023, data.toString(), 45918, EnumConnectionState.LOGIN), (future) -> {
                manager.setConnectionState(EnumConnectionState.LOGIN);
                manager.setNetHandler(new NetHandlerLoginClient(manager, Minecraft.getMinecraft(), null) {
                    @Override
                    public void onDisconnect(IChatComponent reason) {
                        if (!loopActive.get()) return;
                        if (reason.getUnformattedText().equals("done")) {
                            loopActive.set(false);
                            successful.set(true);
                        } else {
                            System.err.println("Couldn't register with analytics: " + reason.getUnformattedText());
                            loopActive.set(false);
                        }
                    }

                    @Override
                    public void handleDisconnect(S00PacketDisconnect packetIn) {
                        if (packetIn.func_149603_c().getUnformattedText().equals("done")) {
                            loopActive.set(false);
                            successful.set(true);
                        } else {
                            System.err.println("Couldn't register with analytics: " + packetIn.func_149603_c().getUnformattedText());
                            loopActive.set(false);
                        }
                        manager.closeChannel(packetIn.func_149603_c());
                    }
                });
                manager.sendPacket(new C00PacketLoginStart(Minecraft.getMinecraft().getSession().getProfile()));
            });
            while (loopActive.get()) {
                manager.processReceivedPackets();
                manager.checkDisconnected();
            }

            return successful.get();
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
