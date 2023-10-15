package me.howandev.itemmaker.item.property.impl;

import me.howandev.itemmaker.item.property.ItemProperty;
import me.howandev.itemmaker.key.Key;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class CustomModelProperty extends ItemProperty<Integer> {
    public CustomModelProperty() {
        super("custom-model");
    }

    @Override
    public @NotNull ItemStack apply(final @NotNull ItemStack itemStack, final @NotNull Integer value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null)
            meta.setCustomModelData(value);
        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public @NotNull ItemStack parseThenApply(final @NotNull ItemStack itemStack, final @NotNull Object value) throws IllegalArgumentException {
        Integer model = null;
        if (value instanceof Number numberValue)
            model = numberValue.intValue();

        if (model == null)
            try {
                model = Integer.parseInt(value.toString());
            } catch (NumberFormatException ex) {
                throw new IllegalArgumentException(ex);
            }

        return apply(itemStack, model);
    }

    @Override
    public boolean isRemovable(final @NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        return meta != null && meta.hasCustomModelData();
    }

    @Override
    public @NotNull ItemStack remove(final @NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null)
            meta.setCustomModelData(null);
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
