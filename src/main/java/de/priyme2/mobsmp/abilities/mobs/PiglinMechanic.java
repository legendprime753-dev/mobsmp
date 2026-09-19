package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Material;
import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Bukkit;

public class PiglinMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player) { 
        effect(player, PotionEffectType.STRENGTH, 3, 0); // Stärke 1
        if(player.getInventory().getItemInMainHand().getType().name().endsWith("_AXE")) {
            effect(player, PotionEffectType.SPEED, 3, 1);
        }
    }
    
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability1", 40000); 
        
        effect(player, PotionEffectType.STRENGTH, 15, 1);
        playCast(player);
    }
    
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability2", 60000);
        
        for(int i=0; i<2; i++) {
            PiglinBrute pb = player.getWorld().spawn(player.getLocation(), PiglinBrute.class);
            pb.setMetadata("summoner", new org.bukkit.metadata.FixedMetadataValue(plugin, player.getUniqueId().toString()));
            pb.setImmuneToZombification(true);
            Bukkit.getScheduler().runTaskLater(plugin, pb::remove, 20*20L);
        }
        playCast(player);
    }
}
