package me.howandev.itemmaker.item.attribute.impl;

import me.howandev.itemmaker.ItemMakerPlugin;
import me.howandev.itemmaker.item.SimpleItem;
import me.howandev.itemmaker.item.attribute.ItemEventAttribute;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareSmithingEvent;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmithingInventory;

public class UnsmithableAttribute extends ItemEventAttribute {
    public UnsmithableAttribute() {
        super("unsmithable");
    }

    @EventHandler
    public void onPrepareSmithing(PrepareSmithingEvent event) {
        SmithingInventory eventInventory = event.getInventory();

        ItemStack itemStack = eventInventory.getItem(1);
        if (itemStack == null || itemStack.getType().isAir())
            return;

        SimpleItem item = (SimpleItem) ItemMakerPlugin.instance().getItemManager().getItem(itemStack);
        if (item == null)
            return;

        if (!item.hasAttribute(this))
            return;

        event.setResult(new ItemStack(Material.AIR));
    }

    @EventHandler
    public void onSmithItem(SmithItemEvent event) {
        SmithingInventory eventInventory = event.getInventory();

        ItemStack itemStack = event.getCurrentItem();
        if (itemStack == null || itemStack.getType().isAir())
            return;

        SimpleItem item = (SimpleItem) ItemMakerPlugin.instance().getItemManager().getItem(itemStack);
        if (item == null)
            return;

        if (!item.hasAttribute(this))
            return;

        event.setCancelled(true);
        eventInventory.setResult(new ItemStack(Material.AIR));
    }
}
