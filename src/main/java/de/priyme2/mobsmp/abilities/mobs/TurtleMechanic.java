package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class TurtleMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player) { effect(player, PotionEffectType.DOLPHINS_GRACE, 2, 0); }
    @Override public void onTick(Player player){ if(player.getLocation().getBlock().getType()!= Material.WATER) effect(player, PotionEffectType.SLOWNESS,2,0); }
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { effect(player, PotionEffectType.RESISTANCE, 8, 2); playCast(player); }
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { damageNearby(player, 5, 2, new Vector(0,0.7,0)); }
}
