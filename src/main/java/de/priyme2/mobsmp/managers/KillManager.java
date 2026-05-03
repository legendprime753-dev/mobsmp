package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.PlayerData;
import org.bukkit.entity.Player;

public class KillManager {
    private final MobSMP plugin;
    public KillManager(MobSMP plugin){this.plugin=plugin;}
    public void addKill(Player killer, int amount){
        PlayerData d = plugin.getData(killer.getUniqueId());
        d.kills = Math.min(5, d.kills + amount);
        d.totalKills += amount;
        d.playerKills += amount;
    }
    public void applyDeathPenalty(Player player){
        PlayerData d = plugin.getData(player.getUniqueId());
        if (d.kills == 0) d.consecutiveDeathsAtZeroKills++;
        d.kills = Math.max(0, d.kills - 2);
        d.deaths++;
    }
}
