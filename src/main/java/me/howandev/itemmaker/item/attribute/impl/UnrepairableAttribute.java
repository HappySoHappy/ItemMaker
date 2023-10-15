package me.howandev.itemmaker.item.attribute.impl;

import me.howandev.itemmaker.ItemMakerPlugin;
import me.howandev.itemmaker.item.SimpleItem;
import me.howandev.itemmaker.item.attribute.ItemEventAttribute;
import me.howandev.itemmaker.nbt.editor.NBTEditor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.inventory.PrepareGrindstoneEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.GrindstoneInventory;
import org.bukkit.inventory.ItemStack;

// i wonder how many times ill have to add new things to this class
public class UnrepairableAttribute extends ItemEventAttribute {
    public UnrepairableAttribute() {
        super("unrepairable");
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if (!event.isRepair())
            return;

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

    //todo: what about enchanting items with books? its cancelled here...
    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory eventInventory = event.getInventory();

        ItemStack target = eventInventory.getItem(0);
        if (target != null) {
            String targetId = NBTEditor.create(target).getString("id");
            if (targetId == null)
                return;

            SimpleItem item = (SimpleItem) ItemMakerPlugin.instance().getItemManager().getItem(targetId);
            if (item == null)
                return;

            if (!item.hasAttribute(this))
                return;

            event.setResult(null);
        }

        ItemStack consumable = eventInventory.getItem(1);
        if (consumable != null) {
            String consumableId = NBTEditor.create(consumable).getString("id");
            if (consumableId == null)
                return;

            SimpleItem item = (SimpleItem) ItemMakerPlugin.instance().getItemManager().getItem(consumableId);
            // I guess the item still should be processed even though it's not registered
            if (item == null)
                return;

            if (!item.hasAttribute(this))
                return;

            event.setResult(null);
        }
    }

    @EventHandler
    public void onPrepareGrindStone(PrepareGrindstoneEvent event) {
        GrindstoneInventory eventInventory = event.getInventory();

        ItemStack inputItemStack = eventInventory.getItem(0);
        SimpleItem inputItem = (SimpleItem) ItemMakerPlugin.instance().getItemManager().getItem(inputItemStack);
        if (inputItem != null && inputItem.hasAttribute(this))
            event.setResult(null);

        ItemStack consumableItemStack = eventInventory.getItem(1);
        SimpleItem consumableItem = (SimpleItem) ItemMakerPlugin.instance().getItemManager().getItem(consumableItemStack);
        if (consumableItem != null && consumableItem.hasAttribute(this))
            event.setResult(null);
    }
}
