package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.entity.Fireball;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Bukkit;

public class BlazeMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player) { 
        effect(player, PotionEffectType.FIRE_RESISTANCE, 3, 0);
        if(player.isInWater() || player.getWorld().hasStorm() && player.getWorld().getHighestBlockYAt(player.getLocation()) <= player.getLocation().getBlockY()) {
            player.damage(1);
        }
    }
    
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability1", 20000); 
        
        for(int i=0; i<3; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.launchProjectile(org.bukkit.entity.SmallFireball.class);
                playCast(player);
            }, i * 5L);
        }
    }
    
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability2", 40000);
        
        for(org.bukkit.entity.LivingEntity e : player.getLocation().getNearbyLivingEntities(10)) {
            if(!e.equals(player)) {
                e.setFireTicks(200); // 10s
            }
        }
        playCast(player);
    }
}
