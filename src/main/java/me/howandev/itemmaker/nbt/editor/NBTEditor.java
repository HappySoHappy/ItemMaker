package me.howandev.itemmaker.nbt.editor;

import me.howandev.itemmaker.ItemMakerConstants;
import me.howandev.itemmaker.nbt.NBTCompound;
import me.howandev.itemmaker.nbt.NBTCompoundDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

//FIXME: CRITICAL: ConcurrentModificationException NBTEditor will randomly erase item data if this happens
// happens when editing items instantly after player joins, see RainbowLeatherAttribute
@SuppressWarnings("unused")
public class NBTEditor {
    public static NBTEditor create(ItemStack itemStack) {
        return new NBTEditor(itemStack);
    }

    public static NBTEditor create(ItemStack itemStack, NamespacedKey key) {
        return new NBTEditor(itemStack, key);
    }

    private final ItemStack itemStack;
    private final NamespacedKey key;
    private NBTEditor(ItemStack itemStack) {
        if (itemStack.getItemMeta() == null)
            throw new IllegalArgumentException("Provided ItemStack does not contain ItemMeta: " + itemStack.getType());

        this.itemStack = itemStack;
        this.key = ItemMakerConstants.CONTAINER_KEY;
    }

    private NBTEditor(ItemStack itemStack, NamespacedKey key) {
        if (itemStack.getItemMeta() == null)
            throw new IllegalArgumentException("Provided ItemStack does not contain ItemMeta: " + itemStack.getType());

        this.itemStack = itemStack;
        this.key = key;
    }

    public Byte getByte(@NotNull String key) {
        return get(key, null);
    }
    public Byte getByte(@NotNull String key, Byte def) {
        return get(key, def);
    }
    @Contract("_, _ -> this")
    public NBTEditor setByte(@NotNull String key, @Nullable Byte value) {
        return set(key, value);
    }

    public Byte[] getByteArray(@NotNull String key) {
        return get(key, null);
    }
    public Byte[] getByteArray(@NotNull String key, Byte[] def) {
        return get(key, def);
    }
    @Contract("_, _ -> this")
    public NBTEditor setByteArray(@NotNull String key, @Nullable Byte[] value) {
        return set(key, value);
    }

    public Double getDouble(@NotNull String key) {
        return get(key, null);
    }
    public Double getDouble(@NotNull String key, Double def) {
        return get(key, def);
    }
    @Contract("_, _ -> this")
    public NBTEditor setDouble(@NotNull String key, @Nullable Double value) {
        return set(key, value);
    }

    public Float getFloat(@NotNull String key) {
        return get(key, null);
    }
    public Float getFloat(@NotNull String key, Float def) {
        return get(key, def);
    }
    @Contract("_, _ -> this")
    public NBTEditor setFloat(@NotNull String key, @Nullable Float value) {
        return set(key, value);
    }

    public Integer getInt(@NotNull String key) {
        return get(key, null);
    }
    public Integer getInt(@NotNull String key, Integer def) {
        return get(key, def);
    }
    @Contract("_, _ -> this")
    public NBTEditor setInt(@NotNull String key, @Nullable Integer value) {
        return set(key, value);
    }

    public Integer[] getIntArray(@NotNull String key) {
        return get(key, null);
    }
    public Integer[] getIntArray(@NotNull String key, Integer[] def) {
        return get(key, def);
    }
    @Contract("_, _ -> this")
    public NBTEditor setIntArray(@NotNull String key, @Nullable Integer[] value) {
        return set(key, value);
    }

    public List<?> getList(@NotNull String key) {
        return get(key, null);
    }
    public List<?> getList(@NotNull String key, List<?> def) {
        return get(key, def);
    }
    @Contract("_, _ -> this")
    public NBTEditor setList(@NotNull String key, @Nullable List<?> value) {
        return set(key, value);
    }

    public Long getLong(@NotNull String key) {
        return get(key, null);
    }
    public Long getLong(@NotNull String key, Long def) {
        return get(key, def);
    }
    @Contract("_, _ -> this")
    public NBTEditor setLong(@NotNull String key, @Nullable Long value) {
        return set(key, value);
    }

    public Long[] getLongArray(@NotNull String key) {
        return get(key, null);
    }
    public Long[] getLongArray(@NotNull String key, Long[] def) {
        return get(key, def);
    }
    @Contract("_, _ -> this")
    public NBTEditor setLongArray(@NotNull String key, @Nullable Long[] value) {
        return set(key, value);
    }

    public Short getShort(@NotNull String key) {
        return get(key, null);
    }
    public Short getShort(@NotNull String key, Short def) {
        return get(key, def);
    }
    @Contract("_, _ -> this")
    public NBTEditor setShort(@NotNull String key, @Nullable Short value) {
        return set(key, value);
    }

    public String getString(@NotNull String key) {
        return get(key, null);
    }
    public String getString(@NotNull String key, String def) {
        return get(key, def);
    }
    @Contract("_, _ -> this")
    public NBTEditor setString(@NotNull String key, @Nullable String value) {
        return set(key, value);
    }

    public UUID getUUID(@NotNull String key) {
        return get(key, null);
    }
    public UUID getUUID(@NotNull String key, UUID def) {
        return get(key, def);
    }
    @Contract("_, _ -> this")
    public NBTEditor setUUID(@NotNull String key, @Nullable UUID value) {
        return set(key, value);
    }

    public NBTCompound getCompound() {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return null;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.get(this.key, NBTCompoundDataType.INSTANCE);
    }

    @Contract("_ -> this")
    public NBTEditor setCompound(@NotNull NBTCompound value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return this;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(this.key, NBTCompoundDataType.INSTANCE, value);
        itemStack.setItemMeta(meta);

        return this;
    }

    public boolean has(@NotNull String key) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return false;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        NBTCompound compound = pdc.get(this.key, NBTCompoundDataType.INSTANCE);
        if (compound == null)
            return false;

        return pdc.has(this.key, NBTCompoundDataType.INSTANCE);
    }

    @Contract("_ -> this")
    public NBTEditor remove(@NotNull String key) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return this;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        NBTCompound compound = pdc.get(this.key, NBTCompoundDataType.INSTANCE);
        if (compound == null)
            return this;

        pdc.remove(this.key);
        itemStack.setItemMeta(meta);

        return this;
    }

    public ItemStack build() {
        return itemStack;
    }

    public <T> T get(String key, T def) {
        // Only null if Material == AIR
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return def;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        NBTCompound compound = pdc.get(this.key, NBTCompoundDataType.INSTANCE);
        if (compound == null)
            return def;

        T value = compound.getValue(key);
        return value != null ? value : def;
    }

    @Contract("_, _ -> this")
    public <T> NBTEditor set(String key, T value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return this;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        NBTCompound compound = pdc.get(this.key, NBTCompoundDataType.INSTANCE);
        if (compound == null)
            compound = new NBTCompound();

        compound.set(key, value);

        pdc.set(this.key, NBTCompoundDataType.INSTANCE, compound);
        itemStack.setItemMeta(meta);

        return this;
    }

    @Contract("_, _ -> this")
    private NBTEditor set(String key, UUID value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta == null)
            return this;

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        NBTCompound compound = pdc.get(this.key, NBTCompoundDataType.INSTANCE);
        if (compound == null)
            compound = new NBTCompound();

        compound.setUUID(key, value);

        pdc.set(this.key, NBTCompoundDataType.INSTANCE, compound);
        itemStack.setItemMeta(meta);

        return this;
    }
}
