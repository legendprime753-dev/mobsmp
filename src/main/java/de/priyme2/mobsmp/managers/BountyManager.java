package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.utils.Msg;
import de.priyme2.mobsmp.models.Bounty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BountyManager implements Listener {
    private final MobSMP plugin;
    
    // Track which inventory maps to which target UUID
    private final Map<Inventory, UUID> openBountyGuis = new HashMap<>();
    
    public BountyManager(MobSMP plugin){
        this.plugin=plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    public void openBountyGUI(Player requester, Player target){
        if(requester.equals(target)) {
            requester.sendMessage(Msg.error("Du kannst kein Bounty auf dich selbst setzen!"));
            return;
        }
        Inventory inv = Bukkit.createInventory(null, 27, Component.text("Bounty: " + target.getName()));
        openBountyGuis.put(inv, target.getUniqueId());
        requester.openInventory(inv);
        requester.sendMessage(Msg.info("Lege Items in das GUI. Schliessen = bestaetigen."));
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        if(openBountyGuis.containsKey(inv)) {
            UUID targetId = openBountyGuis.remove(inv);
            Player target = Bukkit.getPlayer(targetId);
            
            boolean addedAnything = false;
            Bounty b = plugin.getStorageManager().getBounties().computeIfAbsent(targetId, Bounty::new);
            
            for(ItemStack item : inv.getContents()) {
                if(item != null && item.getType() != org.bukkit.Material.AIR) {
                    if(plugin.getTokenManager().isToken(item)) {
                        // Double protection: give token back if it somehow ended up in GUI
                        e.getPlayer().getInventory().addItem(item);
                    } else {
                        b.addReward(item);
                        addedAnything = true;
                    }
                }
            }
            
            if(addedAnything && target != null) {
                Bukkit.broadcast(Msg.warn(e.getPlayer().getName() + " hat das Bounty auf " + target.getName() + " erhoeht!"));
            } else if (!addedAnything && b.rewardItems.isEmpty()) {
                // Remove empty bounty
                plugin.getStorageManager().getBounties().remove(targetId);
            }
        }
    }
    
    public boolean hasBounty(UUID target){ return plugin.getStorageManager().getBounties().containsKey(target); }
    
    public void handleBountyKill(Player killer, Player target){ 
        if(!hasBounty(target.getUniqueId())) return;
        
        int kills = 2;
        if(plugin.getEventManager().isEventActive("BOUNTY_RUSH")) kills *= 2;
        
        plugin.getKillManager().addKill(killer, target, kills); 
        
        Bounty b = plugin.getStorageManager().getBounties().remove(target.getUniqueId());
        distributeLootToKiller(b, killer);
        Bukkit.broadcast(Msg.special("Bounty auf " + target.getName() + " von " + killer.getName() + " eingeloest!"));
    }

    private void distributeLootToKiller(Bounty b, Player killer) {
        if(b.rewardItems.isEmpty()) return;
        
        for(ItemStack item : b.rewardItems) {
            Map<Integer, ItemStack> left = killer.getInventory().addItem(item);
            for(ItemStack drop : left.values()) {
                killer.getWorld().dropItemNaturally(killer.getLocation(), drop);
            }
        }
        killer.sendMessage(Msg.success("Bounty-Belohnung erhalten!"));
    }
}
