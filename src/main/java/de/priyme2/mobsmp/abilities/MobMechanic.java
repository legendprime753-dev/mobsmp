package de.priyme2.mobsmp.abilities;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public interface MobMechanic {
    void applyPassiveStats(Player player);
    void onTick(Player player);
    void onAbilityUnlock1(Player player, PlayerInteractEvent event);
    void onAbilityUnlock2(Player player, PlayerInteractEvent event);
}
