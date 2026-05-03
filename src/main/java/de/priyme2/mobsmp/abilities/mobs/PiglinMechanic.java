package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

public class PiglinMechanic extends BaseMobMechanic {
    @Override public void onTick(Player player){ if(player.getInventory().getItemInMainHand().getType().name().endsWith("_AXE")) effect(player,PotionEffectType.SPEED,2,1); }
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event){ effect(player,PotionEffectType.STRENGTH,15,1); }
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event){ player.getWorld().spawnEntity(player.getLocation(), EntityType.PIGLIN_BRUTE); player.getWorld().spawnEntity(player.getLocation(), EntityType.PIGLIN_BRUTE); }
}
