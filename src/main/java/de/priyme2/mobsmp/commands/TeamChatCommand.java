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
import org.bukkit.entity.Player;

import java.util.UUID;

public class TeamChatCommand implements CommandExecutor {
    private final MobSMP plugin;

    public TeamChatCommand(MobSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Nur für Spieler!");
            return true;
        }

        Team t = plugin.getTeamManager().getTeamOf(p.getUniqueId());
        if (t == null) {
            p.sendMessage(Msg.error("Du bist in keinem Team!"));
            return true;
        }

        if (args.length == 0) {
            p.sendMessage(Msg.error("Benutzung: /tc <Nachricht>"));
            return true;
        }

        String msg = String.join(" ", args);
        Component format = Component.text("[Team] ", NamedTextColor.AQUA)
                .append(Component.text(p.getName() + ": ", NamedTextColor.DARK_AQUA))
                .append(Component.text(msg, NamedTextColor.WHITE));

        for (UUID memberId : t.members) {
            Player member = Bukkit.getPlayer(memberId);
            if (member != null) {
                member.sendMessage(format);
            }
        }

        return true;
    }
}
