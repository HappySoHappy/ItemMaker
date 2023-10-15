package me.howandev.itemmaker.item.property;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class ItemListProperty<T> extends ItemProperty<List<T>> {
    public ItemListProperty(@NotNull String key) {
        super(key);
    }

    public abstract @NotNull ItemStack remove(final @NotNull ItemStack itemStack, final int index);
}
