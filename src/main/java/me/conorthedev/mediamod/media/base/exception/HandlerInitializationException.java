package me.conorthedev.mediamod.media.base.exception;

public class HandlerInitializationException extends Exception {
    public HandlerInitializationException() {
        super();
    }

    public HandlerInitializationException(String s) {
        super(s);
    }

    public HandlerInitializationException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public HandlerInitializationException(Throwable throwable) {
        super(throwable);
    }

    protected HandlerInitializationException(String s, Throwable throwable, boolean b, boolean b1) {
        super(s, throwable, b, b1);
    }
}
