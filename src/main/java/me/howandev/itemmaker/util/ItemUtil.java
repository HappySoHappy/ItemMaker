package me.howandev.itemmaker.util;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.List;

//TODO: should be part of item editor / builder
@Deprecated(forRemoval = true)
public class ItemUtil {
    /**
     * Uses reflection to directly write to displayName field instead of relying on Bukkit methods.
     * This in turn, allows use of JSON formatting outside Paper platform.
     *
     * @param itemStack Item to modify
     * @param displayName Json text
     * @return modified item
     */
    public static ItemStack setDisplayName(ItemStack itemStack, String displayName) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return itemStack;

        try {
            Field displayNameField = getCraftMetaField(itemMeta, "displayName");
            displayNameField.setAccessible(true);
            displayNameField.set(itemMeta, displayName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    /**
     * Uses reflection to directly write to lore field instead of relying on Bukkit methods.
     * This in turn, allows use of JSON formatting outside Paper platform.
     *
     * @param itemStack Item to modify
     * @param lore Json text
     * @return modified item
     */
    public static ItemStack setLore(ItemStack itemStack, List<String> lore) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return itemStack;

        try {
            Field loreField = getCraftMetaField(itemMeta, "lore");
            loreField.setAccessible(true);
            loreField.set(itemMeta, lore);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }

    //Ensure that we are reading from CraftMetaItem, and not for example: LeatherArmorMeta (which doesn't have displayName)
    private static @NotNull Field getCraftMetaField(ItemMeta itemMeta, String fieldName) throws NoSuchFieldException {
        Class<?> clazz = itemMeta.getClass();
        while (clazz != Object.class) {
            if (clazz.getName().endsWith("CraftMetaItem")) {
                try {
                    return clazz.getDeclaredField(fieldName);
                } catch (Exception ex) {
                    continue;
                }
            }
            clazz = clazz.getSuperclass();
        }

        throw new NoSuchFieldException("Failed to read field from CraftMetaItem, field '" + fieldName + "' does not exist!");
    }
}
