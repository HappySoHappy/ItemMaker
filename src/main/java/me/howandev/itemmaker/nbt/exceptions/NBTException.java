package me.howandev.itemmaker.nbt.exceptions;

public class NBTException extends RuntimeException {
    public NBTException() {
        super();
    }

    public NBTException(final String message) {
        super(message);
    }

    public NBTException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public NBTException(final Throwable cause) {
        super(cause);
    }

    protected NBTException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
