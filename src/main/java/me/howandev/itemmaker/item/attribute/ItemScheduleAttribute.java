package me.howandev.itemmaker.item.attribute;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public abstract class ItemScheduleAttribute extends ItemAttribute<Void> {
    private final long periodMillis;
    public ItemScheduleAttribute(@NotNull String key, long periodMillis) {
        super(key);
        this.periodMillis = periodMillis;
    }

    @Override
    public final @NotNull ItemStack apply(@NotNull ItemStack itemStack) {
        return itemStack;
    }

    public long getPeriodMillis() {
        return periodMillis;
    }

    public abstract void execute();
}
