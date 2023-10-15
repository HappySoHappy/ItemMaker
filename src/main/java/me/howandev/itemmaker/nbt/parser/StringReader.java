package me.howandev.itemmaker.nbt.parser;

import me.howandev.itemmaker.nbt.exceptions.MalformedNBTException;

import java.util.function.Predicate;

public class StringReader implements Cloneable {

    private final String input;
    private int cursor = 0;

    public StringReader(final String input) {
        this.input = input;
    }

    public String readUntil(final char terminator) {
        return readUntil(c -> c == terminator);
    }

    public String readUntil(final Predicate<Character> predicate) {
        final StringBuilder result = new StringBuilder();
        while (canRead()) {
            if (predicate.test(peek()))
                return result.toString();
            result.append(read());
        }
        return "";
    }

    public String finish() {
        final String string = input.substring(cursor);
        cursor = input.length();
        return string;
    }

    public boolean canRead() {
        return canRead(1);
    }

    public boolean canRead(final int length) {
        return cursor + length <= input.length();
    }

    public char peek() {
        return peek(0);
    }

    public char peek(final int offset) {
        return input.charAt(cursor + offset);
    }

    public char read() {
        return input.charAt(cursor++);
    }

    public char next() {
        skipWhitespace();
        return read();
    }

    public void skipWhitespace() {
        while (canRead() && Character.isWhitespace(peek()))
            read();
    }

    public void eat(final char expected) {
        final char actual = canRead() ? read() : 0;
        if (actual != expected)
            throw new MalformedNBTException(String.format("Expected character '%s' but found '%s'", expected, actual), cursor);
    }

    public void reset() {
        cursor = 0;
    }

    public int getRemaining() {
        return input.length() - cursor;
    }

    public String getInput() {
        return input;
    }

    public int getCursor() {
        return cursor;
    }

    public void setCursor(final int cursor) {
        this.cursor = cursor;
    }

    @Override
    public StringReader clone() {
        try {
            return (StringReader) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
