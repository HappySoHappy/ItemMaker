package me.howandev.itemmaker.nbt.visitor;

import me.howandev.itemmaker.nbt.*;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NBTPersistentDataVisitor implements NBTVisitor {

    private final PersistentDataAdapterContext context;

    private PersistentDataContainer root;

    private PersistentDataContainer nextContainer;
    private NamespacedKey nextKey;

    public NBTPersistentDataVisitor(final PersistentDataAdapterContext context) {
        this.context = context;
    }

    public PersistentDataContainer visitNBT(final NBTCompound compound) {
        root = context.newPersistentDataContainer();
        visit(compound);
        return root;
    }

    @Override
    public void visit(final NBTString nbtString) {
        if (nextContainer == null || nextKey == null) return;
        nextContainer.set(nextKey, PersistentDataType.STRING, nbtString.value());
    }

    @Override
    public void visit(final NBTByte nbtByte) {
        if (nextContainer == null || nextKey == null) return;
        nextContainer.set(nextKey, PersistentDataType.BYTE, nbtByte.value());
    }

    @Override
    public void visit(final NBTShort nbtShort) {
        if (nextContainer == null || nextKey == null) return;
        nextContainer.set(nextKey, PersistentDataType.SHORT, nbtShort.value());
    }

    @Override
    public void visit(final NBTInt nbtInt) {
        if (nextContainer == null || nextKey == null) return;
        nextContainer.set(nextKey, PersistentDataType.INTEGER, nbtInt.value());
    }

    @Override
    public void visit(final NBTLong nbtLong) {
        if (nextContainer == null || nextKey == null) return;
        nextContainer.set(nextKey, PersistentDataType.LONG, nbtLong.value());
    }

    @Override
    public void visit(final NBTFloat nbtFloat) {
        if (nextContainer == null || nextKey == null) return;
        nextContainer.set(nextKey, PersistentDataType.FLOAT, nbtFloat.value());
    }

    @Override
    public void visit(final NBTDouble nbtDouble) {
        if (nextContainer == null || nextKey == null) return;
        nextContainer.set(nextKey, PersistentDataType.DOUBLE, nbtDouble.value());
    }

    @Override
    public void visit(final NBTByteArray nbtByteArray) {
        if (nextContainer == null || nextKey == null) return;
        nextContainer.set(nextKey, PersistentDataType.BYTE_ARRAY, nbtByteArray.value());
    }

    @Override
    public void visit(final NBTIntArray nbtIntArray) {
        if (nextContainer == null || nextKey == null) return;
        nextContainer.set(nextKey, PersistentDataType.INTEGER_ARRAY, nbtIntArray.value());
    }

    @Override
    public void visit(final NBTLongArray nbtLongArray) {
        if (nextContainer == null || nextKey == null) return;
        nextContainer.set(nextKey, PersistentDataType.LONG_ARRAY, nbtLongArray.value());
    }

    @Override
    public void visit(final NBTList nbtList) {
        if (nextContainer == null || nextKey == null) return;
        final NBTCompound compoundCopy = new NBTCompound();
        for (int i = 0; i < nbtList.size(); i++)
            compoundCopy.set(String.valueOf(i), nbtList.get(i));
        final PersistentDataContainer listContainer = new NBTPersistentDataVisitor(context).visitNBT(compoundCopy);
        nextContainer.set(nextKey, PersistentDataType.TAG_CONTAINER, listContainer);
    }

    @Override
    public void visit(final NBTCompound nbtCompound) {
        final List<String> keys = new ArrayList<>(nbtCompound.keySet());
        Collections.sort(keys);

        for (final String key : keys) {
            final NBT value = nbtCompound.get(key);
            if (value == null) continue;

            final NamespacedKey name = new NamespacedKey(key.toLowerCase(), value.tag().name().toLowerCase());
            final PersistentDataContainer holder = context.newPersistentDataContainer();

            final NamespacedKey keyName = new NamespacedKey(key.toLowerCase(), "key");
            final NamespacedKey valueName = new NamespacedKey(key.toLowerCase(), "value");

            holder.set(keyName, PersistentDataType.STRING, key);

            if (value instanceof NBTCompound compound) {
                final NBTPersistentDataVisitor visitor = new NBTPersistentDataVisitor(context);
                final PersistentDataContainer data = visitor.visitNBT(compound);
                holder.set(valueName, PersistentDataType.TAG_CONTAINER, data);
                continue;
            } else {
                nextContainer = holder;
                nextKey = valueName;
                value.accept(this);
            }

            root.set(name, PersistentDataType.TAG_CONTAINER, holder);
        }
    }

    @Override
    public void visit(final NBTEnd nbtEnd) {

    }

}
