package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlazeMechanic extends BaseMobMechanic {
    @Override public void onTick(Player player){ if(player.getLocation().getBlock().getType()== Material.WATER || player.getWorld().hasStorm()) player.damage(1.0); player.setFireTicks(0); }
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event){ for(int i=0;i<3;i++) player.launchProjectile(Fireball.class); }
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event){ for(Player p: player.getWorld().getPlayers()) if(!p.equals(player)&&p.getLocation().distanceSquared(player.getLocation())<=100) p.setFireTicks(200); }
}
