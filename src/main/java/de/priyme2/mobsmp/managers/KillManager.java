package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.PlayerData;
import de.priyme2.mobsmp.models.StatsData;
import de.priyme2.mobsmp.utils.Msg;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class KillManager {
    private final MobSMP plugin;
    private final Map<UUID, Map<UUID, Long>> lastKills = new HashMap<>();

    public KillManager(MobSMP plugin){this.plugin=plugin;}

    public void addKill(Player killer, Player victim, int amount){
        if(plugin.getConfig().getBoolean("features.anti-kill-boosting", true) && victim != null) {
            long cdMillis = plugin.getConfig().getLong("anti-kill-boosting.cooldown-minutes-per-player", 30) * 60000L;
            Map<UUID, Long> killerMap = lastKills.computeIfAbsent(killer.getUniqueId(), k -> new HashMap<>());
            long lastKill = killerMap.getOrDefault(victim.getUniqueId(), 0L);
            if(System.currentTimeMillis() - lastKill < cdMillis) {
                killer.sendMessage(Msg.error("Anti-Boosting: Kein Punkt fuer diesen Kill."));
                return;
            }
            killerMap.put(victim.getUniqueId(), System.currentTimeMillis());
        }

        PlayerData d = plugin.getStorageManager().getData(killer.getUniqueId());
        
        if (plugin.getEventManager().isEventActive("DOUBLE_KILLS")) {
            amount *= 2;
        }

        int oldKills = d.kills;
        d.kills = Math.min(5, d.kills + amount);
        d.totalKills += amount;
        
        StatsData s = plugin.getStorageManager().getStats().computeIfAbsent(killer.getUniqueId(), StatsData::new);
        s.playerKills += amount;

        plugin.getMostWantedManager().checkMostWanted(killer);

        if ((oldKills < 3 && d.kills >= 3) || (oldKills < 5 && d.kills >= 5)) {
            killer.showTitle(net.kyori.adventure.title.Title.title(Msg.special("Faehigkeit freigeschaltet!"), Component.text("")));
            killer.playSound(killer.getLocation(), org.bukkit.Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f);
            killer.getWorld().spawnParticle(org.bukkit.Particle.TOTEM_OF_UNDYING, killer.getLocation().add(0, 1, 0), 100, 0.5, 0.5, 0.5, 0.2);
        }
    }
    public void applyDeathPenalty(Player player){
        PlayerData d = plugin.getStorageManager().getData(player.getUniqueId());
        if (d.kills == 0) d.consecutiveDeathsAtZeroKills++;
        d.kills = Math.max(0, d.kills - 2);
        d.deaths++;
        
        StatsData s = plugin.getStorageManager().getStats().computeIfAbsent(player.getUniqueId(), StatsData::new);
        s.deaths++;
    }
}
