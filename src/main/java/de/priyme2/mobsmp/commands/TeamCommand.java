package de.priyme2.mobsmp.commands;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.Team;
import de.priyme2.mobsmp.utils.Msg;
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

import java.util.*;

public class TeamCommand implements CommandExecutor, TabCompleter {
    private final MobSMP plugin;
    private final Map<UUID, String> invites = new HashMap<>();

    public TeamCommand(MobSMP plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if(!(sender instanceof Player p)) return true;
        if(args.length == 0) return false;

        if(args[0].equalsIgnoreCase("create") && args.length == 2) {
            if(plugin.getTeamManager().createTeam(args[1], p.getUniqueId())) {
                p.sendMessage(Msg.success("Team " + args[1] + " erstellt!"));
            } else {
                p.sendMessage(Msg.error("Team existiert schon oder du bist bereits in einem."));
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("invite") && args.length == 2) {
            Team t = plugin.getTeamManager().getTeamOf(p.getUniqueId());
            if(t == null) { p.sendMessage(Msg.error("Du bist in keinem Team!")); return true; }
            if(t.members.size() >= 3) { p.sendMessage(Msg.error("Dein Team ist voll!")); return true; }
            
            Player target = Bukkit.getPlayer(args[1]);
            if(target == null) { p.sendMessage(Msg.error("Spieler nicht gefunden.")); return true; }
            
            invites.put(target.getUniqueId(), t.name);
            p.sendMessage(Msg.success("Einladung an " + target.getName() + " gesendet."));
            target.sendMessage(Msg.special("Du wurdest in Team " + t.name + " eingeladen! /team accept"));
            return true;
        }

        if(args[0].equalsIgnoreCase("accept")) {
            String teamName = invites.remove(p.getUniqueId());
            if(teamName == null) { p.sendMessage(Msg.error("Keine offenen Einladungen.")); return true; }
            if(plugin.getTeamManager().joinTeam(teamName, p.getUniqueId())) {
                p.sendMessage(Msg.success("Du bist dem Team beigetreten!"));
            } else {
                p.sendMessage(Msg.error("Team voll oder existiert nicht mehr."));
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("leave")) {
            plugin.getTeamManager().leaveTeam(p.getUniqueId());
            p.sendMessage(Msg.info("Du hast dein Team verlassen."));
            return true;
        }

        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> comps = new ArrayList<>();
        if(args.length == 1) {
            comps.add("create"); comps.add("invite"); comps.add("accept"); comps.add("leave");
        } else if(args.length == 2 && args[0].equalsIgnoreCase("invite")) {
            for(Player pl : Bukkit.getOnlinePlayers()) comps.add(pl.getName());
        }
        return comps;
    }
}
