package me.conorthedev.mediamod.config;

public enum ProgressStyle {
    BAR_AND_NUMBERS_NEW("Progress bar and text (new)"),
    BAR_AND_NUMBERS_OLD("Progress bar and text (old)"),
    BAR_ONLY("Progress bar"),
    NUMBERS_ONLY("Text");
    private final String display;

    ProgressStyle(String display) {
        this.display = display;
    }

    public String getDisplay() {
        return display;
    }
}
