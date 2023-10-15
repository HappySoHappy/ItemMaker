package me.howandev.itemmaker.item.property;

import me.howandev.itemmaker.key.Key;
import me.howandev.itemmaker.key.KeyHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * ItemProperty is a value that is stored in Item's file and is used to create a Bukkit ItemStack,
 * it represents the most basic, yet essential information about the Item.
 *
 * @param <T> generic type for what this property is holding
 */
//TODO: Use ItemEditor to modify items instead of doing all of that coding 2 times DRY
public abstract class ItemProperty<T> implements KeyHolder {
    public static final String PROPERTY_SECTION = "properties";
    private final Key key;
    public ItemProperty(final @NotNull String key) {
        this.key = Key.of(key);
    }

    public boolean isApplicable(final @NotNull ItemStack itemStack) {
        return itemStack.getType().isItem() && itemStack.getItemMeta() != null;
    }
    public abstract @NotNull ItemStack apply(final @NotNull ItemStack itemStack, final @NotNull T value);
    public abstract @NotNull ItemStack parseThenApply(final @NotNull ItemStack itemStack, final @NotNull Object value) throws IllegalArgumentException;
    public abstract boolean isRemovable(final @NotNull ItemStack itemStack);
    public abstract @NotNull ItemStack remove(final @NotNull ItemStack itemStack);

    @Override
    public @NotNull Key getKey() {
        return key;
    }
}
