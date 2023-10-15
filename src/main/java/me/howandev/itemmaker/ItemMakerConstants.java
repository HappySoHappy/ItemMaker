package me.howandev.itemmaker;

import org.bukkit.NamespacedKey;

import java.util.regex.Pattern;

public class ItemMakerConstants {
    public static final NamespacedKey CONTAINER_KEY = new NamespacedKey(ItemMakerPlugin.instance(), "data-container");
    public static final Pattern HEX_PATTERN = Pattern.compile("^#(?<red>[a-fA-F0-9]{2})(?<green>[a-fA-F0-9]{2})(?<blue>[a-fA-F0-9]{2})$");
    public static final Pattern LOCALE_PATTERN = Pattern.compile("^(?<language>[a-zA-Z]+)(?:_(?<country>[A-Za-z]+))?(?:-(?<variant>[A-Za-z]+))?$");
    public static final Pattern ENCHANTMENT_PATTERN = Pattern.compile("^(?:(?<namespace>[a-zA-Z-_]+):)?\\s*(?<enchantment>[a-zA-Z-_]+)(?:\\s+(?<level>\\d+))?$");
    public static final Pattern ATTRIBUTE_PATTERN = Pattern.compile("^(?:(?<slot>[_a-zA-Z]+) )?(?<attribute>[._a-zA-Z]+) (?<value>[-.?\\d]+)(?: (?<operation>[_a-zA-Z0-9]+))?$");
    public static final Pattern NEW_LINE_PATTERN = Pattern.compile(Pattern.quote("%nl%"), Pattern.CASE_INSENSITIVE);

    private ItemMakerConstants() {
        throw new IllegalStateException("ItemMakerConstants class should not be instantiated!");
    }
}
