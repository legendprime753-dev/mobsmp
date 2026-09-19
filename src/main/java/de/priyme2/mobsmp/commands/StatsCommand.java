package de.priyme2.mobsmp.commands;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.StatsData;
import de.priyme2.mobsmp.utils.Msg;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
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
import java.util.UUID;

public class StatsCommand implements CommandExecutor, TabCompleter {
    private final MobSMP plugin;

    public StatsCommand(MobSMP plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        Player target;
        if(args.length > 0) {
            target = Bukkit.getPlayer(args[0]);
            if(target == null) {
                sender.sendMessage(Msg.error("Spieler nicht gefunden."));
                return true;
            }
        } else {
            if(sender instanceof Player p) target = p;
            else return false;
        }

        StatsData s = plugin.getStorageManager().getStats().computeIfAbsent(target.getUniqueId(), StatsData::new);

        sender.sendMessage(Msg.special("Stats von " + target.getName()));
        sender.sendMessage(Msg.colored("Kills (Spieler): " + s.playerKills, TextColor.color(0xFFFFFF)));
        sender.sendMessage(Msg.colored("Kills (Mobs): " + s.mobKills, TextColor.color(0xFFFFFF)));
        sender.sendMessage(Msg.colored("Tode: " + s.deaths, TextColor.color(0xFF5555)));
        sender.sendMessage(Msg.colored("Bloecke platziert: " + s.blocksPlaced, TextColor.color(0xFFFFFF)));
        sender.sendMessage(Msg.colored("Bloecke abgebaut: " + s.blocksBroken, TextColor.color(0xFFFFFF)));
        sender.sendMessage(Msg.colored("Crops geerntet: " + s.cropsHarvested, TextColor.color(0x55FF55)));

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
