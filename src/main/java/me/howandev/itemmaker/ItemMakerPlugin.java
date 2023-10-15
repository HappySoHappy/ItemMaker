package me.howandev.itemmaker;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.howandev.itemmaker.commands.ItemMakerCommands;
import me.howandev.itemmaker.configuration.impl.file.FileConfiguration;
import me.howandev.itemmaker.configuration.impl.file.yaml.YamlConfiguration;
import me.howandev.itemmaker.i18n.ExtraLocale;
import me.howandev.itemmaker.item.Item;
import me.howandev.itemmaker.item.ItemManager;
import me.howandev.itemmaker.item.attribute.AttributeRegistry;
import me.howandev.itemmaker.item.property.PropertyRegistry;
import me.howandev.itemmaker.text.TranslationManager;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.java.JavaPluginLoader;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

public class ItemMakerPlugin extends JavaPlugin {
    @Getter
    @Accessors(fluent = true)
    private static ItemMakerPlugin instance;

    @Getter
    @Accessors(fluent = true)
    private static Logger pluginLogger;

    @Getter
    @Accessors(fluent = true)
    private static FileConfiguration pluginConfiguration;

    @Getter
    private ItemMakerSettings pluginSettings;

    @Getter
    private TranslationManager translationManager;

    @Getter
    private ItemManager itemManager;

    public ItemMakerPlugin() {
        super();
        instance = this;
        pluginLogger = getLogger();
        pluginConfiguration = new YamlConfiguration(new File(getDataFolder(), "config.yml"));
    }

    public ItemMakerPlugin(JavaPluginLoader loader, PluginDescriptionFile description, File dataFolder, File file) {
        super(loader, description, dataFolder, file);
        instance = this;
        pluginLogger = getLogger();
        pluginConfiguration = new YamlConfiguration(new File(getDataFolder(), "config.yml"));
    }

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            pluginConfiguration.load();
        } catch (IOException ignored) { }
        pluginSettings = new ItemMakerSettings(pluginConfiguration);

        translationManager = new TranslationManager(this);
        translationManager.addMessageBundle("itemmaker", ExtraLocale.ENGLISH, ExtraLocale.POLISH);
        translationManager.loadTranslations(); //load additional locales from ItemMaker/locales/*.yml

        translationManager.usePerPlayerLocale(pluginSettings.perPlayerLocale());

        PropertyRegistry.initializeRegistry();
        AttributeRegistry.initializeRegistry();
        itemManager = new ItemManager(this);

        // Only re-create resources if items folder is missing,
        // because sometimes we might want to load nothing.
        if (!new File(getDataFolder(), "items").exists()) {
            saveResource("items/silver_ore.yml", false);
            saveResource("items/simple_andesite.yml", false);
            saveResource("items/test_sword.yml", false);

            saveResource("items/attribute_test/chest.yml", false);
            saveResource("items/attribute_test/feet.yml", false);
            saveResource("items/attribute_test/helmet.yml", false);
            saveResource("items/attribute_test/legs.yml", false);

            saveResource("items/attribute_test/rainbow/rainbow_helmet.yml", false);
            saveResource("items/attribute_test/rainbow/rainbow_chestplate.yml", false);
            saveResource("items/attribute_test/rainbow/rainbow_leggings.yml", false);
            saveResource("items/attribute_test/rainbow/rainbow_boots.yml", false);
        }

        /* item saving
        SimpleItem itemi = new SimpleItem("saved_item");
        itemi.setRevision(1);
        itemi.addProperty(PropertyRegistry.MATERIAL_PROPERTY, "BARRIER");
        itemi.addProperty(PropertyRegistry.ENCHANTMENT_PROPERTY, List.of("UNBREAKING 1", "SHARPNESS 20"));
        itemi.addProperty(PropertyRegistry.UNSTACKABLE_PROPERTY, true);
        itemi.addAttribute(AttributeRegistry.UNGRINDABLE_ATTRIBUTE);
        itemManager.saveItem(itemi);*/

        itemManager.loadItems();

        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.getCommandCompletions().registerCompletion("items", c -> {
            Set<String> loadedItems = new HashSet<>();
            for (Item item : itemManager.getRegisteredItems()) {
                loadedItems.add(item.getId());
            }

            return loadedItems;
        });

        commandManager.registerCommand(new ItemMakerCommands());
    }

    @Override
    public void onDisable() {
        //save data etc...
    }

    /*

    //Give your players the ability to set their desired language, and in PlayerJoinEvent before you send any messages
    // Then call manager.setPlayerLocale(player, preferredLocale), and that language will be used for I18N.
    // see https://github.com/aikar/commands/wiki/Locales
    private void loadCommandLocales(PaperCommandManager commandManager) {
        commandManager.getLocales().setDefaultLocale(Locale.ENGLISH);
        commandManager.usePerIssuerLocale(settings.languagePerUserLocale(), false);

        File localesDirectory = new File(getDataFolder(), "locales");
        File[] localeFiles = localesDirectory.listFiles(File::isFile);
        if (localesDirectory.exists() && localeFiles != null && localeFiles.length > 0) {
            for (File localeFile : localeFiles) {
                try {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(localeFile);
                    String localeString = config.getString("translation");
                    if (localeString == null) throw new IllegalStateException("Configuration does not contain definition for 'translation'");

                    Locale locale = new Locale(localeString);
                    if (commandManager.getLocales().loadLanguage(config, locale)) {
                        commandManager.addSupportedLanguage(locale);
                        getLogger().info(String.format("Successfully loaded '%s' translation from '%s'", localeString, localeFile.getName()));
                    }
                } catch (IllegalStateException | IllegalArgumentException ex) {
                    getLogger().severe(String.format("Failed to load language file '%s', %s", localeFile.getName(), ex.getMessage()));
                }
            }
        }
    }

    private void setupCommands() {
        PaperCommandManager commandManager = new PaperCommandManager(this);
        this.commandManager = commandManager;

        commandManager.enableUnstableAPI("help");
        commandManager.enableUnstableAPI("brigadier");

        loadCommandLocales(commandManager);
        commandManager.registerCommand(new ItemMakerCommands());
    }

     */
}
