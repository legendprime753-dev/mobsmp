package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.entity.Chicken;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.metadata.FixedMetadataValue;
import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Bukkit;

public class ChickenMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player) { 
        effect(player, PotionEffectType.SLOW_FALLING, 2, 0); 
        setMaxHealth(player, 16.0); // 8 hearts
    }
    
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability1", 10000); // 10s
        
        Egg egg = player.launchProjectile(Egg.class); 
        egg.setVelocity(player.getLocation().getDirection().multiply(1.6)); 
        egg.setCustomName("ChickenBomb"); // Listen to projectile hit to apply blindness
        playCast(player); 
    }
    
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability2", 30000); // 30s
        
        for(int i=0; i<3; i++) {
            Chicken chicken = player.getWorld().spawn(player.getLocation(), Chicken.class);
            Zombie zombie = player.getWorld().spawn(player.getLocation(), Zombie.class);
            zombie.setBaby(true);
            chicken.addPassenger(zombie);
            zombie.setMetadata("summoner", new FixedMetadataValue(plugin, player.getUniqueId().toString()));
            chicken.setMetadata("summoner", new FixedMetadataValue(plugin, player.getUniqueId().toString()));
            Bukkit.getScheduler().runTaskLater(plugin, () -> { if(chicken.isValid()) chicken.remove(); if(zombie.isValid()) zombie.remove(); }, 30*20L);
        }
        playCast(player);
    }
}
