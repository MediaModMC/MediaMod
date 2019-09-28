package me.conorthedev.mediamod.media.base;

public abstract class AbstractMediaHandler implements IMediaHandler {
    protected boolean paused;
    protected long lastProgressUpdate;
    protected int lastProgressMs;
    protected int durationMs;

    public boolean isPaused() {
        return paused;
    }

    public long getLastProgressUpdate() {
        return lastProgressUpdate;
    }

    public int getLastProgressMs() {
        return lastProgressMs;
    }

    @Override
    public int getEstimatedProgressMs() {
        if (!paused) {
            int estimate = (int) (lastProgressMs + (System.currentTimeMillis() - lastProgressUpdate));
            if (estimate > durationMs) {
                estimate = durationMs;
            }
            return estimate;
        } else {
            return lastProgressMs;
        }
    }
}
