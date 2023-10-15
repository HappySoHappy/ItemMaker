package me.howandev.itemmaker.configuration.serializer.impl;

import me.howandev.itemmaker.configuration.serializer.Serializer;
import me.howandev.itemmaker.configuration.serializer.SerializerRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ListSerializer implements Serializer<List<?>> {
    @Override
    public @NotNull String getAlias() {
        return "List";
    }

    @Override
    public @NotNull List<?> serialize(final @NotNull Object value) {
        if (!(value instanceof List<?> list)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");

        List<Object> serializedList = new ArrayList<>(list.size());
        for (Object element : list) {
            Serializer<?> serializer = SerializerRegistry.serializerFor(element.getClass());
            if (serializer != null) {
                serializedList.add(serializer.serialize(element));
            }
        }

        return serializedList;
    }

    @Override
    public @NotNull Object deserialize(final @NotNull Object value) throws IllegalArgumentException {
        if (!(value instanceof List<?> list)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");

        List<Object> deserializedList = new ArrayList<>();
        for (Object element : list) {
            Serializer<?> serializer = SerializerRegistry.serializerFor(element);
            if (serializer != null)
                deserializedList.add(serializer.deserialize(element));
        }

        return deserializedList;
    }

    @Override
    public boolean canHandle(final @NotNull Object value) {
        return value instanceof List;
    }

    @Override
    public @Nullable List<?> parse(final Object serialized, final List<?> defaultValue) {
        if (serialized instanceof List<?> serializedList) {
            List<Object> deserializedList = new ArrayList<>();
            for (Object element : serializedList) {
                Serializer<?> serializer = SerializerRegistry.serializerFor(element);
                if (serializer != null)
                    deserializedList.add(serializer.parse(element));

                /*
                if (element instanceof String stringValue) {
                    Matcher matcher = CASTED_VALUE_PATTERN.matcher(stringValue);
                    if (!matcher.matches()) continue;

                    String name = matcher.group(1);
                    try {
                        Serializer<?> customSerializer = SerializerRegistry.serializerFromAlias(name);

                        if (customSerializer == null)
                            customSerializer = SerializerRegistry.serializerFor(Class.forName(name));

                        if (customSerializer != null) {
                            deserializedList.remove(deserializedList.size() - 1);
                            deserializedList.add(customSerializer.deserialize(matcher.group(2)));
                        }
                    } catch (ClassNotFoundException ignored) { }
                }
                */
            }
            return deserializedList;
        }

        return defaultValue;
    }
}
