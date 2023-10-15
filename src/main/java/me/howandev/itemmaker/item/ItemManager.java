package me.howandev.itemmaker.item;

import me.howandev.itemmaker.configuration.impl.file.FileConfiguration;
import me.howandev.itemmaker.configuration.impl.file.yaml.YamlConfiguration;
import me.howandev.itemmaker.key.Key;
import me.howandev.itemmaker.nbt.editor.NBTEditor;
import me.howandev.itemmaker.util.FileUtil;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ItemManager {
    private final Map<Key, Item> itemStore = new LinkedHashMap<>();
    private final Map<Key, FileConfiguration> itemConfigStore = new LinkedHashMap<>();
    private final JavaPlugin plugin;
    public ItemManager(final @NotNull JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void registerItem(final @NotNull Item item, final @NotNull FileConfiguration config) {
        itemStore.put(item.getKey(), item);
        itemConfigStore.put(item.getKey(), config);
    }

    public Set<Item> getRegisteredItems() {
        return new HashSet<>(itemStore.values());
    }

    public Item getItem(final @NotNull Key key) {
        return itemStore.get(key);
    }
    public Item getItem(final @NotNull String key) {
        return itemStore.get(Key.of(key));
    }

    public Item getItem(final @Nullable ItemStack itemStack) {
        if (itemStack == null || itemStack.getType().isAir())
            return null;

        String itemId = NBTEditor.create(itemStack).getString("id");
        if (itemId == null)
            return null;

        return getItem(itemId);
    }

    public FileConfiguration getItemConfiguration(final @NotNull Key key) {
        return itemConfigStore.get(key);
    }

    public int loadItems() {
        File itemsDirectory = new File(plugin.getDataFolder(), "items");
        if (!itemsDirectory.exists() && !itemsDirectory.mkdirs()) {
            plugin.getLogger().warning("Unable to create /items directory");
            return 0;
        }

        String[] fileList = itemsDirectory.list();
        if (fileList != null && fileList.length == 0) {
            plugin.getLogger().warning("/items directory is empty!");
            return 0;
        }

        itemStore.clear();
        Set<File> files = FileUtil.listFileTree(itemsDirectory);
        int loadedItems = 0;
        for (File itemFile : files) {
            YamlConfiguration config = new YamlConfiguration(itemFile);
            try {
                config.load();
            } catch (IOException ex) {
                plugin.getLogger().severe("Exception while loading item file: "+ex.getMessage());
                ex.printStackTrace();
            }

            String itemId = config.getString("id");
            if (itemId == null) {
                plugin.getLogger().warning("Unable to load item from '"+ sanitizeFilePath(itemFile)+"', Missing 'id' parameter for the item!");
                continue;
            }

            //FIXME: problem, how do we decide what item type we should use?
            Item item = new SimpleItem(itemId);
            item.setRevision(config.getInteger("revision", 0));
            item.loadConfiguration(config);

            registerItem(item, config);
            plugin.getLogger().info("Successfully loaded item '"+itemId+"' from '"+ sanitizeFilePath(itemFile)+"'");
            loadedItems++;
        }

        return loadedItems;
    }

    public void saveItem(Item item) {
        File itemsDirectory = new File(plugin.getDataFolder(), "items");
        if (!itemsDirectory.exists() && !itemsDirectory.mkdirs()) {
            plugin.getLogger().warning("Unable to create /items directory");
            return;
        }

        String fileName = item.getId().replaceAll("\\Q\\/:*?\"<>|\\E", "_");
        File itemFile = new File(itemsDirectory, fileName+".yml");
        try {
            itemFile.createNewFile();
            YamlConfiguration config = new YamlConfiguration(itemFile);
            config.load();
            config.set("id", item.getId());
            if (item.getRevision() > 0)
                config.set("revision", item.getRevision());

            item.getProperties().forEach((property, value) -> {
                config.set("properties." + property.getKey(), value);
            });

            if (item instanceof SimpleItem simpleItem) {
                simpleItem.getAttributes().forEach(attribute -> {
                    config.set("attributes." + attribute.getKey(), true);
                });
            }

            config.save(itemFile);
        } catch (Exception ignored) { }
    }

    private String sanitizeFilePath(final @NotNull File file) {
        String dataFolderPath = plugin.getDataFolder().getPath();
        String filePath = file.getPath();

        int prefixLength = 1;
        prefixLength += filePath.startsWith(dataFolderPath) ? dataFolderPath.length() : 0;
        prefixLength += filePath.startsWith(File.separator) ? 1 : 0;
        return filePath.substring(prefixLength);
    }
}
