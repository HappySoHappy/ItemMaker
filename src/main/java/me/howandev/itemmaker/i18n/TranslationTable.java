package me.howandev.itemmaker.i18n;

import lombok.Getter;
import me.howandev.itemmaker.key.Key;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The {@link TranslationTable} class provides a mechanism to store and retrieve translation messages for a particular {@link Locale}.
 * <p>Translation messages are mapped using {@link Key} as the key and the translated text as the value.
 * <p>{@link TranslationTable} can be used to set individual translation messages, set multiple messages at once, or add a ResourceBundle
 *
 * @apiNote The {@link TranslationTable} class is designed to be thread-safe,
 * and is backed by a {@link ConcurrentHashMap} to store the translated messages.
 */
public class TranslationTable {
    private final Map<Key, String> messageStore = new ConcurrentHashMap<>();
    @Getter
    private final Locale locale;

    /**
     * Creates a new {@link TranslationTable} for the specified {@link Locale}.
     *
     * @param locale The ExtraLocale.
     */
    public TranslationTable(final Locale locale) {
        this.locale = locale;
    }

    /**
     * Sets the localized text for the specified {@link Key} in this {@link TranslationTable}.
     *
     * @param key The {@link Key} to associate the text with.
     * @param message The localized text.
     * @return this {@link TranslationTable} instance.
     */
    @Contract("_, _ -> this")
    public TranslationTable setMessage(final Key key, final String message) {
        messageStore.put(key, message);
        return this;
    }

    /**
     * Sets the localized messages for the specified {@link Key} in this {@link TranslationTable}.
     *
     * @param messages {@link Key}-text map containing messages.
     * @return this {@link TranslationTable} instance.
     */
    @Contract("_ -> this")
    public TranslationTable setMessages(final Map<Key, String> messages) {
        messageStore.putAll(messages);
        return this;
    }

    /**
     * Adds all messages from a {@link ResourceBundle}.
     *
     * @param bundle The {@link ResourceBundle} with messages
     * @return this {@link TranslationTable} instance.
     */
    @Contract("_ -> this")
    public TranslationTable addResourceBundle(final ResourceBundle bundle) {
        for (String key : bundle.keySet()) {
            setMessage(Key.of(key), bundle.getString(key));
        }

        return this;
    }

    /**
     * Adds all messages from a {@link ResourceBundle} with a specified name from a {@link ClassLoader}.
     *
     * @param classLoader The {@link ClassLoader}.
     * @param bundleName The name of the bundle.
     * @return this {@link TranslationTable} instance.
     */
    @Contract("_, _ -> this")
    public TranslationTable addMessageBundle(final ClassLoader classLoader, final String bundleName) {
        try {
            addResourceBundle(ResourceBundle.getBundle(bundleName, locale, classLoader));
        } catch (MissingResourceException ignored) { }

        return this;
    }

    /**
     * Returns the text that is associated with specified {@link Key},
     * or {@code null} if no text is associated with that {@link Key}.
     *
     * @param key the {@link Key}.
     * @return The localized text or null if no text is associated with the specified {@link Key}.
     */
    public @Nullable String getMessage(final Key key) {
        return messageStore.get(key);
    }
}
