package dev.cbyrne.mediamod.utils

import club.sk1er.mods.core.universal.ChatColor
import club.sk1er.mods.core.universal.ChatColor.COLOR_CHAR
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.util.ChatComponentText
import net.minecraft.util.IChatComponent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.concurrent.ConcurrentLinkedQueue

object PlayerMessager {
    val queuedMessages: ConcurrentLinkedQueue<String> = ConcurrentLinkedQueue()
    val messages: ConcurrentLinkedQueue<IChatComponent> = ConcurrentLinkedQueue()

    init {
        // todo tick schedule
    }

    private fun check() {
        if(!queuedMessages.isEmpty()) {
            val player: EntityPlayerSP = Minecraft.getMinecraft().thePlayer

            if(player != null && queuedMessages.peek() != null) {
                val poll: String = queuedMessages.poll()
                sendMessage(poll)
            }
        }
    }

    fun sendMessage(message: IChatComponent) {

    }

    fun sendMessage(message: String) {
        message.takeIf { it.isEmpty() }?.let { return }
        sendMessage(ChatColor.translateAlternateColorCodes('&', message), true)
    }

    fun sendMessage(message: String, header: Boolean) {
        message.takeIf { it.isEmpty() }?.let { return }
        if(header) {
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(
                ChatComponentText(
                    ChatColor.translateAlternateColorCodes('&',
                "&c[&fMediaMod&c]&r $message"
            ))
            )
        } else {
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(ChatComponentText(ChatColor.translateAlternateColorCodes('&', message)))
        }
    }

    @SubscribeEvent
    fun onTick (e: TickEvent.ClientTickEvent) {
        if(Minecraft.getMinecraft().thePlayer == null) {
            return;
        }

        while(!messages.isEmpty()) {
            Minecraft.getMinecraft().thePlayer.addChatComponentMessage(messages.poll())
        }

        while(!queuedMessages.isEmpty()) {
            sendMessage(queuedMessages.poll())
        }
    }
}