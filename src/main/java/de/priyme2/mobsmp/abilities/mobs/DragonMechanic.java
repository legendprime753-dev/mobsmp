package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.Material;
import org.bukkit.Color;
import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Bukkit;

public class DragonMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player) { 
        player.setAllowFlight(true);
        setMaxHealth(player, 26.0); // 13 hearts
    }
    
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability1", 30000); 
        
        AreaEffectCloud cloud = player.getWorld().spawn(player.getLocation().add(player.getLocation().getDirection().multiply(2)), AreaEffectCloud.class);
        cloud.setRadius(3.0f);
        cloud.setDuration(100);
        cloud.setColor(Color.PURPLE);
        cloud.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 0), true);
        playCast(player);
    }
    
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability2", 60000);
        
        damageNearby(player, 8, 4, player.getLocation().getDirection().multiply(1.5).setY(0.5));
        player.getWorld().playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
        playCast(player);
    }
}
