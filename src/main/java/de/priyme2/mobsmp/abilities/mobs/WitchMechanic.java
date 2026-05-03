package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

public class WitchMechanic extends BaseMobMechanic {
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event){ Player t=rayTargetPlayer(player,15); if(t!=null) effect(t,PotionEffectType.WEAKNESS,8,1); }
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event){ player.setHealth(Math.min(player.getMaxHealth(), player.getHealth()+8)); effect(player,PotionEffectType.REGENERATION,10,1); }
}
