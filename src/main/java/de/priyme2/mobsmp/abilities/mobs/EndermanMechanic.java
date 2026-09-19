package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.entity.EnderPearl;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Bukkit;

public class EndermanMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player) { 
        effect(player, PotionEffectType.SPEED, 3, 1);
        setMaxHealth(player, 26.0); // 13 hearts
        if(player.isInWater() || player.getWorld().hasStorm() && player.getWorld().getHighestBlockYAt(player.getLocation()) <= player.getLocation().getBlockY()) {
            player.damage(1);
        }
    }
    
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        int kills = plugin.getStorageManager().getData(player.getUniqueId()).kills;
        long cd = 15000 - (kills * 2000L); // 15s base, -2s per kill
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability1", Math.max(cd, 5000)); 
        
        player.launchProjectile(EnderPearl.class);
        playCast(player);
    }
    
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability2", 30000);
        
        Player target = rayTargetPlayer(player, 15);
        if(target != null) {
            org.bukkit.Location loc = target.getLocation().subtract(target.getLocation().getDirection().multiply(1.5));
            loc.setYaw(target.getLocation().getYaw());
            loc.setPitch(target.getLocation().getPitch());
            player.teleport(loc);
        }
        playCast(player);
    }
}
