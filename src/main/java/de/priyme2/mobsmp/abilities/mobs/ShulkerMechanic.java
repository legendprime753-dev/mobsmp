package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Bukkit;

public class ShulkerMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player) { 
        effect(player, PotionEffectType.RESISTANCE, 3, 1);
        effect(player, PotionEffectType.SLOWNESS, 3, 1);
    }
    
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability1", 20000); 
        
        Player target = rayTargetPlayer(player, 20);
        if(target == null) {
            // Find nearest player if no ray target
            double closest = Double.MAX_VALUE;
            for(Player p : player.getWorld().getPlayers()) {
                if(p.equals(player)) continue;
                double dist = p.getLocation().distanceSquared(player.getLocation());
                if(dist < 400 && dist < closest) { closest = dist; target = p; }
            }
        }
        if(target != null) {
            ShulkerBullet b = player.getWorld().spawn(player.getEyeLocation().add(player.getLocation().getDirection()), ShulkerBullet.class);
            b.setShooter(player);
            b.setTarget(target);
        } else {
            player.sendMessage(de.priyme2.mobsmp.utils.Msg.error("Kein Ziel gefunden!"));
        }
        playCast(player);
    }
    
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability2", 45000);
        
        for(org.bukkit.entity.LivingEntity e : player.getLocation().getNearbyLivingEntities(8)) {
            if(!e.equals(player)) {
                e.addPotionEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.LEVITATION, 100, 1));
            }
        }
        playCast(player);
    }
}
