package me.howandev.itemmaker.nbt.parser;

import me.howandev.itemmaker.nbt.NBT;
import me.howandev.itemmaker.nbt.exceptions.MalformedNBTException;

interface NBTElementParser<T extends NBT> {

    T parse(StringReader reader) throws MalformedNBTException;

}
