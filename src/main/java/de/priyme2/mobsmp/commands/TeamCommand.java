package de.priyme2.mobsmp.commands;

import de.priyme2.mobsmp.managers.TeamManager;
import net.kyori.adventure.text.Component;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class TeamCommand implements CommandExecutor, TabCompleter {
    private final TeamManager manager;
    public TeamCommand(TeamManager manager){this.manager=manager;}
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player p)||args.length==0) return true;
        if(args[0].equals("create")&&args.length>1) manager.create(args[1], p.getUniqueId());
        if(args[0].equals("leave")) manager.leave(p.getUniqueId());
        sender.sendMessage(Component.text("Team command done"));
        return true;
    }
    @Override public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { return List.of("create","leave"); }
}
