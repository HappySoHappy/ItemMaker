package me.howandev.itemmaker.item.property.impl.meta;

import me.howandev.itemmaker.item.property.ItemProperty;
import me.howandev.itemmaker.key.Key;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.jetbrains.annotations.NotNull;

public class RepairCostProperty extends ItemProperty<Integer> {
    public RepairCostProperty() {
        super("repair-cost");
    }

    @Override
    public boolean isApplicable(final @NotNull ItemStack itemStack) {
        return itemStack.getItemMeta() instanceof Repairable;
    }

    @Override
    public @NotNull ItemStack apply(final @NotNull ItemStack itemStack, final @NotNull Integer value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof Repairable repairableMeta)
            repairableMeta.setRepairCost(value);
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    @Override
    public @NotNull ItemStack parseThenApply(final @NotNull ItemStack itemStack, final @NotNull Object value) throws IllegalArgumentException {
        int repairCost = 0;
        if (value instanceof Number numberValue)
            repairCost = numberValue.intValue();

        if (value instanceof String stringValue) {
            try {
                repairCost = Integer.parseInt(stringValue);
            } catch (NumberFormatException ignored) { }
        }

        return apply(itemStack, repairCost);
    }

    @Override
    public boolean isRemovable(final @NotNull ItemStack itemStack) {
        return itemStack.getItemMeta() instanceof Repairable repairableMeta && repairableMeta.hasRepairCost();
    }

    @Override
    public @NotNull ItemStack remove(final @NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof Repairable repairableMeta)
            repairableMeta.setRepairCost(0);
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
