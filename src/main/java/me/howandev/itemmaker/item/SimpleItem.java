package me.howandev.itemmaker.item;

import me.howandev.itemmaker.configuration.impl.Configuration;
import me.howandev.itemmaker.item.attribute.AttributeRegistry;
import me.howandev.itemmaker.item.attribute.ItemAttribute;
import me.howandev.itemmaker.item.property.ItemProperty;
import me.howandev.itemmaker.item.property.PropertyRegistry;
import me.howandev.itemmaker.key.Key;
import me.howandev.itemmaker.nbt.editor.NBTEditor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * {@link SimpleItem} is an item that may contain only {@link ItemProperty ItemProperties},
 * and does not persist any additional information after calling {@link Item#asItemStack()}.
 */
public class SimpleItem extends Item {
    Set<ItemAttribute<?>> attributes = new LinkedHashSet<>();
    public SimpleItem(@NotNull String id) {
        super(id);
    }

    @Override
    public void loadConfiguration(@NotNull Configuration configuration) {
        //TODO: remove this attribute section
        Configuration attributesSection = configuration.getSection(ItemAttribute.ATTRIBUTE_SECTION);
        if (attributesSection != null) {
            for (String attributeKey : attributesSection.getKeys(false)) {
                ItemAttribute<?> attribute = AttributeRegistry.getRegisteredAttribute(Key.of(attributeKey));
                if (attribute == null)
                    continue;

                addAttribute(attribute);
            }
        }

        Configuration propertiesSection = configuration.getSection(ItemProperty.PROPERTY_SECTION);
        if (propertiesSection != null) {
            for (String propertyKey : propertiesSection.getKeys(false)) {
                ItemProperty<?> property = PropertyRegistry.getRegisteredProperty(Key.of(propertyKey));
                Object value = propertiesSection.get(propertyKey);
                if (property == null || value == null)
                    continue;

                addProperty(property, value);
            }
        }
    }

    public void addAttribute(final @NotNull ItemAttribute<?> attribute) {
        attributes.add(attribute);
    }

    public Set<ItemAttribute<?>> getAttributes() {
        return Collections.unmodifiableSet(attributes);
    }

    public boolean hasAttribute(ItemAttribute<?> attribute) {
        return attributes.contains(attribute);
    }

    @Override
    public @NotNull ItemStack asItemStack() {
        ItemStack itemStack = NBTEditor.create(new ItemStack(Material.STONE))
                .setString("id", getId())
                .setInt("revision", getRevision())
                .build();

        for (Map.Entry<ItemProperty<?>, Object> entry : properties.entrySet()) {
            itemStack = entry.getKey().parseThenApply(itemStack, entry.getValue());
        }

        for (ItemAttribute<?> attribute : attributes) {
            itemStack = attribute.apply(itemStack);
        }

        return itemStack;
    }
}
