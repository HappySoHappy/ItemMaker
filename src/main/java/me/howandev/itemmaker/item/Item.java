package me.howandev.itemmaker.item;

import lombok.Getter;
import lombok.Setter;
import me.howandev.itemmaker.configuration.impl.Configuration;
import me.howandev.itemmaker.item.property.ItemProperty;
import me.howandev.itemmaker.key.Key;
import me.howandev.itemmaker.key.KeyHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public abstract class Item implements KeyHolder {
    protected final @NotNull Map<ItemProperty<?>, Object> properties = new LinkedHashMap<>();
    private final @NotNull String id;
    @Setter
    private int revision = 0;
    public Item(final @NotNull String id) {
        this.id = id;
    }

    public abstract void loadConfiguration(final @NotNull Configuration configuration);

    public void addProperty(final @NotNull ItemProperty<?> property, final Object value) {
        properties.put(property, value);
    }

    public Map<ItemProperty<?>, Object> getProperties() {
        return Collections.unmodifiableMap(properties);
    }

    public boolean hasProperty(ItemProperty<?> property) {
        return properties.containsKey(property);
    }

    public abstract @NotNull ItemStack asItemStack();

    @Override
    public @NotNull Key getKey() {
        return Key.of(id);
    }
}
