package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

public class ShulkerMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player){ effect(player,PotionEffectType.RESISTANCE,2,1); effect(player,PotionEffectType.SLOWNESS,2,1); }
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event){ Player t=rayTargetPlayer(player,18); if(t!=null) effect(t,PotionEffectType.LEVITATION,4,0); }
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event){ effect(player,PotionEffectType.RESISTANCE,5,10); effect(player,PotionEffectType.SLOWNESS,5,10); }
}
