package com.mediamod.ui.render

import com.mediamod.ui.RenderUtils
import java.awt.Color
import kotlin.math.max
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
    private var lastProgress: Long = 0
    private var setProgressTime: Long = 0

    private fun calculateEstimatedProgress() =
        min(lastProgress + (System.currentTimeMillis() - setProgressTime), duration)

    fun setProgress(newProgress: Long) {
        if (newProgress == lastProgress) return

        lastProgress = newProgress
        setProgressTime = System.currentTimeMillis()
    }

    fun render() {
        // Background rectangle
        RenderUtils.drawRectangle(x, y, width, height, backgroundColor)

        // Progress rectangle
        val progressPercent = (calculateEstimatedProgress().toFloat() / duration.toFloat())
        RenderUtils.drawRectangle(x.toFloat(), y.toFloat(), (progressPercent * width), height.toFloat(), progressColor)
    }
}
