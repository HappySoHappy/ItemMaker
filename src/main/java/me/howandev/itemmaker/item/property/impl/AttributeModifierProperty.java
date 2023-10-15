package me.howandev.itemmaker.item.property.impl;

import me.howandev.itemmaker.ItemMakerConstants;
import me.howandev.itemmaker.item.property.ItemMapProperty;
import me.howandev.itemmaker.key.Key;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

public class AttributeModifierProperty extends ItemMapProperty<Attribute, AttributeModifier> {
    public AttributeModifierProperty() {
        super("attribute");
    }

    @Override
    public @NotNull ItemStack apply(final @NotNull ItemStack itemStack, final @NotNull Map<Attribute, AttributeModifier> value) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null)
            for (Map.Entry<Attribute, AttributeModifier> entry : value.entrySet())
                meta.addAttributeModifier(entry.getKey(), entry.getValue());

        itemStack.setItemMeta(meta);
        return itemStack;
    }

    //TODO: this shit needs rework - wtf?
    @Override
    public @NotNull ItemStack parseThenApply(final @NotNull ItemStack itemStack, final @NotNull Object value) throws IllegalArgumentException {
        List<String> stringAttributes = new ArrayList<>();
        Map<Attribute, AttributeModifier> attributes = new HashMap<>();
        if (value instanceof String stringValue) {
            stringAttributes = Arrays.asList(ItemMakerConstants.NEW_LINE_PATTERN.split(stringValue));
        }

        if (value instanceof List<?> listValue) {
            stringAttributes = listValue.stream().map(Object::toString).collect(Collectors.toList());
        }

        for (String rawEnchantment : stringAttributes) {
            Matcher matcher = ItemMakerConstants.ATTRIBUTE_PATTERN.matcher(rawEnchantment);
            if (matcher.find()) {
                String valueGroup = matcher.group("value");
                Double amount = (valueGroup == null || valueGroup.isEmpty()) ? 1 : Double.parseDouble(valueGroup);

                String slotGroup = matcher.group("slot");
                EquipmentSlot slot = null;
                try {
                    slot = EquipmentSlot.valueOf(slotGroup.toUpperCase(Locale.ROOT));
                } catch (Exception ignored) { }

                String attributeGroup = matcher.group("attribute");

                String operationGroup = matcher.group("operation");
                if (operationGroup == null || operationGroup.isEmpty()) {
                    Attribute attribute = getAttribute(attributeGroup);

                    attributes.put(attribute, getAttributeModifier(attribute, amount, AttributeModifier.Operation.ADD_NUMBER, slot));
                    continue;
                }

                Attribute attribute = getAttribute(attributeGroup);
                attributes.put(attribute, getAttributeModifier(attribute, amount, getOperation(operationGroup), slot));
            }
        }

        return apply(itemStack, attributes);
    }

    @Override
    public boolean isRemovable(final @NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        return meta != null && meta.hasAttributeModifiers();
    }

    @Override
    public @NotNull ItemStack remove(final @NotNull ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null)
            for (Attribute attribute : Attribute.values()) {
                meta.removeAttributeModifier(attribute);
            }
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    @Override
    public @NotNull ItemStack remove(final @NotNull ItemStack itemStack, final int index) {
        ItemMeta meta = itemStack.getItemMeta();
        if (meta != null) {
            Set<Attribute> attributes = meta.hasAttributeModifiers() ? meta.getAttributeModifiers().keySet() : new HashSet<>();
            Attribute attribute = new LinkedList<>(attributes).get(index);
            if (attribute != null)
                meta.removeAttributeModifier(attribute);
        }
        itemStack.setItemMeta(meta);

        return itemStack;
    }

    private @NotNull Attribute getAttribute(final String attributeName) {
        for (Attribute attribute : Attribute.values()) {
            if (attribute.getKey().getKey().equalsIgnoreCase(attributeName))
                return attribute;
        }

        return Attribute.valueOf(attributeName.toUpperCase(Locale.ROOT));
    }

    /**
     * @apiNote using {@link Attribute#getKey()} as uuid for the modifier, im sure that you cant even register attributes runtime
     */
    private AttributeModifier getAttributeModifier(final Attribute attribute, final Double value, final AttributeModifier.Operation operation, final EquipmentSlot slot) {
        String uniqueAttributeSlot = attribute.getKey().getKey() + "@" + slot.name();
        UUID uniqueId = UUID.nameUUIDFromBytes(uniqueAttributeSlot.getBytes(StandardCharsets.UTF_8));
        return new AttributeModifier(uniqueId, attribute.getKey().getKey(), value, operation, slot);
    }

    private @NotNull AttributeModifier.Operation getOperation(final String operationName) {
        return AttributeModifier.Operation.valueOf(operationName.toUpperCase(Locale.ROOT));
    }
}
