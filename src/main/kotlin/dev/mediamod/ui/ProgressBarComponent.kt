package dev.mediamod.ui

import dev.mediamod.MediaMod
import dev.mediamod.data.Track
import dev.mediamod.theme.Theme
import dev.mediamod.utils.setColorAnimated
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.CenterConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import gg.essential.universal.UMatrixStack
import java.lang.Float.min
import java.time.Duration


class ProgressBarComponent : UIBlock(MediaMod.themeManager.currentTheme.colors.progressBarBackground) {
    private val trackState = BasicState<Track?>(null)
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
        trackState.onSetValue {
            it?.let {
                val progress = (it.elapsed / it.duration.toFloat())
                progressBlock.setWidth((progress * 100).percent())
                setProgressText(it.elapsed, it.duration)

                lastUpdate = System.currentTimeMillis()
            } ?: progressBlock.setWidth(0.pixels())
        }

        MediaMod.themeManager.onChange(this::updateTheme)
    }

    fun update(track: Track) {
        trackState.set(track)
    }

    override fun draw(matrixStack: UMatrixStack) {
        trackState.get()?.let {
            if (lastUpdate != 0L && !it.paused) {
                val change = System.currentTimeMillis() - lastUpdate
                val elapsed = it.elapsed + change
                val progress = min(elapsed / it.duration.toFloat(), 1f)

                setProgressText(elapsed, it.duration)
                progressBlock.setWidth((progress * 100).percent())
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

    private fun setProgressText(elapsed: Long, duration: Long) {
        val parsedElapsed = Duration.ofMillis(elapsed)
        val parsedDuration = Duration.ofMillis(duration)
        elapsedText.setText(String.format("%d:%02d", parsedElapsed.toMinutes() % 60, parsedElapsed.seconds % 60))
        durationText.setText(String.format("%d:%02d", parsedDuration.toMinutes() % 60, parsedDuration.seconds % 60))
    }
}