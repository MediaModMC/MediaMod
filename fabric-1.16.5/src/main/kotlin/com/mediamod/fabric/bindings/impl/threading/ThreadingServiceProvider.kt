package com.mediamod.fabric.bindings.impl.threading

import com.mediamod.core.bindings.threading.ThreadingService
import net.minecraft.client.MinecraftClient
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ThreadingServiceProvider : ThreadingService{
    override val threadPool: ExecutorService = Executors.newCachedThreadPool()

    override fun runBlocking(task: () -> Unit) {
        MinecraftClient.getInstance().execute(task)
    }
}