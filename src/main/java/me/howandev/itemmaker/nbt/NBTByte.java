package me.howandev.itemmaker.nbt;

import me.howandev.itemmaker.nbt.visitor.NBTStringVisitor;
import me.howandev.itemmaker.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public record NBTByte(Byte value) implements NBTValue<Byte>, NBT {

    public NBTByte(final Object value) {
        this(((Number) value).byteValue());
    }

    public NBTByte(final Boolean value) {
        this(value ? 1 : 0);
    }

    public NBTByte(final InputStream stream) throws IOException {
        this((byte) stream.read());
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public void write(final OutputStream stream) throws IOException {
        stream.write(value);
    }

    @Override
    public Tag tag() {
        return Tag.BYTE;
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

        final NBTByte nbtByte = (NBTByte) o;

        return value.equals(nbtByte.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public NBTByte clone() {
        try {
            return (NBTByte) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

}
