package de.priyme2.mobsmp.commands;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class AbilityCommand implements CommandExecutor, TabCompleter {
    private final MobSMP plugin;

    public AbilityCommand(MobSMP plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player p)) return true;
        if (args.length != 1) {
            p.sendMessage(Component.text("Benutzung: /ability 1 oder /ability 2", NamedTextColor.RED));
            return true;
        }

        PlayerData d = plugin.getStorageManager().getData(p.getUniqueId());
        if (d == null || d.mobClass == null) return true;

        var mech = plugin.getAbilityHandler().get(d.mobClass);
        if (mech == null) return true;

        if (args[0].equals("1")) {
            if (d.kills < 3) {
                p.sendMessage(Component.text("Du benötigst 3 Kills für diese Fähigkeit!", NamedTextColor.RED));
                return true;
            }
            if (plugin.getAbilityHandler().isOnCooldown(p.getUniqueId(), "ability1")) {
                long left = plugin.getAbilityHandler().getCooldownLeft(p.getUniqueId(), "ability1");
                p.sendMessage(Component.text("Fähigkeit lädt noch auf! (" + String.format("%.1f", left / 1000.0) + "s)", NamedTextColor.RED));
                return true;
            }
            mech.onAbilityUnlock1(p, null);
        } else if (args[0].equals("2")) {
            if (d.kills < 5) {
                p.sendMessage(Component.text("Du benötigst 5 Kills für diese Fähigkeit!", NamedTextColor.RED));
                return true;
            }
            if (plugin.getAbilityHandler().isOnCooldown(p.getUniqueId(), "ability2")) {
                long left = plugin.getAbilityHandler().getCooldownLeft(p.getUniqueId(), "ability2");
                p.sendMessage(Component.text("Fähigkeit lädt noch auf! (" + String.format("%.1f", left / 1000.0) + "s)", NamedTextColor.RED));
                return true;
            }
            mech.onAbilityUnlock2(p, null);
        } else {
            p.sendMessage(Component.text("Benutzung: /ability 1 oder /ability 2", NamedTextColor.RED));
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> comps = new ArrayList<>();
        if (args.length == 1) {
            comps.add("1");
            comps.add("2");
        }
        return comps;
    }
}
