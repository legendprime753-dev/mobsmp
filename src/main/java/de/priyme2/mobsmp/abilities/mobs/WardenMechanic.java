package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Bukkit;

public class WardenMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player) { 
        effect(player, PotionEffectType.RESISTANCE, 3, 0);
        player.removePotionEffect(PotionEffectType.DARKNESS);
        setMaxHealth(player, 26.0); // 13 hearts
    }
    
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability1", 30000); 
        
        Player target = rayTargetPlayer(player, 15);
        if(target != null) {
            target.damage(8, player);
            target.setVelocity(player.getLocation().getDirection().multiply(1.5).setY(0.3));
        }
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_WARDEN_SONIC_BOOM, 1, 1);
        playCast(player);
    }
    
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability2", 60000);
        
        for(org.bukkit.entity.LivingEntity e : player.getLocation().getNearbyLivingEntities(30)) {
            if(!e.equals(player)) {
                e.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.DARKNESS, 200, 0));
            }
        }
        playCast(player);
    }
}
