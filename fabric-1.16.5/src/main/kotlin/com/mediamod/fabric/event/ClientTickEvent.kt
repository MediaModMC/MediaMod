package com.mediamod.fabric.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult

fun interface ClientTickEvent {
    companion object {
        val event: Event<ClientTickEvent> = EventFactory.createArrayBacked(ClientTickEvent::class.java) { listeners ->
            ClientTickEvent {
                listeners.forEach(ClientTickEvent::onTick)
                ActionResult.PASS
            }
        }
    }

    fun onTick()
}
