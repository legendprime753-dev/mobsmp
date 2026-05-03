package de.priyme2.mobsmp.commands;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.managers.BountyManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.List;

public class BountyCommand implements CommandExecutor, TabCompleter {
    private final MobSMP plugin; private final BountyManager manager;
    public BountyCommand(MobSMP plugin,BountyManager manager){this.plugin=plugin;this.manager=manager;}
    @Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player p) || args.length<2) return true;
        Player t= Bukkit.getPlayer(args[1]); if(t==null) return true;
        manager.addBounty(p,t); sender.sendMessage(Component.text("Bounty gesetzt auf "+t.getName()));
        return true;
    }
    @Override public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) { return List.of("set"); }
}
