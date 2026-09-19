package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Material;
import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Bukkit;

public class ZombieMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player) { 
        long time = player.getWorld().getTime();
        int light = player.getLocation().getBlock().getLightLevel();
        if(time > 13000 || light < 7) {
            effect(player, PotionEffectType.STRENGTH, 3, 0);
        }
        
        if(time < 13000 && light >= 14 && player.getWorld().getEnvironment() == org.bukkit.World.Environment.NORMAL && !player.getWorld().hasStorm()) {
            if(player.getInventory().getHelmet() == null) {
                player.setFireTicks(60);
            }
        }
    }
    
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability1", 45000); 
        
        for(int i=0; i<3; i++) {
            Zombie z = player.getWorld().spawn(player.getLocation(), Zombie.class);
            z.setMetadata("summoner", new org.bukkit.metadata.FixedMetadataValue(plugin, player.getUniqueId().toString()));
            Bukkit.getScheduler().runTaskLater(plugin, () -> { if(z.isValid()) z.remove(); }, 30*20L);
        }
        playCast(player);
    }
    
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability2", 30000);
        
        // This effect will be processed in a generic listener checking if a player has a pending tag
        // We can just add a metadata tag to the player
        player.setMetadata("zombie_infect", new org.bukkit.metadata.FixedMetadataValue(plugin, true));
        player.sendMessage(de.priyme2.mobsmp.utils.Msg.warn("Naechster Hit infiziert!"));
        playCast(player);
    }
}
