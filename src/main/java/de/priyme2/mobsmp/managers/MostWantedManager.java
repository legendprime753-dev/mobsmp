package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class MostWantedManager {
    private final MobSMP plugin;
    private int onlineSeconds = 0;
    private int intervalSeconds;
    private UUID mostWantedUUID = null;

    public MostWantedManager(MobSMP plugin) {
        this.plugin = plugin;
        this.intervalSeconds = plugin.getConfig().getInt("most-wanted-interval-minutes", 60) * 60;
        startTimer();
    }

    private void startTimer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                UUID mw = getMostWanted();
                mostWantedUUID = mw;
                if (mw == null) return;
                Player p = Bukkit.getPlayer(mw);
                if (p == null || !p.isOnline()) return;
                onlineSeconds++;
                if (onlineSeconds >= intervalSeconds) {
                    onlineSeconds = 0;
                    announceLocation(p);
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void announceLocation(Player player) {
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        int z = player.getLocation().getBlockZ();
        String world = player.getWorld().getName();
        Bukkit.broadcastMessage(ChatColor.RED + "[Most Wanted] " + ChatColor.WHITE + player.getName() +
                " is at X:" + x + " Y:" + y + " Z:" + z + " in " + world + "!");
    }

    private UUID getMostWanted() {
        UUID best = null;
        int bestKills = -1;
        long bestTimestamp = Long.MAX_VALUE;
        for (Map.Entry<UUID, PlayerData> entry : plugin.getMobClassManager().getAllData().entrySet()) {
            PlayerData data = entry.getValue();
            if (data.getKills() > bestKills ||
                    (data.getKills() == bestKills && data.getKillCountTimestamp() < bestTimestamp)) {
                bestKills = data.getKills();
                bestTimestamp = data.getKillCountTimestamp();
                best = entry.getKey();
            }
        }
        return bestKills > 0 ? best : null;
    }

    public UUID getMostWantedUUID() {
        return mostWantedUUID;
    }
}
