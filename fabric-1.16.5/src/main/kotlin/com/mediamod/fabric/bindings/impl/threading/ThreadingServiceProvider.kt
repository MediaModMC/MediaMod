/*
 *     MediaMod is a mod for Minecraft which displays information about your current track in-game
 *     Copyright (C) 2021 Conor Byrne
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.mediamod.fabric.bindings.impl.threading

import com.mediamod.core.bindings.threading.ThreadingService
import net.minecraft.client.MinecraftClient
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

class ThreadingServiceProvider : ThreadingService{
    override val threadPool: ExecutorService = Executors.newCachedThreadPool()
    override val scheduledThreadPool: ScheduledExecutorService = Executors.newScheduledThreadPool(1)

    override fun runBlocking(task: () -> Unit) {
        MinecraftClient.getInstance().execute(task)
    }
}