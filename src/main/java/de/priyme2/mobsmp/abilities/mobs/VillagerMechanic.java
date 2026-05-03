package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

public class VillagerMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player) { effect(player, PotionEffectType.HERO_OF_THE_VILLAGE, 5, 0); }
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { effect(player, PotionEffectType.LUCK, 20, 0); playCast(player); }
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { IronGolem golem = (IronGolem) player.getWorld().spawnEntity(player.getLocation(), EntityType.IRON_GOLEM); golem.customName(player.name()); golem.setPlayerCreated(true); }
}
