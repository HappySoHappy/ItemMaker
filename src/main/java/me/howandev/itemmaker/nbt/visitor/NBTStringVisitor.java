package me.howandev.itemmaker.nbt.visitor;

import me.howandev.itemmaker.nbt.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class NBTStringVisitor implements NBTVisitor {

    private static final Pattern SIMPLE_VALUE = Pattern.compile("[\\dA-Za-z_\\-.+]+");

    private final StringBuilder builder = new StringBuilder();

    public String visitNBT(final NBT nbt) {
        nbt.accept(this);
        return toString();
    }

    @Override
    public void visit(final NBTString nbtString) {
        builder.append(NBTString.quoteAndEscape(nbtString.value()));
    }

    @Override
    public void visit(final NBTByte nbtByte) {
        builder.append(nbtByte.value()).append('b');
    }

    @Override
    public void visit(final NBTShort nbtShort) {
        builder.append(nbtShort.value()).append('s');
    }

    @Override
    public void visit(final NBTInt nbtInt) {
        builder.append(nbtInt.value());
    }

    @Override
    public void visit(final NBTLong nbtLong) {
        builder.append(nbtLong.value()).append('L');
    }

    @Override
    public void visit(final NBTFloat nbtFloat) {
        builder.append(nbtFloat.value()).append('f');
    }

    @Override
    public void visit(final NBTDouble nbtDouble) {
        builder.append(nbtDouble.value()).append('d');
    }

    @Override
    public void visit(final NBTByteArray nbtByteArray) {
        builder.append("[B;");
        final byte[] bytes = nbtByteArray.value();

        for (int i = 0; i < bytes.length; i++) {
            if (i != 0)
                builder.append(',');

            builder.append(bytes[i]).append('B');
        }

        builder.append(']');
    }

    @Override
    public void visit(final NBTIntArray nbtIntArray) {
        builder.append("[I;");
        final int[] ints = nbtIntArray.value();

        for (int i = 0; i < ints.length; i++) {
            if (i != 0)
                builder.append(',');

            builder.append(ints[i]);
        }

        builder.append(']');
    }

    @Override
    public void visit(final NBTLongArray nbtLongArray) {
        builder.append("[L;");
        final long[] longs = nbtLongArray.value();

        for (int i = 0; i < longs.length; i++) {
            if (i != 0)
                builder.append(',');

            builder.append(longs[i]).append('L');
        }

        builder.append(']');
    }

    @Override
    public void visit(final NBTList nbtList) {
        builder.append('[');

        for (int i = 0; i < nbtList.size(); i++) {
            if (i != 0)
                builder.append(',');

            builder.append(new NBTStringVisitor().visitNBT(nbtList.get(i)));
        }

        builder.append(']');
    }

    @Override
    public void visit(final NBTCompound nbtCompound) {
        builder.append('{');
        final List<String> keys = new ArrayList<>(nbtCompound.keySet());
        Collections.sort(keys);

        for (int i = 0; i < keys.size(); i++) {
            final String key = keys.get(i);
            if (i != 0)
                builder.append(',');

            builder.append(handleEscape(key)).append(':');
            builder.append(new NBTStringVisitor().visitNBT(nbtCompound.get((Object) key)));
        }

        builder.append('}');
    }

    @Override
    public void visit(final NBTEnd nbtEnd) {

    }

    @Override
    public String toString() {
        return builder.toString();
    }

    public void clear() {
        builder.delete(0, builder.length() - 1);
    }

    private static String handleEscape(final String string) {
        return SIMPLE_VALUE.matcher(string).matches() ? string : NBTString.quoteAndEscape(string);
    }
}
