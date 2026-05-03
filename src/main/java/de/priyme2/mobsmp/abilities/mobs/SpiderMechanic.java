package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

public class SpiderMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player){ effect(player,PotionEffectType.SPEED,2,1); setMaxHealth(player,18); }
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event){ Player t=rayTargetPlayer(player,15); if(t!=null) effect(t,PotionEffectType.POISON,6,1); }
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event){ Player t=rayTargetPlayer(player,20); if(t!=null) t.getLocation().getBlock().setType(Material.COBWEB); }
}
