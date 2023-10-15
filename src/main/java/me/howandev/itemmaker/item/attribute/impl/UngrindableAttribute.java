package me.howandev.itemmaker.item.attribute.impl;

import me.howandev.itemmaker.ItemMakerPlugin;
import me.howandev.itemmaker.item.SimpleItem;
import me.howandev.itemmaker.item.attribute.ItemEventAttribute;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;

public class UngrindableAttribute extends ItemEventAttribute {
    public UngrindableAttribute() {
        super("ungrindable");
    }

    @EventHandler
    public void onPrepareGrindstone(PrepareGrindstoneEvent event) {
        GrindstoneInventory eventInventory = event.getInventory();

        ItemStack inputItem = eventInventory.getItem(0);
        if (inputItem != null && !inputItem.getType().isAir()) {
            SimpleItem item = (SimpleItem) ItemMakerPlugin.instance().getItemManager().getItem(inputItem);
            if (item != null && item.hasAttribute(this))
                event.setResult(null);
        }

        ItemStack consumableItem = eventInventory.getItem(1);
        if (consumableItem != null && !consumableItem.getType().isAir()) {
            SimpleItem item = (SimpleItem) ItemMakerPlugin.instance().getItemManager().getItem(consumableItem);
            if (item != null && item.hasAttribute(this))
                event.setResult(null);
        }
    }

    @EventHandler
    @Deprecated(forRemoval = true) // This should never be called and pass all the checks...
    public void onGrindstone(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof GrindstoneInventory grindstoneInventory))
            return;

        if (event.getSlot() != 2)
            return;

        ItemStack inputItem = grindstoneInventory.getItem(0);
        if (inputItem != null && !inputItem.getType().isAir()) {
            SimpleItem item = (SimpleItem) ItemMakerPlugin.instance().getItemManager().getItem(inputItem);
            if (item != null) {
                if (item.hasAttribute(this)) {
                    grindstoneInventory.setItem(2, new ItemStack(Material.AIR));
                    event.setCancelled(true);
                }
            }
        }

        ItemStack consumableItem = grindstoneInventory.getItem(1);
        if (consumableItem != null && !consumableItem.getType().isAir()) {
            SimpleItem item = (SimpleItem) ItemMakerPlugin.instance().getItemManager().getItem(consumableItem);
            if (item != null) {
                if (item.hasAttribute(this)) {
                    grindstoneInventory.setItem(2, new ItemStack(Material.AIR));
                    event.setCancelled(true);
                }
            }
        }
    }
}
