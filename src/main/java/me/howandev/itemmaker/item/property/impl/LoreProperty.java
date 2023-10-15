package me.howandev.itemmaker.item.property.impl;

import me.howandev.itemmaker.ItemMakerConstants;
import me.howandev.itemmaker.item.property.ItemListProperty;
import me.howandev.itemmaker.key.Key;
import me.howandev.itemmaker.text.TextParser;
import me.howandev.itemmaker.util.ItemUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class LoreProperty extends ItemListProperty<String> {
    public LoreProperty() {
        super("lore");
    }

    @Override
    public @NotNull ItemStack apply(final @NotNull ItemStack itemStack, final @NotNull List<String> value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null)
            meta.setLore(value.isEmpty() ? null : value);
        itemStack.setItemMeta(meta);

        List<String> jsonLore = new LinkedList<>();
        for (String rawLore : value) {
            jsonLore.add(TextParser.json(rawLore));
        }

        return ItemUtil.setLore(itemStack, jsonLore);
    }

    @Override
    public @NotNull ItemStack parseThenApply(final @NotNull ItemStack itemStack, final @NotNull Object value) throws IllegalArgumentException {
        List<String> lore = new ArrayList<>();
        if (value instanceof List<?> listValue) {
            lore = listValue.stream().map(Object::toString).collect(Collectors.toList());
        }

        if (lore.isEmpty()) {
            lore = Arrays.asList(ItemMakerConstants.NEW_LINE_PATTERN.split(value.toString()));
        }

        return apply(itemStack, lore);
    }


    @Override
    public boolean isRemovable(final @NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        return meta != null && meta.hasLore();
    }

    @Override
    public @NotNull ItemStack remove(final @NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null)
            meta.setLore(null);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    @Override
    public @NotNull ItemStack remove(final @NotNull ItemStack itemStack, final int index) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null && meta.hasLore()) {
            List<String> lore = meta.getLore();
            if (lore != null)
                lore.remove(index);

            meta.setLore(lore);
        }

        itemStack.setItemMeta(meta);
        return itemStack;
    }
}
