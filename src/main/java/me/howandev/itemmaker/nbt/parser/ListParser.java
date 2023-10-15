package me.howandev.itemmaker.nbt.parser;

import me.howandev.itemmaker.nbt.NBTList;
import me.howandev.itemmaker.nbt.exceptions.MalformedNBTException;

class ListParser implements NBTElementParser<NBTList> {

    @Override
    public NBTList parse(final StringReader reader) throws MalformedNBTException {
        reader.eat('[');
        final NBTList list = new NBTList();
        boolean loop;
        do {
            reader.skipWhitespace();
            if (reader.peek() == ']')
                break;
            list.add(new NBTValueParser().parse(reader));
            loop = false;
            reader.skipWhitespace();
            if (reader.peek() == ',') {
                loop = true;
                reader.next();
            }
        } while (loop);
        reader.skipWhitespace();
        reader.eat(']');
        return list;
    }

}
