package de.priyme2.mobsmp;

import de.priyme2.mobsmp.abilities.AbilityHandler;
import de.priyme2.mobsmp.commands.BountyCommand;
import de.priyme2.mobsmp.commands.MobSMPCommand;
import de.priyme2.mobsmp.commands.StatsCommand;
import de.priyme2.mobsmp.commands.TeamCommand;
import de.priyme2.mobsmp.listeners.*;
import de.priyme2.mobsmp.managers.*;
import de.priyme2.mobsmp.models.MobClass;
import de.priyme2.mobsmp.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class MobSMP extends JavaPlugin {
    private AbilityHandler abilityHandler;
    private final Map<UUID, PlayerData> playerData = new HashMap<>();
    private KillManager killManager;

    @Override public void onEnable() {
        saveDefaultConfig();
        abilityHandler = new AbilityHandler();
        killManager = new KillManager(this);
        var bountyManager = new BountyManager(this);
        var teamManager = new TeamManager();

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerKillListener(this, killManager, bountyManager), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new RuleListener(), this);

        getCommand("mobsmp").setExecutor(new MobSMPCommand(this));
        getCommand("bounty").setExecutor(new BountyCommand(this, bountyManager));
        getCommand("team").setExecutor(new TeamCommand(teamManager));
        getCommand("stats").setExecutor(new StatsCommand(this));

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerData data = getData(p.getUniqueId());
                abilityHandler.get(data.mobClass).onTick(p);
                abilityHandler.get(data.mobClass).applyPassiveStats(p);
            }
        }, 20L, 20L);
    }

    public PlayerData getData(UUID uuid) { return playerData.computeIfAbsent(uuid, id -> new PlayerData(id, "unknown", MobClass.CHICKEN)); }
    public void setData(PlayerData data) { playerData.put(data.uuid, data); }
    public AbilityHandler getAbilityHandler() { return abilityHandler; }
    public KillManager getKillManager() { return killManager; }
    public MobClass randomClass(){ var values = MobClass.values(); return values[new Random().nextInt(values.length)]; }
}
