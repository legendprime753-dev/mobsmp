package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

public class ChickenMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player) { effect(player, PotionEffectType.SLOW_FALLING, 2, 0); setMaxHealth(player, 18.0); }
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { Egg egg = player.launchProjectile(Egg.class); egg.setVelocity(player.getLocation().getDirection().multiply(1.6)); playCast(player); }
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { damageNearby(player, 5, 4, player.getLocation().getDirection().multiply(0.8)); }
}
