package me.howandev.itemmaker.item.property.impl.meta;

import me.howandev.itemmaker.ItemMakerConstants;
import me.howandev.itemmaker.item.property.ItemProperty;
import me.howandev.itemmaker.key.Key;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Matcher;

public class ColorProperty extends ItemProperty<Color> {
    public ColorProperty() {
        super("color");
    }

    @Override
    public boolean isApplicable(final @NotNull ItemStack itemStack) {
        return itemStack.getItemMeta() instanceof LeatherArmorMeta;
    }

    @Override
    public @NotNull ItemStack apply(final @NotNull ItemStack itemStack, final @NotNull Color value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
            leatherArmorMeta.setColor(value);
        }
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    @Override
    public @NotNull ItemStack parseThenApply(final @NotNull ItemStack itemStack, final @NotNull Object value) throws IllegalArgumentException {
        Color color = null;
        if (value instanceof Color colorValue) {
            color = colorValue;
        }

        if (value instanceof Number numberValue) {
            color = Color.fromRGB(numberValue.intValue());
        }

        if (value instanceof List<?> listValue && listValue.size() >= 2) {
            int red = Integer.parseInt(listValue.get(0).toString());
            int green = Integer.parseInt(listValue.get(1).toString());
            int blue = Integer.parseInt(listValue.get(2).toString());
            color = Color.fromRGB(red, green, blue);
        }

        if (value instanceof String stringValue) {
            Matcher hexMatcher = ItemMakerConstants.HEX_PATTERN.matcher(stringValue);
            if (hexMatcher.find()) {
                int red = Integer.parseInt(hexMatcher.group("red"), 16);
                int green = Integer.parseInt(hexMatcher.group("green"), 16);
                int blue = Integer.parseInt(hexMatcher.group("blue"), 16);
                color = Color.fromRGB(red, green, blue);
            }
        }

        if (color == null) throw new IllegalArgumentException("Unable to parse a color!");

        return apply(itemStack, color);
    }

    @Override
    public boolean isRemovable(final @NotNull ItemStack itemStack) {
        return itemStack.getItemMeta() instanceof LeatherArmorMeta;
    }

    @Override
    public @NotNull ItemStack remove(final @NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta instanceof LeatherArmorMeta leatherArmorMeta) {
            leatherArmorMeta.setColor(null);
        }
        itemStack.setItemMeta(meta);

        return itemStack;
    }
}
