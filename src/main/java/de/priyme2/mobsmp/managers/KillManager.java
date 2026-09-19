package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;

import java.util.UUID;

public class KillManager {
    private final MobSMP plugin;

    public KillManager(MobSMP plugin) {
        this.plugin = plugin;
    }

    public void addKill(UUID uuid, int amount) {
        PlayerData data = plugin.getMobClassManager().getPlayerData(uuid);
        if (data == null) return;
        int oldKills = data.getKills();
        data.setKills(oldKills + amount);
        data.setTotalKills(data.getTotalKills() + amount);

        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            // Check health restore
            if (data.isReducedMaxHealth()) {
                data.setKillsForHealthRestore(data.getKillsForHealthRestore() + amount);
                if (data.getKillsForHealthRestore() >= 2) {
                    data.setReducedMaxHealth(false);
                    data.setKillsForHealthRestore(0);
                    var attr = player.getAttribute(Attribute.MAX_HEALTH);
                    if (attr != null) attr.setBaseValue(20.0);
                    player.sendMessage(ChatColor.GREEN + "Your max health has been restored to 10 hearts!");
                }
            }

            // Notify about ability unlock
            if (oldKills < 3 && data.getKills() >= 3) {
                player.sendMessage(ChatColor.GOLD + "You have unlocked your 3-kill ability: " +
                        ChatColor.YELLOW + (data.getMobClass() != null ? data.getMobClass().getAbility3kName() : "Unknown"));
                player.showTitle(Title.title(
                        Component.text("§6FÄHIGKEIT FREIGESCHALTET"),
                        Component.text("§e3-Kill Ability bereit")
                ));
                player.playSound(player.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1.0f, 1.15f);
                player.sendActionBar(Component.text("§aNutze deinen Ability Activator für Ability 1!"));
            }
            if (oldKills < 5 && data.getKills() >= 5) {
                player.sendMessage(ChatColor.GOLD + "You have unlocked your 5-kill ability: " +
                        ChatColor.YELLOW + (data.getMobClass() != null ? data.getMobClass().getAbility5kName() : "Unknown"));
                player.showTitle(Title.title(
                        Component.text("§dULTIMATE FREIGESCHALTET"),
                        Component.text("§5Deine 5-Kill Ability ist jetzt aktiv")
                ));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 0.9f);
                player.sendActionBar(Component.text("§dSneak + Rechtsklick mit Ability Activator für Ability 2!"));
            }
        }
    }

    public void onDeath(UUID uuid) {
        PlayerData data = plugin.getMobClassManager().getPlayerData(uuid);
        if (data == null) return;
        int lossOnDeath = plugin.getConfig().getInt("kill-loss-on-death", 2);
        int prevKills = data.getKills();
        int newKills = Math.max(0, prevKills - lossOnDeath);
        data.setKills(newKills);
        data.setDeaths(data.getDeaths() + 1);
        data.setTotalDeaths(data.getTotalDeaths() + 1);

        // Track consecutive deaths at zero kills
        if (prevKills == 0) {
            data.setConsecutiveDeathsAtZeroKills(data.getConsecutiveDeathsAtZeroKills() + 1);
        } else {
            data.setConsecutiveDeathsAtZeroKills(0);
        }

        int deathsBeforeReduction = plugin.getConfig().getInt("deaths-before-max-health-reduction", 3);
        Player player = Bukkit.getPlayer(uuid);

        if (data.getConsecutiveDeathsAtZeroKills() >= deathsBeforeReduction && !data.isReducedMaxHealth()) {
            data.setReducedMaxHealth(true);
            data.setKillsForHealthRestore(0);
            if (player != null) {
                int hearts = plugin.getConfig().getInt("reduced-max-health-hearts", 7);
                double hp = hearts * 2.0;
                var attr = player.getAttribute(Attribute.MAX_HEALTH);
                if (attr != null) {
                    attr.setBaseValue(hp);
                    if (player.getHealth() > hp) player.setHealth(hp);
                }
                player.sendMessage(ChatColor.RED + "Your max health has been reduced to " + hearts + " hearts due to consecutive deaths!");
                player.showTitle(Title.title(
                        Component.text("§4LEBEN REDUZIERT"),
                        Component.text("§cHole 2 Kills für volle HP")
                ));
                player.playSound(player.getLocation(), Sound.ENTITY_WITHER_HURT, 1.0f, 0.7f);
            }
        }

        if (player != null) {
            player.sendMessage(ChatColor.RED + "You died and lost " + (prevKills - newKills) + " kill(s)! Current: " + newKills);
            player.sendActionBar(Component.text("§c-" + (prevKills - newKills) + " Kills durch Tod"));
        }
    }

    public int getKills(UUID uuid) {
        PlayerData data = plugin.getMobClassManager().getPlayerData(uuid);
        return data != null ? data.getKills() : 0;
    }

    public boolean hasAbility3k(UUID uuid) {
        return getKills(uuid) >= 3;
    }

    public boolean hasAbility5k(UUID uuid) {
        return getKills(uuid) >= 5;
    }
}
