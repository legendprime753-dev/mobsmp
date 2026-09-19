package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.player.PlayerInteractEvent;
import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Bukkit;

public class CreeperMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player) { }
    
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability1", 20000); 
        
        TNTPrimed tnt = player.getWorld().spawn(player.getLocation(), TNTPrimed.class);
        tnt.setFuseTicks(40);
        tnt.setMetadata("no_damage_" + player.getUniqueId(), new org.bukkit.metadata.FixedMetadataValue(plugin, true));
        playCast(player);
    }
    
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability2", 40000);
        
        player.setMetadata("creeper_lightning", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
        player.sendMessage(de.priyme2.mobsmp.utils.Msg.warn("Naechster Hit erzeugt einen Blitz!"));
        playCast(player);
    }
}
