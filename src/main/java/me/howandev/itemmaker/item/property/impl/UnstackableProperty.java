package me.howandev.itemmaker.item.property.impl;

import me.howandev.itemmaker.item.property.ItemProperty;
import me.howandev.itemmaker.nbt.editor.NBTEditor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

// note: I'm not sure if this should be a property
// perhaps we shouuld use timed attribute and check inventories for dupes
public class UnstackableProperty extends ItemProperty<Boolean> {

    public UnstackableProperty() {
        super("unstackable");
    }

    /**
     * @apiNote for all purposes, {@link UUID#randomUUID()} is good enough, here
     */
    @Override
    public @NotNull ItemStack apply(@NotNull ItemStack itemStack, @NotNull Boolean value) {
        if (!value) return itemStack;

        return NBTEditor.create(itemStack).setUUID(getKey().toString(), UUID.randomUUID()).build();
    }

    @Override
    public @NotNull ItemStack parseThenApply(@NotNull ItemStack itemStack, @NotNull Object value) throws IllegalArgumentException {
        boolean unstackable = false;
        if (value instanceof Boolean booleanValue)
            unstackable = booleanValue;

        if (value instanceof String stringValue)
            unstackable = Boolean.parseBoolean(stringValue);

        return apply(itemStack, unstackable);
    }

    @Override
    public boolean isRemovable(@NotNull ItemStack itemStack) {
        return NBTEditor.create(itemStack).has(getKey().toString());
    }

    @Override
    public @NotNull ItemStack remove(@NotNull ItemStack itemStack) {
        return NBTEditor.create(itemStack).remove(getKey().toString()).build();
    }
}
