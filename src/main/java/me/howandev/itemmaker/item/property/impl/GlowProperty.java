package me.howandev.itemmaker.item.property.impl;

import me.howandev.itemmaker.item.property.ItemProperty;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

//private Map<Enchantment, Integer> enchantments; in CraftMetaItem.class
// null if no enchantments
//TODO: add proper enchantment checks, this is very basic implementation
@Deprecated
public class GlowProperty extends ItemProperty<Boolean> {
    public GlowProperty() {
        super("glow");
    }

    @Override
    public @NotNull ItemStack apply(final @NotNull ItemStack itemStack, final @NotNull Boolean value) {
        //So, the best way would be to apply empty {Enchantments:{}} Compound to the item
        // this would result in nice non-lore-showing item glow
        itemStack.addUnsafeEnchantment(Enchantment.LURE, 0);
        return itemStack;
    }

    @Override
    public @NotNull ItemStack parseThenApply(final @NotNull ItemStack itemStack, final @NotNull Object value) throws IllegalArgumentException {
        boolean glow = false;
        if (value instanceof Boolean booleanValue)
            glow = booleanValue;

        if (value instanceof String stringValue)
            glow = Boolean.parseBoolean(stringValue);

        return apply(itemStack, glow);
    }

    @Override
    public boolean isRemovable(final @NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        // enchants != null || !enchants.isEmpty()
        return meta != null && meta.hasEnchants();
    }

    @Override
    public @NotNull ItemStack remove(final @NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null)
            meta.setUnbreakable(false);
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
