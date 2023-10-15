package me.howandev.itemmaker.configuration.impl.file.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import me.howandev.itemmaker.configuration.serializer.Serializer;
import me.howandev.itemmaker.configuration.serializer.SerializerRegistry;

import java.lang.reflect.Type;

//Object to json
@Deprecated(forRemoval = true)
public class JsonSerializer implements com.google.gson.JsonSerializer<Object> {
    @Override
    public JsonElement serialize(Object o, Type type, JsonSerializationContext jsonSerializationContext) {
        Serializer<?> serializer = SerializerRegistry.serializerFor(o.getClass());
        System.out.println("serializer "+serializer+" for "+o);

        return new JsonObject();
    }
}
