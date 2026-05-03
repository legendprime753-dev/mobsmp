package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.player.PlayerInteractEvent;

public class CreeperMechanic extends BaseMobMechanic {
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { TNTPrimed tnt=player.getWorld().spawn(player.getLocation(), TNTPrimed.class); tnt.setFuseTicks(40); tnt.setSource(player); }
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { Player t = rayTargetPlayer(player,4); if(t!=null) t.getWorld().strikeLightning(t.getLocation()); }
}
