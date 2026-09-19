package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Bukkit;

public class WitherMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player) { 
        player.removePotionEffect(PotionEffectType.WITHER);
    }
    
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability1", 20000); 
        
        for(int i = 0; i < 2; i++) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                player.launchProjectile(WitherSkull.class);
                playCast(player);
            }, i * 5L);
        }
    }
    
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability2", 40000);
        
        for(org.bukkit.entity.LivingEntity e : player.getLocation().getNearbyLivingEntities(10)) {
            if(!e.equals(player)) {
                e.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.WITHER, 240, 1));
            }
        }
        playCast(player);
    }
}
