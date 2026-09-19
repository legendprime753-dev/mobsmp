package de.priyme2.mobsmp.commands;

import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class StatsCommand implements CommandExecutor, TabCompleter {
    private final MobSMP plugin;

    public StatsCommand(MobSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player target;
        if (args.length >= 1) {
            target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found or offline!");
                return true;
            }
        } else {
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Usage: /stats <player>");
                return true;
            }
            target = (Player) sender;
        }
        if (sender instanceof Player viewer) {
            plugin.getStatsManager().sendStats(viewer, target);
            return true;
        }

        sender.sendMessage("=== Stats for " + target.getName() + " ===");
        var data = plugin.getMobClassManager().getPlayerData(target.getUniqueId());
        if (data == null) {
            sender.sendMessage("No data found for " + target.getName());
            return true;
        }
        sender.sendMessage("Total Kills: " + data.getTotalKills());
        sender.sendMessage("Player Kills: " + data.getPlayerKills());
        sender.sendMessage("Mob Kills: " + data.getMobKills());
        sender.sendMessage("Deaths: " + data.getTotalDeaths());
        sender.sendMessage("Blocks Placed: " + data.getBlocksPlaced());
        sender.sendMessage("Blocks Broken: " + data.getBlocksBroken());
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length != 1) return Collections.emptyList();
        String lower = args[0].toLowerCase(Locale.ROOT);
        List<String> result = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            String name = player.getName();
            if (name.toLowerCase(Locale.ROOT).startsWith(lower)) {
                result.add(name);
            }
        }
        return result;
    }
}
