package me.howandev.itemmaker.item.attribute.impl;

import me.howandev.itemmaker.ItemMakerPlugin;
import me.howandev.itemmaker.item.SimpleItem;
import me.howandev.itemmaker.item.attribute.ItemScheduleAttribute;
import me.howandev.itemmaker.nbt.editor.NBTEditor;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.ConcurrentModificationException;

public class RainbowLeatherAttribute extends ItemScheduleAttribute {
    private float cursor = 0;
    private final int smoothness = 20;
    //TODO: allow items to specify what colors they have
    private final Color[] colors = new Color[]{
            Color.LIME,
            Color.GREEN,
            Color.fromRGB(0x004400)
    };
    // Inventory Revision ID is an int, this means that with rate of 10 updates-per-second
    // it will overflow in 60 hours
    public RainbowLeatherAttribute() {
        super("rainbow-leather", 100);
    }

    @Override
    public void execute() {
            Bukkit.getOnlinePlayers().forEach(player -> {
                PlayerInventory inventory = player.getInventory();
                Color color = getGradient(colors, smoothness, cursor);

                setLeatherColor(inventory.getHelmet(), color);
                setLeatherColor(inventory.getChestplate(), color);
                setLeatherColor(inventory.getLeggings(), color);
                setLeatherColor(inventory.getBoots(), color);
            });

            cursor += 1.0f / smoothness;
            cursor %= 1.0f;
            //FIXME: CRITICAL: ConcurrentModificationException NBTEditor will randomly erase item data if this happens
    }

    //TODO: i don't know what happens when MinecraftClient.player.currentScreenHandler.revision reaches Integer.MAX_VALUE
    private void setLeatherColor(ItemStack itemStack, Color color) {
        if (itemStack != null) {
            if (!(itemStack.getItemMeta() instanceof LeatherArmorMeta leatherArmorMeta))
                return;

            // using equals() just to be sure
            if (leatherArmorMeta.getColor().equals(color))
                return;

            String itemId = NBTEditor.create(itemStack).getString("id");
            if (itemId == null)
                return;

            SimpleItem item = (SimpleItem) ItemMakerPlugin.instance().getItemManager().getItem(itemId);
            if (item == null)
                return;

            if (!item.hasAttribute(this))
                return;

            leatherArmorMeta.setColor(color);
            itemStack.setItemMeta(leatherArmorMeta);
        }
    }

    private Color getGradient(Color[] colors, int smoothnessAmount, float cursor) {
        if (colors == null || colors.length < 2 || smoothnessAmount < 1 || cursor < 0 || cursor > 1) {
            throw new IllegalArgumentException("Invalid input parameters");
        }

        int totalColors = colors.length;

        int currentColorIndex = (int) (cursor * totalColors) % totalColors;
        int nextColorIndex = (currentColorIndex + 1) % totalColors;

        float lerpAmount = (cursor * totalColors) % 1;

        Color currentColor = colors[currentColorIndex];
        Color nextColor = colors[nextColorIndex];

        int r = lerp(currentColor.getRed(), nextColor.getRed(), lerpAmount);
        int g = lerp(currentColor.getGreen(), nextColor.getGreen(), lerpAmount);
        int b = lerp(currentColor.getBlue(), nextColor.getBlue(), lerpAmount);
        return Color.fromRGB(r, g, b);
    }

    private int lerp(int start, int end, float amount) {
        return (int) (start + amount * (end - start));
    }
}
