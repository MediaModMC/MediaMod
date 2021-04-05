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


package com.mediamod.core.render

import com.mediamod.core.bindings.render.RenderUtil
import java.awt.Color
import kotlin.math.min

/**
 * Renders a progress bar to the screen
 *
 * It includes progress estimation, making the progress bar appear smooth
 * Estimation is required as the progress is only updated every 3 seconds when MediaMod queries Media Information again
 *
 * @param duration The duration of the current track
 * @param x The x position of the progress bar
 * @param y The y position of the progress bar
 * @param width The width of the progress bar
 * @param height The height of the progress bar
 *
 * @author Conor Byrne (dreamhopping)
 */
class ProgressBarRenderer(
    private val x: Int,
    private val y: Int,
    private val width: Int,
    private val height: Int,
    private val backgroundColor: Color = Color.DARK_GRAY.brighter(),
    private val progressColor: Color = Color.GREEN,
    var duration: Long = 0
) {
    private var paused: Boolean = false
    private var lastProgress: Long = 0
    private var setProgressTime: Long = 0
    private var previousEstimatedProgress: Long = 0

    private fun calculateEstimatedProgress(): Long {
        if (paused) return previousEstimatedProgress

        val progress = min(lastProgress + (System.currentTimeMillis() - setProgressTime), duration)
        previousEstimatedProgress = progress
        return progress
    }

    fun setProgress(newProgress: Long) {
        paused = newProgress == lastProgress

        lastProgress = newProgress
        setProgressTime = System.currentTimeMillis()
    }

    fun render() {
        // Background rectangle
        RenderUtil.drawRectangle(x, y, width, height, backgroundColor)

        // Progress rectangle
        val progressPercent = (calculateEstimatedProgress().toFloat() / duration.toFloat())
        RenderUtil.drawRectangle(
            x.toFloat(),
            y.toFloat(),
            (progressPercent * width),
            height.toFloat(),
            progressColor
        )
    }
}
