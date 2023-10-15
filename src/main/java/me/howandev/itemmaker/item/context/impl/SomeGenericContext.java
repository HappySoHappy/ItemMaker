package me.howandev.itemmaker.item.context.impl;

import me.howandev.itemmaker.item.context.ItemContext;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SomeGenericContext extends ItemContext {
    public SomeGenericContext() {
        super("context-generic");
    }

    @Override
    public @NotNull ItemStack apply(@NotNull ItemStack itemStack) {
        return set(itemStack, "valueadqadsasad");
    }

    @Override
    public @NotNull ItemStack parseThenApply(@NotNull ItemStack itemStack, @NotNull Object value) throws IllegalArgumentException {
        return itemStack;
    }
}
