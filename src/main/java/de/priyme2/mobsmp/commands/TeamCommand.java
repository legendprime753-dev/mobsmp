package de.priyme2.mobsmp.commands;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.Team;
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
import java.util.UUID;

public class TeamCommand implements CommandExecutor, TabCompleter {
    private final MobSMP plugin;

    public TeamCommand(MobSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Players only!");
            return true;
        }
        if (args.length == 0) {
            sendHelp(player);
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "create" -> {
                if (args.length < 2) { player.sendMessage(ChatColor.RED + "Usage: /team create <name>"); return true; }
                plugin.getTeamManager().createTeam(player, args[1]);
            }
            case "invite" -> {
                if (args.length < 2) { player.sendMessage(ChatColor.RED + "Usage: /team invite <player>"); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { player.sendMessage(ChatColor.RED + "Player not found!"); return true; }
                plugin.getTeamManager().invitePlayer(player, target);
            }
            case "accept" -> {
                plugin.getTeamManager().acceptInvite(player);
            }
            case "leave" -> {
                plugin.getTeamManager().leaveTeam(player);
            }
            case "disband" -> {
                plugin.getTeamManager().disbandTeam(player);
            }
            case "info" -> {
                Team team;
                if (args.length >= 2) {
                    team = plugin.getTeamManager().getTeamByName(args[1]);
                    if (team == null) { player.sendMessage(ChatColor.RED + "Team not found!"); return true; }
                } else {
                    team = plugin.getTeamManager().getTeam(player.getUniqueId());
                    if (team == null) { player.sendMessage(ChatColor.YELLOW + "You are not in a team."); return true; }
                }
                player.sendMessage(ChatColor.GOLD + "=== Team: " + team.getName() + " ===");
                player.sendMessage(ChatColor.YELLOW + "Leader: " + getPlayerName(team.getLeader()));
                StringBuilder members = new StringBuilder();
                for (UUID uuid : team.getMembers()) members.append(getPlayerName(uuid)).append(", ");
                String mStr = members.length() > 0 ? members.substring(0, members.length() - 2) : "";
                player.sendMessage(ChatColor.YELLOW + "Members (" + team.getSize() + "): " + mStr);
            }
            case "chat" -> {
                if (args.length < 2) {
                    player.sendMessage(ChatColor.RED + "Usage: /team chat <message>");
                    return true;
                }
                String message = String.join(" ", java.util.Arrays.copyOfRange(args, 1, args.length));
                plugin.getTeamManager().sendTeamChat(player, message);
            }
            default -> sendHelp(player);
        }
        return true;
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "Team Commands:");
        player.sendMessage("/team create <name>");
        player.sendMessage("/team invite <player>");
        player.sendMessage("/team accept");
        player.sendMessage("/team leave");
        player.sendMessage("/team disband");
        player.sendMessage("/team info [name]");
        player.sendMessage("/team chat <message>");
    }

    private String getPlayerName(UUID uuid) {
        Player p = Bukkit.getPlayer(uuid);
        if (p != null) return p.getName();
        return Bukkit.getOfflinePlayer(uuid).getName() != null ? Bukkit.getOfflinePlayer(uuid).getName() : uuid.toString();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) return Collections.emptyList();

        if (args.length == 1) {
            return filterPrefix(args[0], List.of("create", "invite", "accept", "leave", "info", "disband", "chat"));
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        if (args.length == 2) {
            return switch (sub) {
                case "invite" -> filterPrefix(args[1], onlinePlayerNames());
                case "info" -> filterPrefix(args[1], new ArrayList<>(plugin.getTeamManager().getAllTeams().keySet()));
                default -> Collections.emptyList();
            };
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
