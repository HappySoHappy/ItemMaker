package me.howandev.itemmaker.nbt.exceptions;

public class MalformedNBTException extends NBTException {

    public MalformedNBTException(final String message, final int pos) {
        super(String.format("Malformed NBT data: %s @ %d", message, pos));
    }

    public MalformedNBTException(final String message, final Throwable cause, final int pos) {
        super(String.format("Malformed NBT data: %s @ %d", message, pos), cause);
    }
}
