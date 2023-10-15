package me.howandev.itemmaker.item.property.impl.meta;

import me.howandev.itemmaker.item.property.ItemProperty;
import me.howandev.itemmaker.key.Key;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class DamageProperty extends ItemProperty<Integer> {
    public DamageProperty() {
        super("damage");
    }

    @Override
    public boolean isApplicable(final @NotNull ItemStack itemStack) {
        return itemStack.getItemMeta() instanceof Damageable;
    }

    @Override
    public @NotNull ItemStack apply(final @NotNull ItemStack itemStack, final @NotNull Integer value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof Damageable damageableMeta)
            damageableMeta.setDamage(value);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    @Override
    public @NotNull ItemStack parseThenApply(final @NotNull ItemStack itemStack, final @NotNull Object value) throws IllegalArgumentException {
        int damage = 0;
        if (value instanceof Number numberValue)
            damage = numberValue.intValue();

        if (value instanceof String stringValue) {
            try {
                damage = Integer.parseInt(stringValue);
            } catch (NumberFormatException ignored) { }
        }

        return apply(itemStack, damage);
    }

    @Override
    public boolean isRemovable(final @NotNull ItemStack itemStack) {
        return itemStack.getItemMeta() instanceof Damageable damageableMeta && damageableMeta.hasDamage();
    }

    @Override
    public @NotNull ItemStack remove(final @NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof Damageable damageableMeta)
            damageableMeta.setDamage(0);
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
