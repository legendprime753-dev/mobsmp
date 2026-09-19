package de.priyme2.mobsmp.listeners;

import de.priyme2.mobsmp.MobSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.entity.Player;

public class PlayerDeathListener implements Listener {
    private final MobSMP plugin;

    public PlayerDeathListener(MobSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        plugin.getKillManager().onDeath(player.getUniqueId());
        player.showTitle(Title.title(
                Component.text("§4DU BIST GESTORBEN"),
                Component.text("§cProgression verloren")
        ));
        player.playSound(player.getLocation(), Sound.ENTITY_WITHER_DEATH, 0.8f, 1.3f);
    }
}
