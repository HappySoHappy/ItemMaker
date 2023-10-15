package me.howandev.itemmaker.item.attribute.impl;

import me.howandev.itemmaker.ItemMakerPlugin;
import me.howandev.itemmaker.item.SimpleItem;
import me.howandev.itemmaker.item.attribute.ItemEventAttribute;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.FurnaceSmeltEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.inventory.ItemStack;

@Deprecated
public class UnsmeltableAttribute extends ItemEventAttribute {
    public UnsmeltableAttribute() {
        super("unsmeltable");
    }

    @EventHandler
    public void onFurnaceStartSmelt(FurnaceStartSmeltEvent event) {
        ItemStack itemStack = event.getSource();
        if (itemStack.getType().isAir())
            return;

        SimpleItem item = (SimpleItem) ItemMakerPlugin.instance().getItemManager().getItem(itemStack);
        if (item == null)
            return;

        if (!item.hasAttribute(this))
            return;

        // This call is probably the most expensive here...
        Furnace furnace = (Furnace) event.getBlock().getState();
        furnace.setCookTime((short) 0);
        furnace.setCookTimeTotal(0);
        furnace.update();
    }

    @EventHandler
    public void onFurnaceSmelt(FurnaceSmeltEvent event) {
        ItemStack itemStack = event.getSource();
        if (itemStack.getType().isAir())
            return;

        SimpleItem item = (SimpleItem) ItemMakerPlugin.instance().getItemManager().getItem(itemStack);
        if (item == null)
            return;

        if (!item.hasAttribute(this))
            return;

        event.setResult(new ItemStack(Material.AIR));
        event.setCancelled(true);
    }
}
