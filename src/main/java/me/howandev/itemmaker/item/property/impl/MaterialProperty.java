package me.howandev.itemmaker.item.property.impl;

import me.howandev.itemmaker.item.property.ItemProperty;
import me.howandev.itemmaker.key.Key;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class MaterialProperty extends ItemProperty<Material> {
    public MaterialProperty() {
        super("material");
    }

    @Override
    public boolean isApplicable(final @NotNull ItemStack itemStack) {
        return true; //Any ItemStack may have their Material changed
    }

    @Override
    public @NotNull ItemStack apply(final @NotNull ItemStack itemStack, final @NotNull Material value) {
        itemStack.setType(value);
        return itemStack;
    }

    @Override
    public @NotNull ItemStack parseThenApply(final @NotNull ItemStack itemStack, final @NotNull Object value) throws IllegalArgumentException {
        Material material = Material.valueOf(value.toString().toUpperCase(Locale.ROOT));
        return apply(itemStack, material);
    }

    @Override
    public boolean isRemovable(final @NotNull ItemStack itemStack) {
        return false; //Obviously we cannot remove Material from an ItemStack
    }

    @Override
    public @NotNull ItemStack remove(final @NotNull ItemStack itemStack) {
        throw new IllegalStateException("Cannot remove Material from ItemStack!");
    }
}
