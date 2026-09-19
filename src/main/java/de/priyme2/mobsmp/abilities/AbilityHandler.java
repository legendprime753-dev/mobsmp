package de.priyme2.mobsmp.abilities;

import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class AbilityHandler {
    private final MobSMP plugin;

    public AbilityHandler(MobSMP plugin) {
        this.plugin = plugin;
    }

    public ItemStack createAbilityActivatorItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.AQUA + "Ability Activator");
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "ability_activator"),
                    PersistentDataType.BOOLEAN, true
            );
            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean isAbilityActivator(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(new NamespacedKey(plugin, "ability_activator"), PersistentDataType.BOOLEAN);
    }

    public void handleRightClick(Player player, boolean sneaking) {
        if (sneaking) {
            plugin.getAbilityManager().useAbility2(player);
        } else {
            plugin.getAbilityManager().useAbility1(player);
        }
    }

    public void ensureAbilityActivator(Player player) {
        for (ItemStack item : player.getInventory().getContents()) {
            if (isAbilityActivator(item)) {
                return;
            }
        }
        player.getInventory().addItem(createAbilityActivatorItem());
        player.sendMessage(ChatColor.AQUA + "You received an Ability Activator! Right click = Ability 1, sneak + right click = Ability 2.");
    }
}
