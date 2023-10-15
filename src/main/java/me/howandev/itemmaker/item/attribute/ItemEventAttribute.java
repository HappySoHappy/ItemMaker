package me.howandev.itemmaker.item.attribute;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ItemEventAttribute extends ItemAttribute<Void> implements Listener {
    public ItemEventAttribute(@NotNull String key) {
        super(key);
    }

    @Override
    public final @NotNull ItemStack apply(@NotNull ItemStack itemStack) {
        return itemStack;
    }
}
