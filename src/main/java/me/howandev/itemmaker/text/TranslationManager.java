package me.howandev.itemmaker.text;

import lombok.Getter;
import me.howandev.itemmaker.ItemMakerConstants;
import me.howandev.itemmaker.configuration.impl.Configuration;
import me.howandev.itemmaker.configuration.impl.file.yaml.YamlConfiguration;
import me.howandev.itemmaker.i18n.TranslationRegistry;
import me.howandev.itemmaker.key.Key;
import me.howandev.itemmaker.util.FileUtil;
import me.howandev.itemmaker.util.PlayerUtil;
import me.howandev.itemmaker.util.TextUtil;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TranslationManager {
    //TODO: add papi support for items
    public static final Pattern TRANSLATE_PATTERN = Pattern.compile("<translate:(?<key>[^:]+)>", Pattern.CASE_INSENSITIVE);
    private final JavaPlugin plugin;
    @Getter
    private final TranslationRegistry<Player> registry;
    private final Map<UUID, Locale> playerLocaleStore = new HashMap<>();
    private final Map<UUID, BiConsumer<Player, Locale>> playerLocaleChanged = new HashMap<>();
    private boolean perPlayerLocale = false;
    public TranslationManager(final @NotNull JavaPlugin plugin) {
        this.plugin = plugin;
        this.registry = TranslationRegistry.create(this::getPlayerLocale);
    }

    public TranslationManager(final @NotNull JavaPlugin plugin, final @NotNull Locale defaultLocale) {
        this.plugin = plugin;
        this.registry = TranslationRegistry.create(this::getPlayerLocale, defaultLocale);
    }

    public void loadTranslations() {
        File localesDirectory = new File(plugin.getDataFolder(), "locales");
        if (!localesDirectory.exists())
            return;

        Set<File> localeFiles = FileUtil.listFileTree(localesDirectory);
        for (File localeFile : localeFiles) {
            try {
                //Bukkit configuration sucks ass, i much more prefer my take on it.
                YamlConfiguration config = new YamlConfiguration(localeFile);
                config.load();

                int dotIndex = localeFile.getName().lastIndexOf('.');
                String localeString = (dotIndex == -1) ? localeFile.getName() : localeFile.getName().substring(0, dotIndex);

                Matcher matcher = ItemMakerConstants.LOCALE_PATTERN.matcher(localeString);
                if (!matcher.matches())
                    throw new IllegalArgumentException(String.format("Provided localeString '%s' is not a valid ISO-639 locale!", localeString));

                String language = matcher.group("language");
                String country = matcher.group("country");
                String variant = matcher.group("variant");
                if (country != null && variant != null) //todo: loads with variant
                    registry.addMessageConfiguration(config, new Locale(language, country, variant));

                if (country != null) //todo: after loading with variant the same locale will be used for country
                    registry.addMessageConfiguration(config, new Locale(language, country));

                registry.addMessageConfiguration(config, new Locale(language)); //todo: after loading with country same locale will be used for language
                plugin.getLogger().info(String.format("Successfully loaded '%s' translation from '%s'", localeString, localeFile.getName()));
            } catch (IOException | IllegalStateException | IllegalArgumentException ex) {
                plugin.getLogger().severe(String.format("Failed to load language file '%s', %s", localeFile.getName(), ex.getMessage()));
            }
        }
    }

    public @NotNull Locale getPlayerLocale(final @NotNull Player player) {
        if (!perPlayerLocale)
            return registry.getDefaultLocale();

        //Forced locale
        if (playerLocaleStore.containsKey(player.getUniqueId()))
            return playerLocaleStore.get(player.getUniqueId());

        return PlayerUtil.getPlayerLocale(player, registry.getDefaultLocale());
    }

    public void setPlayerLocale(final @NotNull Player player, final @NotNull Locale locale) {
        playerLocaleStore.put(player.getUniqueId(), locale);
    }

    public void addLocaleChangeConsumer(UUID uniqueId, BiConsumer<Player, Locale> consumer) {
        playerLocaleChanged.put(uniqueId, consumer);
    }

    public void notifyLocaleChange(Player player, Locale newLocale) {
        for (Map.Entry<UUID, BiConsumer<Player, Locale>> entry : playerLocaleChanged.entrySet()) {
            BiConsumer<Player, Locale> consumer = entry.getValue();
            if (consumer != null)
                consumer.accept(player, newLocale);
        }
    }

    public void usePerPlayerLocale(boolean perPlayerLocale) {
        this.perPlayerLocale = perPlayerLocale;
    }

    public void setMessage(final @NotNull Locale locale, final @NotNull Key key, final @NotNull String message) {
        getRegistry().setMessage(locale, key, message);
    }

    public void setMessages(final @NotNull Locale locale, final @NotNull Map<Key, String> messages) {
        getRegistry().setMessages(locale, messages);
    }

    public void addMessageBundle(final @NotNull ClassLoader classLoader, final @NotNull String bundleName, final @NotNull Locale... locales) {
        getRegistry().addMessageBundle(classLoader, bundleName, locales);
    }

    public void addMessageBundle(final @NotNull String bundleName, final @NotNull Locale... locales) {
        getRegistry().addMessageBundle(getClass().getClassLoader(), bundleName, locales);
    }

    public void addMessageConfiguration(final @NotNull Configuration config, final @NotNull Locale locale) {
        getRegistry().addMessageConfiguration(config, locale);
    }

    public @NotNull String getMessage(final Player player, final Key key, final boolean defaultLocaleFallback) {
        String message = registry.getMessage(player, key, defaultLocaleFallback);
        if (message != null)
            return message;

        plugin.getLogger().warning(String.format("Missing translation: '%s' for '%s' locale", key, registry.getLocale(player)));
        return String.format("<missing:%s>", key);
    }

    public @NotNull String getMessage(final Player player, final Key key) {
        return getMessage(player, key, true);
    }

    public @NotNull String replaceTranslations(final @NotNull Player player, final @NotNull String message) {
        Matcher matcher = TRANSLATE_PATTERN.matcher(message);
        if (!matcher.find())
            return message;

        matcher.reset();
        StringBuilder sb = new StringBuilder(message.length());
        while (matcher.find()) {
            Key key = Key.of(matcher.group("key"));
            matcher.appendReplacement(sb, Matcher.quoteReplacement(getMessage(player, key)));
        }

        matcher.appendTail(sb);
        return sb.toString();
    }

    public @NotNull String getTranslation(final @NotNull Player player, final @NotNull Key key, final String... replacements) {
        String message = getMessage(player, key);
        if (replacements.length > 0) {
            message = TextUtil.replace(message, replacements);
        }

        message = replaceTranslations(player, message);
        return message;
    }

    public @NotNull String getTranslation(final @NotNull Player player, final @NotNull String key, final String... replacements) {
        String message = getMessage(player, Key.of(key));
        if (replacements.length > 0) {
            message = TextUtil.replace(message, replacements);
        }

        message = replaceTranslations(player, message);
        return message;
    }
}
