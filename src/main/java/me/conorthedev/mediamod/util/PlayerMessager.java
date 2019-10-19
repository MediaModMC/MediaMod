package me.conorthedev.mediamod.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.ConcurrentLinkedQueue;

public class PlayerMessager {

    public static final PlayerMessager INSTANCE = new PlayerMessager();
    private static final ConcurrentLinkedQueue<String> queuedMessages = new ConcurrentLinkedQueue<>();
    private static final ConcurrentLinkedQueue<ITextComponent> messages = new ConcurrentLinkedQueue<>();

    private PlayerMessager() {
        TickScheduler.INSTANCE.schedule(0, this::check);
    }

    public void queue(String chat) {
        queuedMessages.add(chat);
    }

    public static void sendMessage(ITextComponent message) {
        if (message == null) message = new TextComponentString("");
        messages.add(message);
    }

    public static void sendMessage(String message, boolean header) {
        if (message == null) return;
        if (header) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(ChatColor.translateAlternateColorCodes('&',
                    "&c[&fMediaMod&c]&r " + message)));
        } else {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(ChatColor.translateAlternateColorCodes('&', message)));
        }
    }

    public static void sendMessage(String message) {
        if (message == null) return;
        sendMessage(ChatColor.translateAlternateColorCodes('&', message), true);
    }

    private void check() {
        if (!queuedMessages.isEmpty()) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player != null && queuedMessages.peek() != null) {
                String poll = queuedMessages.poll();
                sendMessage(poll);
            }
        }
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (Minecraft.getMinecraft().player == null) {
            return;
        }

        while (!messages.isEmpty()) {
            Minecraft.getMinecraft().player.sendMessage(messages.poll());
        }

        while (!queuedMessages.isEmpty()) {
            sendMessage(queuedMessages.poll());
        }
    }
}
