package me.howandev.itemmaker.nbt.parser;

import me.howandev.itemmaker.nbt.NBT;
import me.howandev.itemmaker.nbt.NBTByteArray;
import me.howandev.itemmaker.nbt.NBTIntArray;
import me.howandev.itemmaker.nbt.NBTLongArray;
import me.howandev.itemmaker.nbt.exceptions.MalformedNBTException;

import java.util.ArrayList;
import java.util.List;

class ArrayParser implements NBTElementParser<NBT> {

    @Override
    public NBT parse(final StringReader reader) throws MalformedNBTException {
        reader.eat('[');
        final List<Number> elements = new ArrayList<>();
        final char dataClass = reader.read();
        final Class<? extends Number> arrayClass = switch (dataClass) {
            case 'B' -> Byte.class;
            case 'I' -> Integer.class;
            case 'L' -> Long.class;
            default -> throw new MalformedNBTException("Unexpected array type '" + dataClass + '\'', reader.getCursor());
        };
        reader.eat(';');
        boolean loop;
        do {
            reader.skipWhitespace();
            if (reader.peek() == ']')
                break;
            final Number value = new NumberParser().parse(reader).value();

            if (!arrayClass.isInstance(value))
                throw new MalformedNBTException("Unable to parse array."
                        + "Value is not of expected type " + arrayClass.getName() + ".",
                        reader.getCursor());

            elements.add(value);
            loop = false;
            reader.skipWhitespace();
            if (reader.peek() == ',') {
                loop = true;
                reader.next();
            }
        } while (loop);
        reader.skipWhitespace();
        reader.eat(']');

        final Number[] array = elements.toArray(new Number[0]);
        if (arrayClass == Byte.class) {
            final byte[] bytes = new byte[array.length];
            for (int i = 0; i < array.length; i++)
                bytes[i] = (byte) array[i];

            return new NBTByteArray(bytes);
        } else if (arrayClass == Integer.class) {
            final int[] ints = new int[array.length];
            for (int i = 0; i < array.length; i++)
                ints[i] = (int) array[i];

            return new NBTIntArray(ints);
        }

        final long[] longs = new long[array.length];
        for (int i = 0; i < array.length; i++)
            longs[i] = (long) array[i];

        return new NBTLongArray(longs);
    }

}
