package me.howandev.itemmaker.key;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The {@link Key} class represents a unique identifier.<p>
 *
 * @apiNote The {@link Key} class is designed to be thread-safe,
 * and is backed by a {@link ConcurrentHashMap} to store the values.
 */
public class Key {
    protected static final Map<String, Key> KEY_STORE = new ConcurrentHashMap<>();
    private static final AtomicInteger HASH_COUNTER = new AtomicInteger();
    private final int hash = HASH_COUNTER.getAndIncrement();
    private final @NotNull String key;
    protected Key(final @NotNull String key) {
        this.key = key;
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public String toString() {
        return key;
    }

    public static @NotNull Key of(final @NotNull String key) {
        return KEY_STORE.computeIfAbsent(key.toLowerCase().intern(), Key::new);
    }
}
