package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class RerollManager {
    private final MobSMP plugin;
    public final NamespacedKey fragmentKey;
    public final NamespacedKey bookKey;

    public RerollManager(MobSMP plugin) {
        this.plugin = plugin;
        this.fragmentKey = new NamespacedKey(plugin, "kill_fragment");
        this.bookKey = new NamespacedKey(plugin, "reroll_book");
    }

    public ItemStack createFragment() {
        ItemStack item = new ItemStack(Material.AMETHYST_SHARD);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("ᴋɪʟʟ ꜰʀᴀɢᴍᴇɴᴛ", NamedTextColor.LIGHT_PURPLE).decoration(TextDecoration.ITALIC, false));
        meta.getPersistentDataContainer().set(fragmentKey, PersistentDataType.BYTE, (byte)1);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isFragment(ItemStack item) {
        if(item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(fragmentKey, PersistentDataType.BYTE);
    }

    public ItemStack createRerollBook() {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("ʀᴇʀᴏʟʟ ʙᴜᴄʜ", NamedTextColor.GOLD, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        meta.getPersistentDataContainer().set(bookKey, PersistentDataType.BYTE, (byte)1);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isRerollBook(ItemStack item) {
        if(item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(bookKey, PersistentDataType.BYTE);
    }
}
