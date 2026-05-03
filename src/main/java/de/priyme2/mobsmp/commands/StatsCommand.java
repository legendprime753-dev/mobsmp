package de.priyme2.mobsmp.commands;

import de.priyme2.mobsmp.MobSMP;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.*;

import java.util.List;

public class StatsCommand implements CommandExecutor, TabCompleter {
    private final MobSMP plugin;
    public StatsCommand(MobSMP plugin){this.plugin=plugin;}
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        var t = args.length==0? null : Bukkit.getPlayer(args[0]);
        var d = plugin.getData((t==null? ((org.bukkit.entity.Player)sender).getUniqueId():t.getUniqueId()));
        sender.sendMessage(Component.text("Kills: "+d.kills+" Total: "+d.totalKills+" Deaths: "+d.deaths));
        return true;
    }
    @Override public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { return List.of(); }
}
