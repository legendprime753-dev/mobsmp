package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

public class WardenMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player){ effect(player,PotionEffectType.RESISTANCE,2,0); setMaxHealth(player,30); effect(player,PotionEffectType.DARKNESS,2,0); }
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event){ Player t=rayTargetPlayer(player,20); if(t!=null) t.damage(8,player); }
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event){ for(Player p: player.getWorld().getPlayers()) if(p.getLocation().distanceSquared(player.getLocation())<=900 && !p.equals(player)) effect(p,PotionEffectType.DARKNESS,10,0); }
}
