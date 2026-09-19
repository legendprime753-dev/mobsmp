package de.priyme2.mobsmp.commands;

import de.priyme2.mobsmp.MobSMP;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BountyCommand implements CommandExecutor, TabCompleter {
    private final MobSMP plugin;
    public BountyCommand(MobSMP plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) return true;
        if(args.length == 0) return false;

        Player target = Bukkit.getPlayer(args[0]);
        if(target == null) {
            p.sendMessage(de.priyme2.mobsmp.utils.Msg.error("Spieler nicht gefunden."));
            return true;
        }

        if(plugin.getBountyManager().hasBounty(target.getUniqueId())) {
            // Can still add to it
        }

        plugin.getBountyManager().openBountyGUI(p, target);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> comps = new ArrayList<>();
        if(args.length == 1) {
            for(Player pl : Bukkit.getOnlinePlayers()) comps.add(pl.getName());
        }
        return comps;
    }
}
