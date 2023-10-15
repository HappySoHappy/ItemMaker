package me.howandev.itemmaker.item.property.impl;

import me.howandev.itemmaker.item.property.ItemProperty;
import me.howandev.itemmaker.key.Key;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class UnbreakableProperty extends ItemProperty<Boolean> {
    public UnbreakableProperty() {
        super("unbreakable");
    }

    @Override
    public @NotNull ItemStack apply(final @NotNull ItemStack itemStack, final @NotNull Boolean value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null)
            meta.setUnbreakable(value);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public @NotNull ItemStack parseThenApply(final @NotNull ItemStack itemStack, final @NotNull Object value) throws IllegalArgumentException {
        boolean unbreakable = false;
        if (value instanceof Boolean booleanValue)
            unbreakable = booleanValue;

        if (value instanceof String stringValue)
            unbreakable = Boolean.parseBoolean(stringValue);

        return apply(itemStack, unbreakable);
    }

    @Override
    public boolean isRemovable(final @NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        return meta != null && meta.isUnbreakable();
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
