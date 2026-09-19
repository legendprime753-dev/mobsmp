package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.PlayerData;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.UUID;

public class StatsManager {
    private final MobSMP plugin;

    public StatsManager(MobSMP plugin) {
        this.plugin = plugin;
    }

    public void recordBlockPlace(UUID uuid) {
        PlayerData data = plugin.getMobClassManager().getPlayerData(uuid);
        if (data != null) data.setBlocksPlaced(data.getBlocksPlaced() + 1);
    }

    public void recordBlockBreak(UUID uuid) {
        PlayerData data = plugin.getMobClassManager().getPlayerData(uuid);
        if (data != null) data.setBlocksBroken(data.getBlocksBroken() + 1);
    }

    public void recordMobKill(UUID uuid) {
        PlayerData data = plugin.getMobClassManager().getPlayerData(uuid);
        if (data != null) {
            data.setMobKills(data.getMobKills() + 1);
            data.setTotalKills(data.getTotalKills() + 1);
        }
    }

    public void sendStats(Player viewer, Player target) {
        PlayerData data = plugin.getMobClassManager().getPlayerData(target.getUniqueId());
        if (data == null) {
            viewer.sendMessage(ChatColor.RED + "No data found for " + target.getName());
            return;
        }
        viewer.sendMessage(ChatColor.GOLD + "=== Stats for " + target.getName() + " ===");
        viewer.sendMessage(ChatColor.YELLOW + "Total Kills: " + data.getTotalKills());
        viewer.sendMessage(ChatColor.YELLOW + "Player Kills: " + data.getPlayerKills());
        viewer.sendMessage(ChatColor.YELLOW + "Mob Kills: " + data.getMobKills());
        viewer.sendMessage(ChatColor.YELLOW + "Deaths: " + data.getTotalDeaths());
        viewer.sendMessage(ChatColor.YELLOW + "Blocks Placed: " + data.getBlocksPlaced());
        viewer.sendMessage(ChatColor.YELLOW + "Blocks Broken: " + data.getBlocksBroken());
    }
}
