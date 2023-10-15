package me.howandev.itemmaker.item.property;

import me.howandev.itemmaker.item.property.impl.*;
import me.howandev.itemmaker.item.property.impl.meta.ColorProperty;
import me.howandev.itemmaker.item.property.impl.meta.DamageProperty;
import me.howandev.itemmaker.item.property.impl.meta.RepairCostProperty;
import me.howandev.itemmaker.key.Key;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

//armor trims etc... 1.20 shit
public final class PropertyRegistry {
    private static final Map<Key, ItemProperty<?>> REGISTERED_PROPERTIES = new HashMap<>();
    public static final AttributeModifierProperty ATTRIBUTE_MODIFIER_PROPERTY;
    public static final ColorProperty COLOR_PROPERTY;
    public static final CustomModelProperty CUSTOM_MODEL_PROPERTY;
    public static final DisplayNameProperty DISPLAY_NAME_PROPERTY;
    public static final DamageProperty DAMAGE_PROPERTY;
    public static final EnchantmentProperty ENCHANTMENT_PROPERTY;
    public static final FlagProperty FLAG_PROPERTY;
    public static final GlowProperty GLOW_PROPERTY;
    public static final LoreProperty LORE_PROPERTY;
    public static final MaterialProperty MATERIAL_PROPERTY;
    public static final RepairCostProperty REPAIR_COST_PROPERTY;
    public static final UnbreakableProperty UNBREAKABLE_PROPERTY;
    public static final UnstackableProperty UNSTACKABLE_PROPERTY;

    static {
        ATTRIBUTE_MODIFIER_PROPERTY = new AttributeModifierProperty();
        register(ATTRIBUTE_MODIFIER_PROPERTY);

        COLOR_PROPERTY = new ColorProperty();
        register(COLOR_PROPERTY);

        CUSTOM_MODEL_PROPERTY = new CustomModelProperty();
        register(CUSTOM_MODEL_PROPERTY);

        DISPLAY_NAME_PROPERTY = new DisplayNameProperty();
        register(DISPLAY_NAME_PROPERTY);

        DAMAGE_PROPERTY = new DamageProperty();
        register(DAMAGE_PROPERTY);

        ENCHANTMENT_PROPERTY = new EnchantmentProperty();
        register(ENCHANTMENT_PROPERTY);

        FLAG_PROPERTY = new FlagProperty();
        register(FLAG_PROPERTY);

        GLOW_PROPERTY = new GlowProperty();
        register(GLOW_PROPERTY);

        LORE_PROPERTY = new LoreProperty();
        register(LORE_PROPERTY);

        MATERIAL_PROPERTY = new MaterialProperty();
        register(MATERIAL_PROPERTY);

        REPAIR_COST_PROPERTY = new RepairCostProperty();
        register(REPAIR_COST_PROPERTY);

        UNBREAKABLE_PROPERTY = new UnbreakableProperty();
        register(UNBREAKABLE_PROPERTY);

        UNSTACKABLE_PROPERTY = new UnstackableProperty();
        register(UNSTACKABLE_PROPERTY);
    }

    private PropertyRegistry() {

    }

    public static void register(ItemProperty<?> property) {
        REGISTERED_PROPERTIES.put(property.getKey(), property);
    }

    public static @Nullable ItemProperty<?> getRegisteredProperty(Key key) {
        return REGISTERED_PROPERTIES.get(key);
    }

    /**
     * Dummy method to initialize the class, does nothing.
     */
    public static void initializeRegistry() {
    }
}
