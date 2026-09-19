package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Material;
import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Bukkit;

public class TurtleMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player) { 
        if(player.isInWater()) {
            effect(player, PotionEffectType.DOLPHINS_GRACE, 3, 0);
        } else {
            effect(player, PotionEffectType.SLOWNESS, 3, 0);
        }
    }
    
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability1", 20000); 
        
        effect(player, PotionEffectType.RESISTANCE, 8, 2); // Res 3 = amp 2
        playCast(player);
    }
    
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability2", 30000);
        
        damageNearby(player, 6, 2, player.getLocation().getDirection().multiply(1.5).setY(0.5));
        for(org.bukkit.entity.LivingEntity e : player.getLocation().getNearbyLivingEntities(6)) {
            if(!e.equals(player)) e.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
        }
        playCast(player);
    }
}
