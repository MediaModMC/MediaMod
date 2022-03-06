package dev.mediamod.ui

import dev.mediamod.MediaMod
import dev.mediamod.data.Track
import dev.mediamod.theme.Theme
import dev.mediamod.utils.setColorAnimated
import gg.essential.elementa.components.UIBlock
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import gg.essential.universal.UMatrixStack
import java.lang.Float.min

class ProgressBarComponent : UIBlock(MediaMod.themeManager.currentTheme.colors.progressBarBackground) {
    private val trackState = BasicState<Track?>(null)
    private var lastUpdate = 0L

    private val progressBlock = UIBlock(MediaMod.themeManager.currentTheme.colors.progressBar)
        .constrain {
            width = 1.pixels()
            height = 100.percent()
        } childOf this

    init {
        trackState.onSetValue {
            it?.let {
                val progress = (it.elapsed / it.duration.toFloat())
                progressBlock.setWidth((progress * 100).percent())
                lastUpdate = System.currentTimeMillis()
            } ?: run {
                progressBlock.setWidth(0.pixels())
            }
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

                progressBlock.setWidth((progress * 100).percent())
            }
        }

        super.draw(matrixStack)
    }

    private fun updateTheme(theme: Theme) {
        setColorAnimated(theme.colors.progressBarBackground.constraint)
        progressBlock.setColorAnimated(theme.colors.progressBar.constraint)
    }
}