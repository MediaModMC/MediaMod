package dev.mediamod.ui

import dev.mediamod.MediaMod
import dev.mediamod.theme.Theme
import dev.mediamod.utils.setColorAnimated
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import gg.essential.universal.UMatrixStack
import java.lang.Float.min
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class ProgressBarComponent : UIBlock(MediaMod.themeManager.currentTheme.colors.progressBarBackground) {
    private val elapsedTextState = BasicState("0:00")
    private val durationTextState = BasicState("0:00")

    private var lastUpdate = 0L

    private val progressBlock = UIBlock(MediaMod.themeManager.currentTheme.colors.progressBar)
        .constrain {
            width = 1.pixels()
            height = 100.percent()
        } childOf this

    private val elapsedText = UIText("0:00", false)
        .constrain {
            x = 2.pixels()
            y = CenterConstraint()
            textScale = 0.7f.pixels()
            color = MediaMod.themeManager.currentTheme.colors.background.constraint
        } childOf this

    private val durationText = UIText("0:00", false)
        .constrain {
            x = 2.pixels(alignOpposite = true)
            y = CenterConstraint()
            textScale = 0.7f.pixels()
            color = MediaMod.themeManager.currentTheme.colors.background.constraint
        } childOf this

    init {
        elapsedText.bindText(elapsedTextState)
        durationText.bindText(durationTextState)

        MediaMod.serviceManager.currentTrack.onSetValue {
            it?.let {
                updateProgress(it.elapsed, it.duration)
                lastUpdate = System.currentTimeMillis()
            } ?: progressBlock.setWidth(0.pixels())
        }

        MediaMod.themeManager.onChange(this::updateTheme)
    }

    override fun draw(matrixStack: UMatrixStack) {
        MediaMod.serviceManager.currentTrack.get()?.let {
            if (lastUpdate != 0L && !it.paused) {
                val change = System.currentTimeMillis() - lastUpdate
                val elapsed = it.elapsed + change
                updateProgress(elapsed, it.duration)
            }
        }

        super.draw(matrixStack)
    }

    private fun updateTheme(theme: Theme) {
        setColorAnimated(theme.colors.progressBarBackground.constraint)
        progressBlock.setColorAnimated(theme.colors.progressBar.constraint)
        elapsedText.setColorAnimated(theme.colors.background.constraint)
        durationText.setColorAnimated(theme.colors.background.constraint)
    }

    private fun updateProgress(elapsed: Long, duration: Long) {
        val progress = min(elapsed / duration.toFloat(), 1f)
        progressBlock.setWidth((progress * 100).percent())

        updateProgressText(elapsed, duration)
    }

    private fun updateProgressText(elapsed: Long, duration: Long) {
        elapsedTextState.set(elapsed.milliseconds.format("%02d:%02d"))
        durationTextState.set(duration.milliseconds.format("%02d:%02d"))
    }

    private fun Duration.format(format: String) =
        toComponents { minutes, seconds, _ -> String.format(format, minutes, seconds) }
}