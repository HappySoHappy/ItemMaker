package me.howandev.itemmaker.item.property.impl;

import me.howandev.itemmaker.item.property.ItemListProperty;
import me.howandev.itemmaker.key.Key;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class FlagProperty extends ItemListProperty<ItemFlag> {
    public FlagProperty() {
        super("flag");
    }

    @Override
    public @NotNull ItemStack apply(final @NotNull ItemStack itemStack, final @NotNull List<ItemFlag> value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null)
            for (ItemFlag flag : value)
                meta.addItemFlags(flag);

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    //todo: nicer parsing cause this is cancer
    @Override
    public @NotNull ItemStack parseThenApply(final @NotNull ItemStack itemStack, final @NotNull Object value) throws IllegalArgumentException {
        List<ItemFlag> flags = new ArrayList<>();
        if (value instanceof List<?> listValue) {
            for (Object objFlag : listValue)
                flags.add(ItemFlag.valueOf(objFlag.toString().toUpperCase(Locale.ROOT)));
        }

        if (flags.isEmpty()) {
            flags.add(ItemFlag.valueOf(value.toString().toUpperCase(Locale.ROOT)));
        }

        return apply(itemStack, flags);
    }

    @Override
    public boolean isRemovable(final @NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        return meta != null && !meta.getItemFlags().isEmpty();
    }

    @Override
    public @NotNull ItemStack remove(final @NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null)
            meta.removeItemFlags(ItemFlag.values());

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    @Override
    public @NotNull ItemStack remove(final @NotNull ItemStack itemStack, final int index) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            List<ItemFlag> flags = new LinkedList<>(meta.getItemFlags());
            if (index >= 0 && index < flags.size()) {
                meta.removeItemFlags(flags.get(index));
            }
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
