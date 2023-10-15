package me.howandev.itemmaker.nbt;

import me.howandev.itemmaker.nbt.visitor.NBTPersistentDataVisitor;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class NBTCompoundDataType implements PersistentDataType<PersistentDataContainer, NBTCompound> {

    public static final NBTCompoundDataType INSTANCE = new NBTCompoundDataType();

    private NBTCompoundDataType() {

    }

    @Override
    public @NotNull Class<PersistentDataContainer> getPrimitiveType() {
        return PersistentDataContainer.class;
    }

    @Override
    public @NotNull Class<NBTCompound> getComplexType() {
        return NBTCompound.class;
    }

    @Override
    public @NotNull PersistentDataContainer toPrimitive(final @NotNull NBTCompound complex, final @NotNull PersistentDataAdapterContext context) {
        final NBTPersistentDataVisitor visitor = new NBTPersistentDataVisitor(context);
        return visitor.visitNBT(complex);
    }

    @Override
    public @NotNull NBTCompound fromPrimitive(final @NotNull PersistentDataContainer primitive, final @NotNull PersistentDataAdapterContext context) {
        return compoundFromContainer(primitive);
    }

    private NBTCompound compoundFromContainer(final PersistentDataContainer container) {
        final NBTCompound compound = new NBTCompound();

        for (final NamespacedKey name : container.getKeys()) {

            final NamespacedKey keyName = new NamespacedKey(name.getNamespace(), "key");
            final NamespacedKey valueName = new NamespacedKey(name.getNamespace(), "value");

            final PersistentDataContainer child = container.get(name, TAG_CONTAINER);
            if (child == null) continue;
            final String key = child.get(keyName, STRING);

            final NBT.Tag tag = NBT.Tag.valueOf(name.getKey().toUpperCase());

            final NBT value = switch (tag) {
                case END -> throw new UnsupportedOperationException();
                case BYTE -> new NBTByte(child.get(valueName, BYTE));
                case SHORT -> new NBTShort(child.get(valueName, SHORT));
                case INT -> new NBTInt(child.get(valueName, INTEGER));
                case LONG -> new NBTLong(child.get(valueName, LONG));
                case FLOAT -> new NBTFloat(child.get(valueName, FLOAT));
                case DOUBLE -> new NBTDouble(child.get(valueName, DOUBLE));
                case BYTE_ARRAY -> new NBTByteArray(child.get(valueName, BYTE_ARRAY));
                case STRING -> new NBTString(child.get(valueName, STRING));
                case LIST -> {
                    final PersistentDataContainer listContainer = child.get(valueName, TAG_CONTAINER);
                    yield listFromContainer(Objects.requireNonNull(listContainer));
                }
                case COMPOUND ->  {
                    final PersistentDataContainer compoundContainer = child.get(valueName, TAG_CONTAINER);
                    yield compoundFromContainer(Objects.requireNonNull(compoundContainer));
                }
                case INT_ARRAY -> new NBTIntArray(child.get(valueName, INTEGER_ARRAY));
                case LONG_ARRAY -> new NBTLongArray(child.get(valueName, LONG_ARRAY));
            };

            compound.set(key, value);
        }

        return compound;
    }

    private NBTList listFromContainer(final PersistentDataContainer container) {
        final NBTCompound compound = compoundFromContainer(container);
        final NBTList list = new NBTList();
        for (int i = 0; i < compound.size(); i++)
            list.add(compound.get(String.valueOf(i)));
        return list;
    }

}
