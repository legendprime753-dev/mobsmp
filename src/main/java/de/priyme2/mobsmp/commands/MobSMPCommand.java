package de.priyme2.mobsmp.commands;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.managers.EventManager;
import de.priyme2.mobsmp.models.MobClass;
import de.priyme2.mobsmp.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class MobSMPCommand implements CommandExecutor, TabCompleter {
    private final MobSMP plugin;

    public MobSMPCommand(MobSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("mobsmp.admin")) {
            sender.sendMessage(ChatColor.RED + "No permission!");
            return true;
        }
        if (args.length == 0) {
            sender.sendMessage(ChatColor.GOLD + "MobSMP Admin Commands:");
            sender.sendMessage("/mobsmp reload");
            sender.sendMessage("/mobsmp setclass <player> <class>");
            sender.sendMessage("/mobsmp setkills <player> <amount>");
            sender.sendMessage("/mobsmp reset <player>");
            sender.sendMessage("/mobsmp info <player>");
            sender.sendMessage("/mobsmp event start <type>");
            sender.sendMessage("/mobsmp event stop");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload" -> {
                plugin.reloadConfig();
                sender.sendMessage(ChatColor.GREEN + "Config reloaded!");
            }
            case "setclass" -> {
                if (args.length < 3) { sender.sendMessage(ChatColor.RED + "Usage: /mobsmp setclass <player> <class>"); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { sender.sendMessage(ChatColor.RED + "Player not found!"); return true; }
                try {
                    MobClass cls = MobClass.valueOf(args[2].toUpperCase());
                    plugin.getMobClassManager().setMobClass(target.getUniqueId(), cls);
                    sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s class to " + cls.getDisplayName());
                    target.sendMessage(ChatColor.GOLD + "Your class has been set to " + cls.getColoredName());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid class! Valid: " + getClassList());
                }
            }
            case "setkills" -> {
                if (args.length < 3) { sender.sendMessage(ChatColor.RED + "Usage: /mobsmp setkills <player> <amount>"); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { sender.sendMessage(ChatColor.RED + "Player not found!"); return true; }
                try {
                    int amount = Integer.parseInt(args[2]);
                    PlayerData data = plugin.getMobClassManager().getPlayerData(target.getUniqueId());
                    if (data != null) {
                        data.setKills(amount);
                        sender.sendMessage(ChatColor.GREEN + "Set " + target.getName() + "'s kills to " + amount);
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Invalid number!");
                }
            }
            case "reset" -> {
                if (args.length < 2) { sender.sendMessage(ChatColor.RED + "Usage: /mobsmp reset <player>"); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { sender.sendMessage(ChatColor.RED + "Player not found!"); return true; }
                PlayerData data = plugin.getMobClassManager().getPlayerData(target.getUniqueId());
                if (data != null) {
                    data.setKills(0);
                    data.setDeaths(0);
                    data.setConsecutiveDeathsAtZeroKills(0);
                    data.setReducedMaxHealth(false);
                    data.setKillsForHealthRestore(0);
                    var attr = target.getAttribute(Attribute.MAX_HEALTH);
                    if (attr != null) attr.setBaseValue(20.0);
                    sender.sendMessage(ChatColor.GREEN + "Reset " + target.getName() + "'s data!");
                }
            }
            case "info" -> {
                if (args.length < 2) { sender.sendMessage(ChatColor.RED + "Usage: /mobsmp info <player>"); return true; }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) { sender.sendMessage(ChatColor.RED + "Player not found!"); return true; }
                PlayerData data = plugin.getMobClassManager().getPlayerData(target.getUniqueId());
                if (data == null) { sender.sendMessage(ChatColor.RED + "No data!"); return true; }
                sender.sendMessage(ChatColor.GOLD + "=== Info: " + target.getName() + " ===");
                sender.sendMessage(ChatColor.YELLOW + "Class: " + (data.getMobClass() != null ? data.getMobClass().getColoredName() : "None"));
                sender.sendMessage(ChatColor.YELLOW + "Kills: " + data.getKills());
                sender.sendMessage(ChatColor.YELLOW + "Deaths: " + data.getDeaths());
                sender.sendMessage(ChatColor.YELLOW + "Consec. Deaths at 0: " + data.getConsecutiveDeathsAtZeroKills());
                sender.sendMessage(ChatColor.YELLOW + "Reduced Health: " + data.isReducedMaxHealth());
            }
            case "event" -> {
                if (args.length < 2) { sender.sendMessage(ChatColor.RED + "Usage: /mobsmp event <start|stop> [type]"); return true; }
                if (args[1].equalsIgnoreCase("stop")) {
                    if (plugin.getEventManager().stopEvent()) {
                        sender.sendMessage(ChatColor.GREEN + "Event stopped!");
                    } else {
                        sender.sendMessage(ChatColor.RED + "No event is currently active!");
                    }
                } else if (args[1].equalsIgnoreCase("start")) {
                    if (args.length < 3) { sender.sendMessage(ChatColor.RED + "Usage: /mobsmp event start <DOUBLE_KILLS|BOUNTY_RUSH|SPECIAL_DROPS>"); return true; }
                    try {
                        EventManager.EventType type = EventManager.EventType.valueOf(args[2].toUpperCase());
                        if (!plugin.getEventManager().startEvent(type)) {
                            sender.sendMessage(ChatColor.RED + "An event is already running!");
                        } else {
                            sender.sendMessage(ChatColor.GREEN + "Event started: " + type.name());
                        }
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(ChatColor.RED + "Invalid event type! Use: DOUBLE_KILLS, BOUNTY_RUSH, SPECIAL_DROPS");
                    }
                }
            }
            default -> sender.sendMessage(ChatColor.RED + "Unknown subcommand!");
        }
        return true;
    }

    private String getClassList() {
        StringBuilder sb = new StringBuilder();
        for (MobClass cls : MobClass.values()) {
            sb.append(cls.name()).append(", ");
        }
        return sb.substring(0, sb.length() - 2);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("mobsmp.admin")) return Collections.emptyList();

        if (args.length == 1) {
            return filterPrefix(args[0], List.of("reload", "setclass", "setkills", "reset", "info", "event"));
        }

        String sub = args[0].toLowerCase(Locale.ROOT);
        if (args.length == 2) {
            return switch (sub) {
                case "setclass", "setkills", "reset", "info" -> filterPrefix(args[1], onlinePlayerNames());
                case "event" -> filterPrefix(args[1], List.of("start", "stop"));
                default -> Collections.emptyList();
            };
        }

        if (args.length == 3) {
            return switch (sub) {
                case "setclass" -> {
                    List<String> classes = new ArrayList<>();
                    for (MobClass cls : MobClass.values()) classes.add(cls.name());
                    yield filterPrefix(args[2], classes);
                }
                case "event" -> filterPrefix(args[2], List.of("DOUBLE_KILLS", "BOUNTY_RUSH", "SPECIAL_DROPS"));
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
