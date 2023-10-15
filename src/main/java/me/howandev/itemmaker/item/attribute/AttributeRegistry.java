package me.howandev.itemmaker.item.attribute;

import me.howandev.itemmaker.ItemMakerPlugin;
import me.howandev.itemmaker.item.attribute.impl.*;
import me.howandev.itemmaker.key.Key;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

//TODO: this class should have a public constructor instead of being static
// item undroppable - disable dropping
public class AttributeRegistry {
    private static final Map<Key, ItemAttribute<?>> REGISTERED_ATTRIBUTES = new HashMap<>();
    public static final RainbowLeatherAttribute RAINBOW_LEATHER_ATTRIBUTE;
    public static final UnsmeltableAttribute UNSMELTABLE_ATTRIBUTE;
    public static final UncraftableAttribute UNCRAFTABLE_ATTRIBUTE;
    public static final UnenchantableAttribute UNENCHANTABLE_ATTRIBUTE;
    public static final UngrindableAttribute UNGRINDABLE_ATTRIBUTE;
    public static final UnrepairableAttribute UNREPAIRABLE_ATTRIBUTE;
    public static final UnsmithableAttribute UNSMITHABLE_ATTRIBUTE;
    static {
        RAINBOW_LEATHER_ATTRIBUTE = new RainbowLeatherAttribute();
        register(RAINBOW_LEATHER_ATTRIBUTE);

        UNSMELTABLE_ATTRIBUTE = new UnsmeltableAttribute();
        register(UNSMELTABLE_ATTRIBUTE);

        UNCRAFTABLE_ATTRIBUTE = new UncraftableAttribute();
        register(UNCRAFTABLE_ATTRIBUTE);

        UNENCHANTABLE_ATTRIBUTE = new UnenchantableAttribute();
        register(UNENCHANTABLE_ATTRIBUTE);

        UNGRINDABLE_ATTRIBUTE = new UngrindableAttribute();
        register(UNGRINDABLE_ATTRIBUTE);

        UNREPAIRABLE_ATTRIBUTE = new UnrepairableAttribute();
        register(UNREPAIRABLE_ATTRIBUTE);

        UNSMITHABLE_ATTRIBUTE = new UnsmithableAttribute();
        register(UNSMITHABLE_ATTRIBUTE);
    }

    public static void register(ItemAttribute<?> attribute) {
        REGISTERED_ATTRIBUTES.put(attribute.getKey(), attribute);
    }

    public static void register(ItemEventAttribute eventAttribute) {
        Bukkit.getPluginManager().registerEvents(eventAttribute, ItemMakerPlugin.instance());
        REGISTERED_ATTRIBUTES.put(eventAttribute.getKey(), eventAttribute);
    }

    public static void register(ItemScheduleAttribute scheduleAttribute) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(
                ItemMakerPlugin.instance(),
                scheduleAttribute::execute,
                0,
                scheduleAttribute.getPeriodMillis() / 50);
        REGISTERED_ATTRIBUTES.put(scheduleAttribute.getKey(), scheduleAttribute);
    }

    public static @Nullable ItemAttribute<?> getRegisteredAttribute(Key key) {
        return REGISTERED_ATTRIBUTES.get(key);
    }

    /**
     * Dummy method to initialize the class, does nothing.
     */
    public static void initializeRegistry() {
    }
}
