package de.priyme2.mobsmp.listeners;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.MobClass;
import de.priyme2.mobsmp.models.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerJoinListener implements Listener {
    private final MobSMP plugin;

    public PlayerJoinListener(MobSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getMobClassManager().loadPlayer(player);
        PlayerData data = plugin.getMobClassManager().getPlayerData(player.getUniqueId());

        if (data != null) {
            MobClass cls = data.getMobClass();

            // Apply class attributes
            if (cls != null) {
                plugin.getAbilityManager().applyClassAttributes(player, cls);
                player.sendMessage(ChatColor.GOLD + "Your mob class: " + cls.getColoredName());
            }
            plugin.getAbilityHandler().ensureAbilityActivator(player);

            // Restore reduced health if needed
            if (data.isReducedMaxHealth()) {
                int hearts = plugin.getConfig().getInt("reduced-max-health-hearts", 7);
                double hp = hearts * 2.0;
                var attr = player.getAttribute(Attribute.MAX_HEALTH);
                if (attr != null) {
                    attr.setBaseValue(hp);
                    if (player.getHealth() > hp) player.setHealth(hp);
                }
            }

            // Register in team scoreboard if in team
            plugin.getTeamManager().getTeam(player.getUniqueId()); // just access to ensure loaded

            if (!player.hasPlayedBefore() && cls != null) {
                player.showTitle(Title.title(
                        Component.text("§5DEINE KLASSE"),
                        Component.text(cls.getColoredName())
                ));
                player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 1.0f);
                player.sendActionBar(Component.text("§dWillkommen in MobSMP! Nutze den Ability Activator."));
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        plugin.getMobClassManager().unloadPlayer(event.getPlayer().getUniqueId());
    }
}
