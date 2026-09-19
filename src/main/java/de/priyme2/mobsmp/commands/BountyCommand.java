package de.priyme2.mobsmp.commands;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.Bounty;
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
import java.util.Map;
import java.util.UUID;

public class BountyCommand implements CommandExecutor, TabCompleter {
    private final MobSMP plugin;

    public BountyCommand(MobSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /bounty <set|list|info>");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "set" -> {
                if (!(sender instanceof Player player)) { sender.sendMessage(ChatColor.RED + "Players only!"); return true; }
                if (args.length < 2) { sender.sendMessage(ChatColor.RED + "Usage: /bounty set <player>"); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { sender.sendMessage(ChatColor.RED + "Player not found!"); return true; }
                plugin.getBountyManager().setBounty(player, target);
            }
            case "list" -> {
                Map<UUID, Bounty> bounties = plugin.getBountyManager().getAllBounties();
                if (bounties.isEmpty()) {
                    sender.sendMessage(ChatColor.YELLOW + "No active bounties.");
                    return true;
                }
                sender.sendMessage(ChatColor.GOLD + "=== Active Bounties ===");
                for (Map.Entry<UUID, Bounty> entry : bounties.entrySet()) {
                    String name = Bukkit.getOfflinePlayer(entry.getKey()).getName();
                    sender.sendMessage(ChatColor.YELLOW + (name != null ? name : entry.getKey().toString()) +
                            ChatColor.GRAY + " - " + entry.getValue().getContributorCount() + " contributor(s)");
                }
            }
            case "info" -> {
                if (args.length < 2) { sender.sendMessage(ChatColor.RED + "Usage: /bounty info <player>"); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { sender.sendMessage(ChatColor.RED + "Player not found or offline!"); return true; }
                Bounty bounty = plugin.getBountyManager().getBounty(target.getUniqueId());
                if (bounty == null) {
                    sender.sendMessage(ChatColor.YELLOW + target.getName() + " has no bounty.");
                    return true;
                }
                sender.sendMessage(ChatColor.GOLD + "Bounty on " + target.getName() + ": " + bounty.getContributorCount() + " contributor(s)");
            }
            default -> sender.sendMessage(ChatColor.RED + "Usage: /bounty <set|list|info>");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filterPrefix(args[0], List.of("set", "list", "info"));
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase(Locale.ROOT);
            if (sub.equals("set") || sub.equals("info")) {
                return filterPrefix(args[1], onlinePlayerNames());
            }
        }
        return Collections.emptyList();
    }

    private List<String> onlinePlayerNames() {
        List<String> names = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            names.add(player.getName());
        }
        return names;
    }

    private List<String> filterPrefix(String input, List<String> values) {
        String lower = input.toLowerCase(Locale.ROOT);
        List<String> out = new ArrayList<>();
        for (String value : values) {
            if (value.toLowerCase(Locale.ROOT).startsWith(lower)) {
                out.add(value);
            }
        }
        return out;
    }
}
