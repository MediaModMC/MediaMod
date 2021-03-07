package com.mediamod.util

import net.minecraft.client.Minecraft
import java.util.concurrent.Executors

/**
 * @author Conor Byrne (dreamhopping)
 */
object MultithreadingUtils {
    private val threadPool = Executors.newCachedThreadPool()

    /**
     * Runs a task on a new thread using [threadPool]
     */
    fun runAsync(task: () -> Unit) {
        threadPool.submit(task)
    }

    /**
     * Runs a task on the main thread using [Minecraft.addScheduledTask]
     */
    fun runBlocking(task: () -> Unit) {
        Minecraft.getMinecraft().addScheduledTask(task)
    }
}
