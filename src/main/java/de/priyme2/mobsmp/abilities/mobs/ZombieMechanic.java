package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

public class ZombieMechanic extends BaseMobMechanic {
    @Override public void onTick(Player player){ if(player.getLocation().getBlock().getLightLevel()<7) effect(player,PotionEffectType.STRENGTH,2,0); if(player.getWorld().isDayTime()&&player.getInventory().getHelmet()==null) player.setFireTicks(40); }
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { for(int i=0;i<3;i++) player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE); }
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { Player t = rayTargetPlayer(player,4); if(t!=null){ effect(t,PotionEffectType.HUNGER,5,2); effect(t,PotionEffectType.WITHER,5,0);} }
}
