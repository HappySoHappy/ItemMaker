package me.howandev.itemmaker.nbt;

import me.howandev.itemmaker.nbt.visitor.NBTStringVisitor;
import me.howandev.itemmaker.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.io.OutputStream;

public record NBTEnd(Void value) implements NBTValue<Void>, NBT {
    public static final NBTEnd INSTANCE = new NBTEnd(null);

    public static NBTEnd getInstance(final Object object) {
        return INSTANCE;
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public void write(final OutputStream stream) throws IOException {
        stream.write(Tag.END.ordinal());
    }

    @Override
    public Tag tag() {
        return Tag.END;
    }

    @Override
    public void accept(final NBTVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(final Object o) {
        return o instanceof NBTEnd;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public NBTEnd clone() {
        try {
            return (NBTEnd) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
