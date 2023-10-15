package me.howandev.itemmaker.nbt;


import me.howandev.itemmaker.nbt.visitor.NBTStringVisitor;
import me.howandev.itemmaker.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public record NBTFloat(Float value) implements NBTValue<Float>, NBT {

    public NBTFloat(final Object value) {
        this(((Number) value).floatValue());
    }

    public NBTFloat(final InputStream stream) throws IOException {
        this(Float.intBitsToFloat(NBTInt.decodeInt(stream)));
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public void write(final OutputStream stream) throws IOException {
        final int value = Float.floatToIntBits(this.value);
        NBTInt.encodeInt(stream, value);
    }

    @Override
    public Tag tag() {
        return Tag.FLOAT;
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

        final NBTFloat nbtFloat = (NBTFloat) o;

        return value.equals(nbtFloat.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public NBTFloat clone() {
        try {
            return (NBTFloat) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
