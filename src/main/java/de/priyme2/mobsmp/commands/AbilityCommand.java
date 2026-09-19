package de.priyme2.mobsmp.commands;

import de.priyme2.mobsmp.MobSMP;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AbilityCommand implements CommandExecutor {
    private final MobSMP plugin;

    public AbilityCommand(MobSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Players only!");
            return true;
        }
        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /ability <1|2>");
            return true;
        }
        switch (args[0]) {
            case "1" -> plugin.getAbilityManager().useAbility1(player);
            case "2" -> plugin.getAbilityManager().useAbility2(player);
            default -> player.sendMessage(ChatColor.RED + "Usage: /ability <1|2>");
        }
        return true;
    }
}
