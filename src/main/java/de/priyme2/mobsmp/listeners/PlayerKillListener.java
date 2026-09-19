package de.priyme2.mobsmp.listeners;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerKillListener implements Listener {
    private final MobSMP plugin;

    public PlayerKillListener(MobSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerKill(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if (killer == null || killer.equals(victim)) return;

        PlayerData killerData = plugin.getMobClassManager().getPlayerData(killer.getUniqueId());
        if (killerData == null) return;

        // Check bounty first
        if (plugin.getBountyManager().hasBounty(victim.getUniqueId())) {
            plugin.getBountyManager().onBountyKilled(killer, victim);
        } else {
            // Normal kill: +1, +2 on Double Kills or Bounty Rush events
            int killAmount = (plugin.getEventManager().isDoubleKillsActive() || plugin.getEventManager().isBountyRushActive()) ? 2 : 1;
            plugin.getKillManager().addKill(killer.getUniqueId(), killAmount);
            PlayerData updatedData = plugin.getMobClassManager().getPlayerData(killer.getUniqueId());
            int totalKills = updatedData != null ? updatedData.getKills() : killerData.getKills();
            killer.sendMessage(ChatColor.GREEN + "+" + killAmount + " kill(s)! Total: " + totalKills);
            killer.sendActionBar(Component.text("§a+" + killAmount + " Kills  §7|  §f" + totalKills + " aktuell"));
        }

        // Track player kills
        PlayerData killerStats = plugin.getMobClassManager().getPlayerData(killer.getUniqueId());
        if (killerStats != null) killerStats.setPlayerKills(killerStats.getPlayerKills() + 1);

        // Drop mob soul at the victim location
        victim.getWorld().dropItemNaturally(victim.getLocation(), plugin.getRerollManager().createMobSoulItem());
        killer.sendMessage(ChatColor.LIGHT_PURPLE + "A Mob Soul dropped from your kill!");
        killer.playSound(killer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.1f);
        killer.showTitle(Title.title(
                Component.text("§6KILL"),
                Component.text("§eMob Soul erhalten")
        ));
    }
}
