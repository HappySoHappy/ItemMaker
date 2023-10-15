package me.howandev.itemmaker.item.property.impl;

import me.howandev.itemmaker.item.property.ItemProperty;
import me.howandev.itemmaker.key.Key;
import me.howandev.itemmaker.text.TextParser;
import me.howandev.itemmaker.util.ItemUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class DisplayNameProperty extends ItemProperty<String> {
    public DisplayNameProperty() {
        super("display-name");
    }

    @Override
    public @NotNull ItemStack apply(final @NotNull ItemStack itemStack, final @NotNull String value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null)
            meta.setDisplayName(value);
        itemStack.setItemMeta(meta);

        return ItemUtil.setDisplayName(itemStack, TextParser.json(value));
    }

    @Override
    public @NotNull ItemStack parseThenApply(final @NotNull ItemStack itemStack, final @NotNull Object value) throws IllegalArgumentException {
        return apply(itemStack, value.toString());
    }

    @Override
    public boolean isRemovable(final @NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        return meta != null && meta.hasDisplayName();
    }

    @Override
    public @NotNull ItemStack remove(final @NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null)
            meta.setDisplayName(null);
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
