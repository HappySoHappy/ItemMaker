package me.howandev.itemmaker.util;

import me.howandev.itemmaker.ItemMakerConstants;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Locale;
import java.util.regex.Matcher;

public class PlayerUtil {
    private PlayerUtil() {
        throw new IllegalStateException("PlayerUtil class should not be instantiated!");
    }

    private static @Nullable Field getEntityField(Player player) throws NoSuchFieldException {
        Class<?> clazz = player.getClass();
        while (clazz != Object.class) {
            if (clazz.getName().endsWith("CraftEntity")) {
                Field field = clazz.getDeclaredField("entity");
                field.setAccessible(true);
                return field;
            }
            clazz = clazz.getSuperclass();
        }
        return null;
    }

    @Contract("_, !null -> !null")
    public static @Nullable Locale getPlayerLocale(@NotNull Player player, @Nullable Locale fallbackLocale) {
        if (!player.isOnline())
            return fallbackLocale;

        try {
            Field entityField = getEntityField(player);
            if (entityField == null)
                return fallbackLocale;

            Object nmsPlayer = entityField.get(player);
            if (nmsPlayer == null)
                return fallbackLocale;

            Field localeField = nmsPlayer.getClass().getDeclaredField("locale");
            localeField.setAccessible(true);
            Object localeObject = localeField.get(nmsPlayer);
            if (!(localeObject instanceof String localeString))
                return fallbackLocale;

            Matcher matcher = ItemMakerConstants.LOCALE_PATTERN.matcher(localeString);
            if (matcher.matches()) {
                String language = matcher.group("language");
                String country = matcher.group("country");
                String variant = matcher.group("variant");

                if (country != null && variant != null)
                    return new Locale(language, country, variant);

                if (country != null)
                    return new Locale(language, country);

                return new Locale(language);
            }
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            //Should never be thrown as this will never run on bukkit 1.7 or lower.
        }

        return fallbackLocale;
    }

    public static @Nullable Locale getPlayerLocale(@NotNull Player player) {
        return getPlayerLocale(player, null);
    }
}
