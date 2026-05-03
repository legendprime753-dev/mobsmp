package de.priyme2.mobsmp.commands;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.MobClass;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MobSMPCommand implements CommandExecutor, TabCompleter {
    private final MobSMP plugin;
    public MobSMPCommand(MobSMP plugin){this.plugin=plugin;}
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(args.length>=3 && args[0].equalsIgnoreCase("setclass")){
            Player t=Bukkit.getPlayer(args[1]); if(t==null) return true;
            plugin.getData(t.getUniqueId()).mobClass=MobClass.valueOf(args[2].toUpperCase());
            sender.sendMessage(Component.text("Set class."));
        }
        return true;
    }
    @Override public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if(args.length==1) return List.of("setclass");
        if(args.length==3) return Arrays.stream(MobClass.values()).map(Enum::name).collect(Collectors.toList());
        return List.of();
    }
}
