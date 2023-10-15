package me.howandev.itemmaker.nbt.parser;

import me.howandev.itemmaker.nbt.*;
import me.howandev.itemmaker.nbt.exceptions.MalformedNBTException;

class NumberParser implements NBTElementParser<NBT> {

    @Override
    public NBT parse(final StringReader reader) throws MalformedNBTException {
        char c;
        final StringBuilder result = new StringBuilder();
        boolean hasDecimal = false;

        while (isNumberAllowed(c = reader.peek())) {
            if (c == '.') {
                if (hasDecimal)
                    throw new MalformedNBTException("The number contains multiple decimal points: " + result, reader.getCursor());
                hasDecimal = true;
            }
            result.append(reader.read());
        }
        final String numberString = result.toString();
        final NBT nbt;

        nbt = switch (Character.toLowerCase(reader.peek())) {
            case 'b' -> new NBTByte(Byte.parseByte(numberString));
            case 's' -> new NBTShort(Short.parseShort(numberString));
            case 'l' -> new NBTLong(Long.parseLong(numberString));
            case 'f' -> new NBTFloat(Float.parseFloat(numberString));
            case 'd' -> new NBTDouble(Double.parseDouble(numberString));
            default -> null;
        };

        if (nbt == null)
            return hasDecimal ? new NBTDouble(Double.parseDouble(numberString))
                    : new NBTInt(Integer.parseInt(numberString));

        reader.read();
        return nbt;
    }

    private static boolean isNumberAllowed(final char c) {
        return Character.isDigit(c) || c == '.' || c == '-';
    }

}
