package me.howandev.itemmaker.item.context;

import me.howandev.itemmaker.item.context.impl.SomeGenericContext;
import me.howandev.itemmaker.key.Key;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class ContextRegistry {
    private static final Map<Key, ItemContext> REGISTERED_CONTEXTS = new HashMap<>();
    public static final SomeGenericContext SOME_GENERIC_CONTEXT;
    static {
        SOME_GENERIC_CONTEXT = new SomeGenericContext();
        register(SOME_GENERIC_CONTEXT);
    }

    public static void register(ItemContext attribute) {
        REGISTERED_CONTEXTS.put(attribute.getKey(), attribute);
    }

    public static @Nullable ItemContext getRegisteredContext(Key key) {
        return REGISTERED_CONTEXTS.get(key);
    }

    /**
     * Dummy method to initialize the class, does nothing.
     */
    public static void initializeRegistry() {
    }
}
