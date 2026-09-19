package de.priyme2.mobsmp.listeners;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.utils.Msg;
import de.priyme2.mobsmp.models.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public class TokenListener implements Listener {
    private final MobSMP plugin;

    public TokenListener(MobSMP plugin) { this.plugin = plugin; }
    
    @EventHandler public void onRespawn(PlayerRespawnEvent e) {
        plugin.getServer().getScheduler().runTask(plugin, () -> {
            plugin.getTokenManager().giveTokenIfNotPresent(e.getPlayer());
        });
    }

    @EventHandler public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        if(e.getItem() == null) return;
        if(plugin.getTokenManager().isToken(e.getItem())) {
            e.setCancelled(true);
            if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                PlayerData d = plugin.getStorageManager().getData(p.getUniqueId());
                if(d.mobClass == null) return;

                var mech = plugin.getAbilityHandler().get(d.mobClass);
                if(p.isSneaking()) {
                    if(d.kills >= 5) {
                        if(!plugin.getAbilityHandler().isOnCooldown(p.getUniqueId(), "ability2")) {
                            mech.onAbilityUnlock2(p, e);
                            // Cooldown logic handled in mechanics normally, but we can set a default or mechanics call setCooldown
                        } else {
                            p.sendMessage(Msg.warn("Cooldown!"));
                        }
                    } else {
                        p.sendMessage(Msg.error("Du brauchst 5 Kills!"));
                    }
                } else {
                    if(d.kills >= 3) {
                        if(!plugin.getAbilityHandler().isOnCooldown(p.getUniqueId(), "ability1")) {
                            mech.onAbilityUnlock1(p, e);
                        } else {
                            p.sendMessage(Msg.warn("Cooldown!"));
                        }
                    } else {
                        p.sendMessage(Msg.error("Du brauchst 3 Kills!"));
                    }
                }
            }
        }
    }

    @EventHandler public void onDrop(PlayerDropItemEvent e) {
        if(plugin.getTokenManager().isToken(e.getItemDrop().getItemStack())) e.setCancelled(true);
    }
    
    @EventHandler public void onSpawn(ItemSpawnEvent e) {
        if(plugin.getTokenManager().isToken(e.getEntity().getItemStack())) e.setCancelled(true);
    }

    @EventHandler public void onCraft(CraftItemEvent e) {
        for(ItemStack item : e.getInventory().getMatrix()) {
            if(plugin.getTokenManager().isToken(item)) {
                e.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler public void onClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null) return;
        
        // Prevent moving token via number key (hotbar swap)
        if(e.getClick() == org.bukkit.event.inventory.ClickType.NUMBER_KEY) {
            ItemStack hotbarItem = e.getWhoClicked().getInventory().getItem(e.getHotbarButton());
            if(hotbarItem != null && plugin.getTokenManager().isToken(hotbarItem) && e.getClickedInventory().getType() != org.bukkit.event.inventory.InventoryType.PLAYER) {
                e.setCancelled(true);
                return;
            }
        }

        boolean isTokenCurrent = e.getCurrentItem() != null && plugin.getTokenManager().isToken(e.getCurrentItem());
        boolean isTokenCursor = e.getCursor() != null && plugin.getTokenManager().isToken(e.getCursor());

        if (isTokenCurrent || isTokenCursor) {
            // If they are interacting with an inventory that is not their own (like a chest, bounty GUI, etc.)
            if (e.getView().getTopInventory().getType() != org.bukkit.event.inventory.InventoryType.CRAFTING) {
                // If they shift click
                if (e.isShiftClick() && e.getClickedInventory().getType() == org.bukkit.event.inventory.InventoryType.PLAYER) {
                    e.setCancelled(true);
                }
                // If they click directly in the top inventory
                if (e.getClickedInventory().getType() != org.bukkit.event.inventory.InventoryType.PLAYER) {
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler public void onDrag(org.bukkit.event.inventory.InventoryDragEvent e) {
        if((e.getOldCursor() != null && plugin.getTokenManager().isToken(e.getOldCursor())) || 
           (e.getCursor() != null && plugin.getTokenManager().isToken(e.getCursor()))) {
            for(int slot : e.getRawSlots()) {
                if(slot < e.getView().getTopInventory().getSize() && e.getView().getTopInventory().getType() != org.bukkit.event.inventory.InventoryType.CRAFTING) {
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler public void onInteractEntity(org.bukkit.event.player.PlayerInteractEntityEvent e) {
        if(e.getPlayer().getInventory().getItem(e.getHand()) != null && 
           plugin.getTokenManager().isToken(e.getPlayer().getInventory().getItem(e.getHand()))) {
            if(e.getRightClicked() instanceof org.bukkit.entity.ItemFrame || e.getRightClicked() instanceof org.bukkit.entity.ArmorStand) {
                e.setCancelled(true);
            }
        }
    }
}
