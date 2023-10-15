package me.howandev.itemmaker.configuration.impl.file.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.howandev.itemmaker.configuration.impl.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Map;

@Deprecated(forRemoval = true)
public class JsonConfiguration extends FileConfiguration {
    private static final JsonDeserializer JSON_DESERIALIZER = new JsonDeserializer();
    private static final JsonSerializer JSON_SERIALIZER = new JsonSerializer();
    private static final Gson GSON;
    static {
        GSON = new GsonBuilder()
                .registerTypeHierarchyAdapter(Object.class, JSON_DESERIALIZER)
                .registerTypeHierarchyAdapter(Object.class, JSON_SERIALIZER)
                .setPrettyPrinting()
                .create();
    }

    public JsonConfiguration(@NotNull File file) {
        super(file);
    }

    @Override
    public @NotNull FileConfiguration loadFromString(@NotNull String contents) {
        Map<?, ?> loadedStore;
        try {
            loadedStore = GSON.fromJson(contents, Map.class);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex);
        }

        if (loadedStore != null) {
            convertMapsToSections(loadedStore, this);
        }

        return this;
    }

    @Override
    public String dumpToString() {
        return GSON.toJson(getValues(false));
    }
}
