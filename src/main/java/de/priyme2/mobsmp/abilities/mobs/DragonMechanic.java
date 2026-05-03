package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;

public class DragonMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player){ player.setAllowFlight(true); }
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event){ damageNearby(player,6,5,null); }
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event){ damageNearby(player,10,8,player.getLocation().getDirection().multiply(1.1)); }
}
