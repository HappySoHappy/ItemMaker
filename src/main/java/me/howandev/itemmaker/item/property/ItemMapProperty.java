package me.howandev.itemmaker.item.property;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public abstract class ItemMapProperty<T, U> extends ItemProperty<Map<T, U>> {
    public ItemMapProperty(@NotNull String key) {
        super(key);
    }

    public abstract @NotNull ItemStack remove(@NotNull ItemStack itemStack, int index);
}
