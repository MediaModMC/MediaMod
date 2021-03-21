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

package com.mediamod.core.bindings.threading

import com.mediamod.core.bindings.BindingRegistry
import java.util.concurrent.ExecutorService

interface ThreadingService {
    val threadPool: ExecutorService

    fun runAsync(task: () -> Unit) {
        threadPool.submit(task)
    }

    fun runBlocking(task: () -> Unit)

    companion object : ThreadingService by BindingRegistry.threadingService
}