package me.howandev.itemmaker.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Subcommand;
import me.howandev.itemmaker.ItemMakerPlugin;
import me.howandev.itemmaker.item.Item;
import me.howandev.itemmaker.item.ItemManager;
import me.howandev.itemmaker.key.Key;
import me.howandev.itemmaker.text.TranslationManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.Locale;

@CommandAlias("itemmaker|itemm|im")
public class ItemMakerCommands extends BaseCommand {
    @Subcommand("reload")
    public void reload(CommandSender sender) {
        try {
            ItemMakerPlugin.pluginConfiguration().load();
            sender.sendMessage("Loaded "+ItemMakerPlugin.instance().getItemManager().loadItems()+" items");
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        ItemMakerPlugin.instance().getTranslationManager().loadTranslations();
    }

    @Subcommand("test")
    @CommandCompletion("@items")
    public void test(CommandSender sender, String key) {
        ItemManager manager = ItemMakerPlugin.instance().getItemManager();
        Item item = manager.getItem(Key.of(key));
        if (item == null) {
            sender.sendMessage("item with id '"+key+"' does not exist");
            return;
        }

        ItemStack itemStack = item.asItemStack();
        if (sender instanceof Player player) {
            player.getInventory().addItem(itemStack);
            player.sendMessage("item was added to your inventory");
        }
    }

    @Subcommand("changelocale")
    public void changeLocale(CommandSender sender, String locale) {
        if (sender instanceof Player player) {
            TranslationManager manager = ItemMakerPlugin.instance().getTranslationManager();
            String[] split = locale.split("\\Q_\\E");
            Locale locale2 = split.length > 1 ? new Locale(split[0], split[1]) : new Locale(split[0]);
            manager.setPlayerLocale(player, locale2);
        }
    }
}
