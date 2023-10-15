package me.howandev.itemmaker.configuration.impl.file.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

//Json to object
@Deprecated(forRemoval = true)
public class JsonDeserializer implements com.google.gson.JsonDeserializer<Object> {
    @Override
    public Object deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        if (jsonElement.isJsonObject()) {
            //stackoverflow
            return jsonDeserializationContext.deserialize(jsonElement.getAsJsonObject(), Map.class);

        } else if (jsonElement.isJsonArray()) {
            return jsonDeserializationContext.deserialize(jsonElement, List.class);
        } else if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive primitive = jsonElement.getAsJsonPrimitive();
            if (primitive.isString()) {
                return primitive.getAsString();
            } else if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            } else if (primitive.isNumber()) {
                String numberString = primitive.getAsString();
                if (numberString.contains(".")) {
                    return primitive.getAsDouble();
                }
                int number = primitive.getAsInt();
                if (numberString.equals(String.valueOf(number))) {
                    return primitive.getAsInt();
                }
                return primitive.getAsLong();
            }
            throw new JsonParseException("Unknown json primitive: " + primitive.getClass().getSimpleName());
        } else if (jsonElement.isJsonNull()) {
            return null;
        }

        throw new JsonParseException("Unknown json type: " + jsonElement.getClass().getSimpleName());
    }
}
