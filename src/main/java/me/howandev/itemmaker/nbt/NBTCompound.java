package me.howandev.itemmaker.nbt;

import me.howandev.itemmaker.nbt.exceptions.NBTException;
import me.howandev.itemmaker.nbt.visitor.NBTStringVisitor;
import me.howandev.itemmaker.nbt.visitor.NBTVisitor;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

public final class NBTCompound implements NBTValue<Map<String, NBT>>, Iterable<String>, Map<String, NBT>, NBT {

    private final Map<String, NBT> map = new HashMap<>();

    public NBTCompound(final NBTCompound compound) {
        this();
        this.map.putAll(compound.map);
    }

    public NBTCompound() {
    }

    public NBTCompound(final Object value) {
        this((Map<?, ?>) value);
    }

    public NBTCompound(final Map<?, ?> map) {
        this();
        if (map instanceof NBTCompound compound) this.map.putAll(compound.map);
        else for (final Entry<?, ?> entry : map.entrySet()) {
            this.set(entry.getKey().toString(), entry.getValue());
        }
    }

    public NBTCompound(final InputStream stream) throws IOException {
        this();
        this.read(stream);
    }

    private static UUID fromInts(final int[] array) {
        if (array.length < 4) return null;
        final long big = 0xFFFFFFFFL;
        return new UUID((long) array[0] << 32 | (long) array[1] & big, (long) array[2] << 32 | (long) array[3] & big);
    }

    private static int[] toInts(final UUID uuid) {
        final long most = uuid.getMostSignificantBits();
        final long least = uuid.getLeastSignificantBits();
        return new int[]{(int) (most >> 32), (int) most, (int) (least >> 32), (int) least};
    }

    public <T> void set(final String key, final T value) {
        if (value == null) this.remove(key);
        else if (value instanceof NBT nbt) this.map.put(key, nbt);
        else if (value instanceof UUID uuid) this.setUUID(key, uuid);
        else this.map.put(key, NBT.convert(value));
    }

    public <T> void set(final String key, final Inserter<T> inserter, final T value) {
        if (value == null) {
            this.remove(key);
            return;
        }
        final NBTCompound compound = new NBTCompound();
        inserter.accept(compound, value);
        this.map.put(key, compound);
    }

    public void set(final String key, final byte... value) {
        this.map.put(key, new NBTByteArray(value));
    }

    public void set(final String key, final int... value) {
        this.map.put(key, new NBTIntArray(value));
    }

    public void set(final String key, final long... value) {
        this.map.put(key, new NBTLongArray(value));
    }

    public <T> void setList(final String key, final Inserter<T> inserter, final Collection<T> value) {
        if (value == null) {
            this.remove(key);
            return;
        }
        final NBTList list = new NBTList();
        for (final T type : value) {
            final NBTCompound compound = new NBTCompound();
            inserter.accept(compound, type);
            list.add(compound);
        }
        this.map.put(key, list);
    }

    @SafeVarargs
    public final <T> void setList(final String key, final Inserter<T> inserter, final T... value) {
        if (value == null) {
            this.remove(key);
            return;
        }
        this.setList(key, inserter, List.of(value));
    }

    public void read(final InputStream stream) throws IOException {
        final Tag[] tags = Tag.values();
        for (int i = stream.read(); i != -1; i = stream.read()) {
            final Tag tag = tags[i];
            if (tag == Tag.END) return;
            final String key = NBTString.decodeString(stream);
            final NBT nbt = switch (tag) {
                case BYTE -> new NBTByte(stream);
                case SHORT -> new NBTShort(stream);
                case INT -> new NBTInt(stream);
                case LONG -> new NBTLong(stream);
                case FLOAT -> new NBTFloat(stream);
                case DOUBLE -> new NBTDouble(stream);
                case BYTE_ARRAY -> new NBTByteArray(stream);
                case STRING -> new NBTString(stream);
                case LIST -> new NBTList(stream);
                case COMPOUND -> new NBTCompound(stream);
                case INT_ARRAY -> new NBTIntArray(stream);
                case LONG_ARRAY -> new NBTLongArray(stream);
                default -> throw new IOException("Unexpected value: " + tag);
            };
            this.put(key, nbt);
        }
    }

    public void read(final File file) throws IOException {
        try (InputStream stream = new FileInputStream(file)) {
            this.readAll(stream);
        }
    }

    public int size() {
        return map.size();
    }

    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key) {
        return map.containsKey(key);
    }

    @Override
    public boolean containsValue(final Object value) {
        return map.containsValue(value);
    }

    @SuppressWarnings("unchecked")
    public <T extends NBT> T get(final String key, final T alternative) {
        final NBT nbt = map.get(key);
        if (nbt != null) return (T) nbt;
        return alternative;
    }

    @SuppressWarnings("unchecked")
    public <T extends NBT> T get(final String key, final Tag tag) {
        final NBT nbt = map.get(key);
        if (nbt == null) return null;
        if (nbt.tag() == tag) return (T) nbt;
        throw new NBTException("Requested tag is a '" + nbt.tag() + "' not a '" + tag + "'.");
    }

    @Override
    public NBT get(final Object key) {
        return map.get(key);
    }

    public <T> T get(final String key, final Extractor<T> extractor) {
        if (map.get(key) instanceof NBTCompound compound) return extractor.apply(compound);
        return null;
    }

    public <T> T get(final String key, final Extractor<T> extractor, final T alternative) {
        if (map.get(key) instanceof NBTCompound compound) return extractor.apply(compound, alternative);
        return alternative;
    }

    public NBT put(final String key, final Object value) {
        final NBT prev = get(key);
        set(key, value);
        return prev;
    }

    @Override
    public NBT put(final String key, final NBT value) {
        return put(key, (Object) value);
    }

    @Override
    public NBT remove(final Object key) {
        return map.remove(key);
    }

    @Override
    public void putAll(final Map<? extends String, ? extends NBT> map) {
        this.map.putAll(map);
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    @Override
    public Set<String> keySet() {
        return map.keySet();
    }

    @Override
    public Collection<NBT> values() {
        return map.values();
    }

    @Override
    public Set<Entry<String, NBT>> entrySet() {
        return map.entrySet();
    }

    public <T> T getValue(final String key) {
        final NBT nbt = map.get(key);
        if (nbt == null) return null;
        return nbt.value();
    }

    public <T> T getValue(final String key, final T alternative) {
        final NBT nbt = map.get(key);
        if (nbt != null) return nbt.value();
        return alternative;
    }

    public <T> T getValue(final String key, final Class<T> source) {
        final NBT nbt = map.get(key);
        if (nbt == null) return null;
        if (source.isAssignableFrom(nbt.value().getClass())) return nbt.value();
        throw new NBTException("Requested class is a '" + nbt.value().getClass() + "' not a '" + source + "'.");
    }

    public NBTList getList(final String key) {
        final NBT nbt = map.get(key);
        if (nbt instanceof NBTList list) return list;
        return new NBTList(nbt);
    }

    public <T> List<T> getList(final String key, final Extractor<T> extractor) {
        if (!(map.get(key) instanceof NBTList list)) return null;
        final List<T> converted = new ArrayList<>(list.size());
        for (final NBT nbt : list) {
            if (!(nbt instanceof NBTCompound compound)) throw new NBTException("List contains a non-compound element.");
            converted.add(extractor.apply(compound));
        }
        return converted;
    }

    public <T> List<T> getList(final String key, final Extractor<T> extractor, final List<T> alternative) {
        try {
            final List<T> list = this.getList(key, extractor);
            if (list != null) return list;
            return alternative;
        } catch (NBTException ex) {
            return alternative;
        }
    }

    @Override
    public Iterator<String> iterator() {
        return map.keySet().iterator();
    }

    @Override
    public void forEach(final Consumer<? super String> action) {
        this.map.keySet().forEach(action);
    }

    @Override
    public Spliterator<String> spliterator() {
        return map.keySet().spliterator();
    }

    @Override
    public Map<String, NBT> value() {
        return Collections.unmodifiableMap(new HashMap<>(this));
    }

    public Map<String, ?> revert() {
        final HashMap<String, Object> hashMap = new HashMap<>(map.size());
        map.forEach((key, nbt) -> hashMap.put(key, NBT.revert(nbt)));
        return hashMap;
    }

    @Override
    public String toString() {
        return new NBTStringVisitor().visitNBT(this);
    }

    public void readAll(final InputStream stream) {
        try {
            final int tag = stream.read();
            assert tag == this.tag().ordinal(); // we are discarding this anyway
            NBTString.decodeString(stream); // ignore the empty base tag
            this.read(stream);
        } catch (IOException ex) {
            throw new NBTException(ex);
        }
    }

    public void write(final File file) throws IOException {
        try (OutputStream stream = new FileOutputStream(file)) {
            this.writeAll(stream);
        }
    }

    @Override
    public void write(final OutputStream stream) throws IOException {
        for (final Entry<String, NBT> entry : map.entrySet()) {
            stream.write(entry.getValue().tag().ordinal());
            NBTString.encodeString(stream, entry.getKey());
            entry.getValue().write(stream);
        }
        stream.write(Tag.END.ordinal());
    }

    public void writeAll(final OutputStream stream) {
        try {
            stream.write(this.tag().ordinal());
            NBTString.encodeString(stream, "");
            this.write(stream);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Tag tag() {
        return Tag.COMPOUND;
    }

    @Override
    public void accept(final NBTVisitor visitor) {
        visitor.visit(this);
    }

    public boolean contains(final String key, final NBT.Tag tag) {
        if (!this.containsKey(key)) return false;
        return map.get(key).tag() == tag;
    }

    public void setUUID(final String key, final UUID value) {
        if (this.contains(key + "Most", Tag.LONG) && this.contains(key + "Least", Tag.LONG)) {
            this.map.remove(key + "Most");
            this.map.remove(key + "Least");
        }
        this.map.put(key, new NBTIntArray(toInts(value)));
    }

    public UUID getUUID(final String key, final UUID alternative) {
        final UUID found = this.getUUID(key);
        if (found == null) return alternative;
        return found;
    }

    @SuppressWarnings("ConstantConditions")
    public UUID getUUID(final String key) {
        if (this.contains(key, Tag.INT_ARRAY)) { // I would love to know why we use four ints rather than two longs
            final int[] value = this.getValue(key);
            return fromInts(value);
        } else if (this.contains(key + "Most", Tag.LONG) && this.contains(key + "Least", Tag.LONG)) {
            return new UUID(this.getValue(key + "Most"), this.getValue(key + "Least"));
        } else return null;
    }

    public boolean hasUUID(final String key) {
        if (this.contains(key + "Most", Tag.LONG) && this.contains(key + "Least", Tag.LONG)) return true;
        if (!this.containsKey(key)) return false;
        final NBT nbt = map.get(key);
        return map.get(key).tag() == Tag.INT_ARRAY && nbt.value() instanceof int[] ints && ints.length == 4;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        final NBTCompound strings = (NBTCompound) o;

        return map.equals(strings.map);
    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }

    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public NBTCompound clone() {
        final Map<String, NBT> clone = new HashMap<>(map.size());
        map.forEach((key, nbt) -> clone.put(key, nbt.clone()));
        return new NBTCompound(clone);
    }

}
