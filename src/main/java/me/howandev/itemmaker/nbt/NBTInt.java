package me.howandev.itemmaker.nbt;

import me.howandev.itemmaker.nbt.visitor.NBTStringVisitor;
import me.howandev.itemmaker.nbt.visitor.NBTVisitor;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public record NBTInt(Integer value) implements NBTValue<Integer>, NBT {
    public NBTInt(final Object value) {
        this(((Number) value).intValue());
    }

    public NBTInt(final InputStream stream) throws IOException {
        this(decodeInt(stream));
    }

    static int decodeInt(final InputStream stream) throws IOException {
        final int a = stream.read(), b = stream.read(), c = stream.read(), d = stream.read();
        if (d < 0) throw new EOFException();
        return (a << 24) + (b << 16) + (c << 8) + d;
    }

    static void encodeInt(final OutputStream stream, final int value) throws IOException {
        final byte[] buffer = new byte[4];
        buffer[0] = (byte) (value >>> 24);
        buffer[1] = (byte) (value >>> 16);
        buffer[2] = (byte) (value >>> 8);
        buffer[3] = (byte) value;
        stream.write(buffer);
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public void write(final OutputStream stream) throws IOException {
        NBTInt.encodeInt(stream, value);
    }

    @Override
    public Tag tag() {
        return Tag.INT;
    }

    @Override
    public void accept(final NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final NBTInt nbtInt = (NBTInt) o;

        return value.equals(nbtInt.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public NBTInt clone() {
        try {
            return (NBTInt) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
