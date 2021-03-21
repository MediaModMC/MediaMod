package com.mediamod.fabric.bindings.impl.schedule

import com.mediamod.core.bindings.schedule.TickSchedulerService
import com.mediamod.core.bindings.threading.ThreadingService
import net.minecraft.client.MinecraftClient
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class TickSchedulerServiceProvider : TickSchedulerService {
    override val tasks: MutableList<TickSchedulerService.TickTask> = mutableListOf()

    init {

    }
}