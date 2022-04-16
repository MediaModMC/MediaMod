package dev.mediamod.gui.screen.editor.component

import com.github.kittinunf.result.getOrNull
import dev.mediamod.MediaMod
import dev.mediamod.data.api.mediamod.PublishThemeResponse
import dev.mediamod.gui.ColorPalette
import dev.mediamod.gui.component.UIButton
import dev.mediamod.gui.style.styled
import dev.mediamod.gui.style.stylesheet
import dev.mediamod.theme.Colors
import dev.mediamod.theme.Theme
import gg.essential.elementa.components.UIContainer
import gg.essential.elementa.components.UIText
import gg.essential.elementa.constraints.ChildBasedSizeConstraint
import gg.essential.elementa.constraints.SiblingConstraint
import gg.essential.elementa.dsl.*
import gg.essential.elementa.state.BasicState
import java.awt.Color
import kotlin.concurrent.thread
import kotlin.reflect.KMutableProperty

@Suppress("unused")
class ThemeEditorContainer : UIContainer() {
    val theme = BasicState<Theme?>(null)

    private val stylesheet = stylesheet {
        "this" {
            x = 15.pixels()
            y = 15.pixels()
            width = 100.percent() - 30.pixels()
            height = 100.percent() - 30.pixels()
        }

        "colorsContainer" {
            y = SiblingConstraint(7.5f)
            width = 100.percent()
            height = 100.percent()
        }

        "publishButton" {
            x = 0.pixels(true)
            y = 0.pixels(true)
            width = ChildBasedSizeConstraint() + 50.pixels()
            height = 25.pixels()
            color = ColorPalette.secondaryBackground.brighter().constraint
        }
    }

    private val themeNameState = BasicState("")
    private val themeNameText = UIText()
        .bindText(themeNameState)
        .constrain {
            textScale = 1.5f.pixels()
        } childOf this

    private val colorsContainer by UIContainer()
        .styled(stylesheet["colorsContainer"])
        .childOf(this)

    init {
        styled(stylesheet["this"])

        val publish = UIButton("Publish", Color.white)
            .styled(stylesheet["publishButton"])
            .onClick {
                theme.get()?.let {
                    if (it !is Theme.LoadedTheme) return@let
                    thread(true) { publishTheme(it) }
                }
            } childOf this

        if (theme.get() !is Theme.LoadedTheme)
            publish.hide(true)

        theme.onSetValue {
            it?.let {
                themeNameState.set(it.name)
                loadColors(it.colors)
            } ?: run {
                themeNameState.set("")
                colorsContainer.children.clear()
            }

            if (theme.get() !is Theme.LoadedTheme) {
                publish.hide(true)
            } else {
                publish.unhide()
            }
        }
    }

    private fun publishTheme(it: Theme.LoadedTheme) {
        MediaMod.notificationManager.showNotification("MediaMod", "Publishing theme...")

        val response = MediaMod.apiManager.publishTheme(it).getOrNull()
            ?: return MediaMod.notificationManager.showNotification("MediaMod", "Failed to publish theme!")

        if (response is PublishThemeResponse) {
            MediaMod.notificationManager.showNotification(
                "Theme published!",
                "URL copied to clipboard."
            )
            // TODO: Copy to clipboard
        } else {
            MediaMod.notificationManager.showNotification("MediaMod", "Failed to publish theme!")
        }
    }

    private fun loadColors(colors: Colors) {
        colorsContainer.children.clear()

        fun complete() = theme.get()?.let {
            if (it !is Theme.LoadedTheme) return@let
            it.colors = colors

            theme.set(it)
        }

        colorComponent(colors::background, "Background", ::complete)
        colorComponent(colors::progressBar, "Progress Bar", ::complete)
        colorComponent(colors::progressBarBackground, "Progress Bar Background", ::complete)
        colorComponent(colors::progressBarText, "Progress Bar Text", ::complete)
        colorComponent(colors::text, "Text", ::complete)
    }

    private fun colorComponent(color: KMutableProperty<Color>, name: String, block: () -> Unit) {
        ThemeColorComponent(color.getter.call(), name, theme.get() is Theme.InbuiltTheme)
            .constrain {
                y = SiblingConstraint(5f)
            }.onChange {
                color.setter.call(it)
                block()
            } childOf colorsContainer
    }
}