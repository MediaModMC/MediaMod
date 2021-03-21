package com.mediamod.fabric.event

import net.fabricmc.fabric.api.event.Event
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.ActionResult

fun interface RenderTickEvent {
    companion object {
        val event: Event<RenderTickEvent> = EventFactory.createArrayBacked(RenderTickEvent::class.java) { listeners ->
            RenderTickEvent { partialTicks ->
                listeners.forEach {
                    it.onTick(partialTicks)
                }

                ActionResult.PASS
            }
        }
    }

    fun onTick(partialTicks: Float)
}
