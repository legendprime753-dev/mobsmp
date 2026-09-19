package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class TokenManager {
    private final MobSMP plugin;
    public final NamespacedKey tokenKey;

    public TokenManager(MobSMP plugin) {
        this.plugin = plugin;
        this.tokenKey = new NamespacedKey(plugin, "mob_token");
    }

    public void giveTokenIfNotPresent(Player p) {
        for(ItemStack item : p.getInventory().getContents()) {
            if(isToken(item)) return;
        }
        p.getInventory().addItem(createToken());
    }

    public ItemStack createToken() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        meta.displayName(Component.text("ᴍᴏʙ ᴛᴏᴋᴇɴ", NamedTextColor.GOLD, TextDecoration.BOLD).decoration(TextDecoration.ITALIC, false));
        meta.getPersistentDataContainer().set(tokenKey, PersistentDataType.BYTE, (byte)1);
        item.setItemMeta(meta);
        return item;
    }

    public boolean isToken(ItemStack item) {
        if(item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(tokenKey, PersistentDataType.BYTE);
    }
}
