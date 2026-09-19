package de.priyme2.mobsmp.listeners;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.managers.RerollManager;
import de.priyme2.mobsmp.models.MobClass;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class InventoryListener implements Listener {
    private final MobSMP plugin;

    public InventoryListener(MobSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        String title = event.getView().getTitle();
        if (!title.equals(RerollManager.REROLL_GUI_TITLE)) return;
        event.setCancelled(true);

        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;

        String className = clicked.getItemMeta().getPersistentDataContainer()
                .get(new NamespacedKey(plugin, "reroll_class"), PersistentDataType.STRING);
        if (className == null) return;

        try {
            MobClass cls = MobClass.valueOf(className);
            player.closeInventory();
            // Remove reroll book from inventory
            ItemStack[] contents = player.getInventory().getContents();
            for (int i = 0; i < contents.length; i++) {
                if (plugin.getRerollManager().isRerollBook(contents[i])) {
                    if (contents[i].getAmount() > 1) {
                        contents[i].setAmount(contents[i].getAmount() - 1);
                    } else {
                        player.getInventory().setItem(i, null);
                    }
                    break;
                }
            }
            plugin.getMobClassManager().setMobClass(player.getUniqueId(), cls);
            player.sendMessage(ChatColor.GREEN + "You are now a " + cls.getColoredName() + ChatColor.GREEN + "!");
        } catch (IllegalArgumentException ignored) {}
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = (Player) event.getPlayer(); if (player == null) return;
        ItemStack item = event.getItem();
        if (item == null) return;

        // Ability Activator
        if (plugin.getAbilityHandler().isAbilityActivator(item)) {
            if (event.getAction().name().contains("RIGHT_CLICK")) {
                event.setCancelled(true);
                plugin.getAbilityHandler().handleRightClick(player, player.isSneaking());
            }
            return;
        }

        // Reroll Book
        if (plugin.getRerollManager().isRerollBook(item)) {
            if (event.getAction().name().contains("RIGHT_CLICK")) {
                event.setCancelled(true);
                player.openInventory(plugin.getRerollManager().createRerollGUI());
            }
        }
    }
}
