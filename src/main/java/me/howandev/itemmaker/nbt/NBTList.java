package me.howandev.itemmaker.nbt;

import me.howandev.itemmaker.nbt.exceptions.NBTException;
import me.howandev.itemmaker.nbt.visitor.NBTStringVisitor;
import me.howandev.itemmaker.nbt.visitor.NBTVisitor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

public final class NBTList implements NBTValue<List<NBT>>, NBT, List<NBT> {

    private final List<NBT> list;
    private Tag type = Tag.END;

    public NBTList(final NBTList list) {
        this();
        this.list.addAll(list.list);
        this.type = list.type;
    }

    public NBTList() {
        this.list = new LinkedList<>();
    }

    public NBTList(final Object... array) {
        this();
        for (final Object value : array) this.add(NBT.convert(value));
    }

    public NBTList(final InputStream stream) throws IOException {
        this();
        final Tag[] tags = Tag.values();
        final Tag tag = tags[stream.read()];
        final int length = NBTInt.decodeInt(stream);
        if (tag == Tag.END || length < 1) return;
        switch (tag) {
            case BYTE -> {
                for (int i = 0; i < length; i++) this.add(new NBTByte(stream));
            }
            case SHORT -> {
                for (int i = 0; i < length; i++) this.add(new NBTShort(stream));
            }
            case INT -> {
                for (int i = 0; i < length; i++) this.add(new NBTInt(stream));
            }
            case LONG -> {
                for (int i = 0; i < length; i++) this.add(new NBTLong(stream));
            }
            case FLOAT -> {
                for (int i = 0; i < length; i++) this.add(new NBTFloat(stream));
            }
            case DOUBLE -> {
                for (int i = 0; i < length; i++) this.add(new NBTDouble(stream));
            }
            case BYTE_ARRAY -> {
                for (int i = 0; i < length; i++) this.add(new NBTByteArray(stream));
            }
            case STRING -> {
                for (int i = 0; i < length; i++) this.add(new NBTString(stream));
            }
            case LIST -> {
                for (int i = 0; i < length; i++) this.add(new NBTList(stream));
            }
            case COMPOUND -> {
                for (int i = 0; i < length; i++) this.add(new NBTCompound(stream));
            }
            case INT_ARRAY -> {
                for (int i = 0; i < length; i++) this.add(new NBTIntArray(stream));
            }
            case LONG_ARRAY -> {
                for (int i = 0; i < length; i++) this.add(new NBTLongArray(stream));
            }
            default -> throw new IOException("Unexpected value: " + tag);
        }
    }

    public NBTList(final Object value) {
        this();
        if (value instanceof Collection<?> collection)
            for (final Object thing : collection) this.add(NBT.convert(thing));
        else if (value instanceof Object[] array)
            for (final Object thing : array) this.add(NBT.convert(thing));
        else this.add(NBT.convert(value));
    }

    public NBTList(final NBT... items) {
        this();
        this.addAll(Arrays.asList(items));
    }

    public NBTList(final Collection<?> collection) {
        this();
        for (final Object value : collection) this.add(NBT.convert(value));
    }

    private void tag(final NBT nbt) {
        if (type == Tag.END) this.type = nbt.tag();
        else if (type != nbt.tag())
            throw new NBTException("Lists may contain one type of value. This is marked for '" + type.name() + "'");
    }

    @Override
    public Tag tag() {
        return Tag.LIST;
    }

    @Override
    public void write(final OutputStream stream) throws IOException {
        if (type == Tag.END && list.size() > 0) this.tag(list.get(0));
        stream.write(type.ordinal());
        NBTInt.encodeInt(stream, list.size());
        for (final NBT nbt : list) nbt.write(stream);
    }

    @Override
    public void accept(final NBTVisitor visitor) {
        visitor.visit(this);
    }

    public Tag getType() {
        return type;
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(final Object o) {
        for (final NBT nbt : list) if (nbt.softEquals(o)) return true;
        return false;
    }

    public boolean contains(final NBT nbt) {
        return list.contains(nbt);
    }

    @Override
    public Iterator<NBT> iterator() {
        return list.iterator();
    }

    @Override
    public NBT[] toArray() {
        return list.toArray(new NBT[0]);
    }

    @Override
    public <T> T[] toArray(final T[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean add(final NBT nbt) {
        this.tag(nbt);
        return list.add(nbt);
    }

    @Override
    public void add(final int index, final NBT element) {
        this.tag(element);
        this.list.add(index, element);
    }

    @Override
    public NBT remove(final int index) {
        return list.remove(index);
    }

    @Override
    public boolean remove(final Object o) {
        final boolean z = list.remove(o);
        if (list.isEmpty()) type = Tag.END;
        return z;
    }

    @Override
    public boolean containsAll(final Collection<?> c) {
        return new HashSet<>(list).containsAll(c);
    }

    @Override
    public boolean addAll(final Collection<? extends NBT> c) {
        return addAll(size(), c);
    }

    @Override
    public boolean addAll(final int index, final Collection<? extends NBT> c) {
        c.forEach(this::tag);
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(final Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(final Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        this.list.clear();
        this.type = Tag.END;
    }

    @Override
    public NBT get(final int index) {
        return list.get(index);
    }

    @Override
    public NBT set(final int index, final NBT element) {
        this.tag(element);
        return list.set(index, element);
    }

    @Override
    public int indexOf(final Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(final Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<NBT> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<NBT> listIterator(final int index) {
        return list.listIterator(index);
    }

    @Override
    public List<NBT> subList(final int fromIndex, final int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    public boolean addValue(final Object object) {
        return this.add(NBT.convert(object));
    }

    @Override
    public List<NBT> value() {
        return Collections.unmodifiableList(new LinkedList<>(this));
    }

    public List<?> revert() {
        return list.stream()
                .map(NBT::revert)
                .collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final NBTList nbtList = (NBTList) o;

        return list.equals(nbtList.list);
    }

    @Override
    public int hashCode() {
        return list.hashCode();
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public NBTList clone() {
        final List<NBT> clone = new LinkedList<>();
        list.forEach(nbt -> clone.add(nbt.clone()));
        return new NBTList(clone);
    }

}
