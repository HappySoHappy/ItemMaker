package me.howandev.itemmaker.configuration.serializer.impl;

import me.howandev.itemmaker.configuration.serializer.Serializer;
import me.howandev.itemmaker.configuration.serializer.SerializerRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapSerializer implements Serializer<Map<?, ?>> {
    @Override
    public @NotNull String getAlias() {
        return "Map";
    }

    @Override
    public @NotNull Map<?, ?> serialize(final @NotNull Object value) {
        if (!(value instanceof Map<?, ?> map)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");
        return map;
    }

    @Override
    public @NotNull Object deserialize(final @NotNull Object value) throws IllegalArgumentException {
        if (!(value instanceof Map<?, ?> map)) throw new IllegalArgumentException("Unsupported type was supplied to the Serializer!");

        Map<Object, Object> deserializedMap = new LinkedHashMap<>();
        if (map.containsKey(Serializer.SERIALIZED_OBJECT_KEY)) {
            String name = map.get(Serializer.SERIALIZED_OBJECT_KEY).toString();
            Serializer<?> customSerializer = SerializerRegistry.serializerFor(name);
            try {
                if (customSerializer == null)
                    customSerializer = SerializerRegistry.serializerFor(Class.forName(name));

                if (customSerializer != null)
                    return customSerializer.deserialize(map);
            } catch (ClassNotFoundException ignored) { }
        }

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            Object element = entry.getValue();
            Serializer<?> serializer = SerializerRegistry.serializerFor(element);
            if (serializer != null)
                deserializedMap.put(entry.getKey(), serializer.deserialize(element));
        }

        return deserializedMap;
    }

    @Override
    public boolean canHandle(final @NotNull Object value) {
        return value instanceof Map;
    }

    @Override
    public @Nullable Map<?, ?> parse(final Object serialized, final Map<?, ?> defaultValue) {
        if (serialized instanceof Map<?,?> serializedMap) {
            Map<Object, Object> deserializedMap = new LinkedHashMap<>(); //Keep order
            for (Map.Entry<?, ?> entry : serializedMap.entrySet()) {
                Object element = entry.getValue();
                Serializer<?> serializer = SerializerRegistry.serializerFor(element.getClass());
                if (serializer != null)
                    deserializedMap.put(entry.getKey(), serializer.parse(element));

                /*
                if (element instanceof String stringValue) {
                    Matcher matcher = CASTED_VALUE_PATTERN.matcher(stringValue);
                    if (!matcher.find()) continue;

                    try {
                        String name = matcher.group(1);
                        Serializer<?> customSerializer = SerializerRegistry.serializerFromAlias(name);
                        if (customSerializer == null)
                            customSerializer = SerializerRegistry.serializerFor(Class.forName(name));

                        if (customSerializer != null)
                            deserializedMap.put(entry.getKey(), customSerializer.deserialize(matcher.group(2)));
                    } catch (ClassNotFoundException ignored) { }
                }
                */
            }

            return deserializedMap;
        }

        return defaultValue;
    }
}
