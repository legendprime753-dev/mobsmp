package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.player.PlayerInteractEvent;

public class WitherMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player){ setMaxHealth(player,15); }
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event){ player.launchProjectile(WitherSkull.class); }
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event){ for(Player p: player.getWorld().getPlayers()) if(!p.equals(player)&&p.getLocation().distanceSquared(player.getLocation())<=100) effect(p,org.bukkit.potion.PotionEffectType.WITHER,5,1); }
}
