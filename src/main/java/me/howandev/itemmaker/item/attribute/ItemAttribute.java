package me.howandev.itemmaker.item.attribute;

import me.howandev.itemmaker.key.Key;
import me.howandev.itemmaker.key.KeyHolder;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * ItemAttribute is a value that is stored in Item's NBT data and is used to attach generic behaviour (code)
 * to the Item.
 */
public abstract class ItemAttribute<T> implements KeyHolder {
    public static final String ATTRIBUTE_SECTION = "attributes";
    private final Key key;
    public ItemAttribute(final @NotNull String key) {
        this.key = Key.of(key);
    }

    public abstract @NotNull ItemStack apply(final @NotNull ItemStack itemStack);

    @Override
    public @NotNull Key getKey() {
        return key;
    }
}
