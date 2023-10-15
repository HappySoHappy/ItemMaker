package me.howandev.itemmaker.item.context;

import me.howandev.itemmaker.key.Key;
import me.howandev.itemmaker.key.KeyHolder;
import me.howandev.itemmaker.nbt.NBTCompound;
import me.howandev.itemmaker.nbt.editor.NBTEditor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Item context is a key:value store of an item
 * it is used by external plugins to add functionality to items
 * such example would be custom enchantments:
 * (key:value) Supercharged:4
 * this would result in following nbt added to item:
 * ItemMaker: {
 *     Context: {
 *         "Supercharged": 4
 *     }
 * }
 */
// contexts may be applied in the item file, hence CONTEXT_SECTION
// also contexts are stored in a NBTCompound named by CONTEXT_SECTION
// but generally plugins should do that on their own
public abstract class ItemContext implements KeyHolder {

    public static final String CONTEXT_SECTION = "contexts";
    private final Key key;
    public ItemContext(final @NotNull String key) {
        this.key = Key.of(key);
    }

    public abstract @NotNull ItemStack apply(final @NotNull ItemStack itemStack);

    public abstract @NotNull ItemStack parseThenApply(@NotNull ItemStack itemStack, @NotNull Object value) throws IllegalArgumentException;

    public @Nullable <T> T get(final @NotNull ItemStack itemStack) {
        NBTEditor editor = NBTEditor.create(itemStack);

        NBTCompound compound = editor.get(CONTEXT_SECTION + "." + this.key, null);
        if (compound != null)
            return compound.getValue("v");

        return null;
    }

    public <T> @NotNull ItemStack set(final @NotNull ItemStack itemStack, T value) {
        NBTEditor editor = NBTEditor.create(itemStack);
        NBTCompound compound = new NBTCompound();
        compound.set("v", value);
        editor.set(CONTEXT_SECTION + "." + this.key, compound);
        return editor.build();
    }

    public @NotNull ItemStack remove(final @NotNull ItemStack itemStack) {
        NBTEditor editor = NBTEditor.create(itemStack);
        editor.remove(CONTEXT_SECTION + "." + this.key);
        return editor.build();
    }

    @Override
    public @NotNull Key getKey() {
        return key;
    }
}
