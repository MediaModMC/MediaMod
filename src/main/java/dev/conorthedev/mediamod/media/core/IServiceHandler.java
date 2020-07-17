package dev.conorthedev.mediamod.media.core;

import dev.conorthedev.mediamod.media.MediaHandler;
import dev.conorthedev.mediamod.media.core.api.MediaInfo;
import net.minecraftforge.fml.common.gameevent.InputEvent;

import javax.annotation.Nullable;

public interface IServiceHandler extends Comparable<IServiceHandler> {
    /**
     * The name to be shown in MediaMod Menus
     */
    String displayName();

    /**
     * This should initialise any needed variables, start any local servers, etc.
     *
     * @return If initialisation was successful
     */
    boolean load();

    /**
     * This indicates if the handler is ready for usage
     */
    boolean isReady();

    /**
     * Returns metadata about the current track
     * If no track is playing, or an error occurs it can return null
     */
    @Nullable
    MediaInfo getCurrentMediaInfo();

    /**
     * This returns the priority of the service handler 0 being the lowest and the number of services loaded being the highest
     * <p>
     * TODO: Load from configuration
     */
    default int getPriority() {
        return 0;
    }

    /**
     * Compares the current service handler against another
     *
     * @param compareTo: The service handler to compare to
     * @return The priority that comes first
     */
    default int compareTo(IServiceHandler compareTo) {
        return compareTo.getPriority() - this.getPriority();
    }

    /**
     * @return The estimated progress in milliseconds based on the amount of time since the last track update
     */
    default int getEstimatedProgress() {
        return 0;
    }

    /**
     * @return a boolean which states if the service supports skipping tracks
     * @see dev.conorthedev.mediamod.keybinds.KeybindInputHandler#onKeyInput(InputEvent.KeyInputEvent)
     */
    default boolean supportsSkipping() {
        return false;
    }

    /**
     * @return a boolean which states if the service supports pausing tracks
     * @see dev.conorthedev.mediamod.keybinds.KeybindInputHandler#onKeyInput(InputEvent.KeyInputEvent)
     */
    default boolean supportsPausing() {
        return false;
    }

    /**
     * @return a boolean which states if the action was successful
     * @see dev.conorthedev.mediamod.keybinds.KeybindInputHandler#onKeyInput(InputEvent.KeyInputEvent)
     */
    default boolean skipTrack() {
        return supportsSkipping();
    }

    /**
     * @return a boolean which states if the action was successful
     * @see dev.conorthedev.mediamod.keybinds.KeybindInputHandler#onKeyInput(InputEvent.KeyInputEvent)
     */
    default boolean pausePlayTrack() {
        return supportsPausing();
    }
}
