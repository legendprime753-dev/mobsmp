package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

public class EndermanMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player){ effect(player,PotionEffectType.SPEED,2,1); }
    @Override public void onTick(Player player){ if(player.getLocation().getBlock().getType()== Material.WATER || player.getWorld().hasStorm()) player.damage(1.0); }
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event){ player.teleport(player.getLocation().add(player.getLocation().getDirection().multiply(8))); }
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event){ Player t=rayTargetPlayer(player,15); if(t!=null) player.teleport(t.getLocation().subtract(t.getLocation().getDirection())); }
}
