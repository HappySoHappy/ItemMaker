package me.howandev.itemmaker.item.property.impl;

import me.howandev.itemmaker.ItemMakerConstants;
import me.howandev.itemmaker.item.property.ItemMapProperty;
import me.howandev.itemmaker.item.property.ItemProperty;
import me.howandev.itemmaker.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class EnchantmentProperty extends ItemMapProperty<Enchantment, Integer> {
    public EnchantmentProperty() {
        super("enchantment");
    }

    @Override
    public boolean isApplicable(final @NotNull ItemStack itemStack) {
        return itemStack.getType().isItem();
    }

    @Override
    public @NotNull ItemStack apply(final @NotNull ItemStack itemStack, final @NotNull Map<Enchantment, Integer> value) {
        itemStack.addUnsafeEnchantments(value);
        return itemStack;
    }

    @Override
    public @NotNull ItemStack parseThenApply(final @NotNull ItemStack itemStack, final @NotNull Object value) throws IllegalArgumentException {
        List<String> stringEnchantments = new ArrayList<>();
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        if (value instanceof String stringValue) {
            stringEnchantments = Arrays.asList(ItemMakerConstants.NEW_LINE_PATTERN.split(stringValue));
        }

        if (value instanceof List<?> listValue) {
            stringEnchantments = listValue.stream().map(Object::toString).collect(Collectors.toList());
        }

        for (String rawEnchantment : stringEnchantments) {
            Matcher matcher = ItemMakerConstants.ENCHANTMENT_PATTERN.matcher(rawEnchantment);
            if (matcher.find()) {
                String levelGroup = matcher.group("level");
                Integer level = (levelGroup == null || levelGroup.isEmpty()) ? 1 : Integer.parseInt(levelGroup);

                String enchantmentGroup = matcher.group("enchantment");

                String namespaceGroup = matcher.group("namespace");
                if (namespaceGroup == null || namespaceGroup.isEmpty()) {
                    Enchantment enchantment = getEnchantment(enchantmentGroup);
                    enchantments.put(enchantment, level);
                    continue;
                }

                Enchantment enchantment = getEnchantment(NamespacedKey.fromString(enchantmentGroup, Bukkit.getPluginManager().getPlugin(namespaceGroup)));
                enchantments.put(enchantment, level);
            }
        }

        return apply(itemStack, enchantments);
    }

    @Override
    public boolean isRemovable(final @NotNull ItemStack itemStack) {
        return itemStack.getEnchantments().size() > 0;
    }

    @Override
    public @NotNull ItemStack remove(final @NotNull ItemStack itemStack) {
        for (Enchantment enchantment : itemStack.getEnchantments().keySet()) {
            itemStack.removeEnchantment(enchantment);
        }

        return itemStack;
    }

    //TODO: cancer make this better
    @Override
    public @NotNull ItemStack remove(final @NotNull ItemStack itemStack, final int index) {
        Enchantment enchantment = new LinkedList<>(itemStack.getEnchantments().keySet()).get(index);
        if (enchantment != null)
            itemStack.removeEnchantment(enchantment);

        return itemStack;
    }

    private @Nullable Enchantment getEnchantment(final NamespacedKey key) {
        return Enchantment.getByKey(key);
    }

    private @Nullable Enchantment getEnchantment(final String enchantmentName) {
        for (Enchantment enchantment : Enchantment.values()) {
            if (enchantment.getKey().getKey().equalsIgnoreCase(enchantmentName))
                return enchantment;
        }

        return Enchantment.getByKey(NamespacedKey.fromString(enchantmentName));
    }
}
