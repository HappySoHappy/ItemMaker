package me.howandev.itemmaker.item.attribute.impl;

import me.howandev.itemmaker.ItemMakerPlugin;
import me.howandev.itemmaker.item.SimpleItem;
import me.howandev.itemmaker.item.attribute.ItemEventAttribute;
import me.howandev.itemmaker.nbt.editor.NBTEditor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.inventory.ItemStack;

public class UnenchantableAttribute extends ItemEventAttribute {
    public UnenchantableAttribute() {
        super("unenchantable");
    }

    @EventHandler
    public void onPrepareItemEnchant(PrepareItemEnchantEvent event) {
        ItemStack itemStack = event.getItem();
        String itemId = NBTEditor.create(itemStack).getString("id");
        if (itemId == null)
            return;

        SimpleItem item = (SimpleItem) ItemMakerPlugin.instance().getItemManager().getItem(itemId);
        if (item == null)
            return;

        if (!item.hasAttribute(this))
            return;

        event.setCancelled(true);
    }


    @EventHandler
    public void onEnchantItem(EnchantItemEvent event) {
        ItemStack itemStack = event.getItem();
        String itemId = NBTEditor.create(itemStack).getString("id");
        if (itemId == null)
            return;

        SimpleItem item = (SimpleItem) ItemMakerPlugin.instance().getItemManager().getItem(itemId);
        if (item == null)
            return;

        if (!item.hasAttribute(this))
            return;

        event.setCancelled(true);
    }
}
