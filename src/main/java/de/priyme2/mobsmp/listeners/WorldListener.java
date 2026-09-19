package de.priyme2.mobsmp.listeners;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.MobClass;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

public class WorldListener implements Listener {
    private final MobSMP plugin;

    public WorldListener(MobSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Material type = event.getBlock().getType();

        // End Crystal: block placement
        if (type == Material.END_CRYSTAL) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "End Crystals cannot be placed!");
            return;
        }

        // Cobwebs: only SPIDER class
        if (type == Material.COBWEB) {
            MobClass cls = plugin.getMobClassManager().getMobClass(player.getUniqueId());
            if (cls != MobClass.SPIDER) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "Only Spider class can place cobwebs!");
                return;
            }
        }

        // Mace limit
        if (type == Material.MACE) {
            // Mace is a weapon, not a block, so this won't trigger. Handled elsewhere.
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        plugin.getStatsManager().recordBlockBreak(player.getUniqueId());

        // SPECIAL_DROPS event: double drops
        if (plugin.getEventManager().isSpecialDropsActive()) {
            for (ItemStack drop : event.getBlock().getDrops(player.getInventory().getItemInMainHand())) {
                player.getWorld().dropItemNaturally(event.getBlock().getLocation(), drop);
            }
        }
    }

    @EventHandler
    public void onBlockPlaceStats(BlockPlaceEvent event) {
        plugin.getStatsManager().recordBlockPlace(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInHand = event.getItem();
        Action action = event.getAction();

        // End Crystals: fully blocked
        if (itemInHand != null && itemInHand.getType() == Material.END_CRYSTAL &&
                action != null &&
                action.isRightClick()) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "End Crystals are disabled on this server!");
            return;
        }

        if (event.getClickedBlock() == null) return;
        Material type = event.getClickedBlock().getType();

        // Respawn Anchors: fully blocked
        if (type == Material.RESPAWN_ANCHOR) {
            event.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Respawn Anchors are disabled on this server!");
        }
    }

    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() == null) return;
        ItemStack result = event.getRecipe().getResult();
        if (result == null) return;
        Material type = result.getType();
        if (type == Material.END_CRYSTAL || type == Material.RESPAWN_ANCHOR) {
            event.getInventory().setResult(null);
        }
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Item item = event.getItem();
        ItemStack stack = item.getItemStack();

        // Ender Pearl limit
        if (stack.getType() == Material.ENDER_PEARL) {
            int limit = plugin.getConfig().getInt("enderperls-per-player-limit", 8);
            int current = countItem(player, Material.ENDER_PEARL);
            if (current >= limit) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You can only carry " + limit + " ender pearls!");
            }
        }

        // Mace limit
        if (stack.getType() == Material.MACE) {
            int limit = plugin.getConfig().getInt("mace-limit-per-player", 1);
            int current = countItem(player, Material.MACE);
            if (current >= limit) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You can only carry " + limit + " mace(s)!");
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        // SPECIAL_DROPS: double mob drops
        if (plugin.getEventManager().isSpecialDropsActive()) {
            for (ItemStack drop : new java.util.ArrayList<>(event.getDrops())) {
                event.getDrops().add(drop.clone());
            }
        }

        // Track mob kills for player killers
        if (event.getEntity().getKiller() != null) {
            Player killer = event.getEntity().getKiller();
            if (!(event.getEntity() instanceof Player)) {
                plugin.getStatsManager().recordMobKill(killer.getUniqueId());
            }
        }
    }

    private int countItem(Player player, Material material) {
        int count = 0;
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && item.getType() == material) {
                count += item.getAmount();
            }
        }
        return count;
    }
}
