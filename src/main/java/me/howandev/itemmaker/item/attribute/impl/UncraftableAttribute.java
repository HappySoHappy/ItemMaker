package me.howandev.itemmaker.item.attribute.impl;

import me.howandev.itemmaker.ItemMakerPlugin;
import me.howandev.itemmaker.item.SimpleItem;
import me.howandev.itemmaker.item.attribute.ItemEventAttribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public class UncraftableAttribute extends ItemEventAttribute {
    public UncraftableAttribute() {
        super("uncraftable");
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        for (ItemStack itemStack : event.getInventory().getMatrix()) {
            if (itemStack == null || itemStack.getType().isAir())
                continue;

            SimpleItem item = (SimpleItem) ItemMakerPlugin.instance().getItemManager().getItem(itemStack);
            if (item == null)
                continue;

            if (!item.hasAttribute(this))
                continue;

            event.getInventory().setResult(null);
            return;
        }
    }

    @EventHandler
    public void onCraftItem(CraftItemEvent event) {
        for (ItemStack itemStack : event.getInventory().getMatrix()) {
            if (itemStack == null || itemStack.getType().isAir())
                continue;

            SimpleItem item = (SimpleItem) ItemMakerPlugin.instance().getItemManager().getItem(itemStack);
            if (item == null)
                continue;

            if (!item.hasAttribute(this))
                continue;

            event.getInventory().setResult(null);
            event.setCancelled(true);
            return;
        }
    }
}
