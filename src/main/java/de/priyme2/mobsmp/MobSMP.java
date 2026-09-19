package de.priyme2.mobsmp;

import de.priyme2.mobsmp.abilities.AbilityHandler;
import de.priyme2.mobsmp.commands.*;
import de.priyme2.mobsmp.listeners.*;
import de.priyme2.mobsmp.managers.*;
import org.bukkit.plugin.java.JavaPlugin;

public class MobSMP extends JavaPlugin {
    private static MobSMP instance;

    private MobClassManager mobClassManager;
    private KillManager killManager;
    private AbilityManager abilityManager;
    private BountyManager bountyManager;
    private MostWantedManager mostWantedManager;
    private TeamManager teamManager;
    private StatsManager statsManager;
    private RerollManager rerollManager;
    private EventManager eventManager;
    private AbilityHandler abilityHandler;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        // Init managers in order
        eventManager = new EventManager(this);
        teamManager = new TeamManager(this);
        mobClassManager = new MobClassManager(this);
        killManager = new KillManager(this);
        abilityManager = new AbilityManager(this);
        bountyManager = new BountyManager(this);
        mostWantedManager = new MostWantedManager(this);
        statsManager = new StatsManager(this);
        rerollManager = new RerollManager(this);
        abilityHandler = new AbilityHandler(this);

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerKillListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        getServer().getPluginManager().registerEvents(new WorldListener(this), this);

        // Register commands
        MobSMPCommand mobSMPCommand = new MobSMPCommand(this);
        AbilityCommand abilityCommand = new AbilityCommand(this);
        BountyCommand bountyCommand = new BountyCommand(this);
        TeamCommand teamCommand = new TeamCommand(this);
        StatsCommand statsCommand = new StatsCommand(this);

        getCommand("mobsmp").setExecutor(mobSMPCommand);
        getCommand("mobsmp").setTabCompleter(mobSMPCommand);
        getCommand("ability").setExecutor(abilityCommand);
        getCommand("bounty").setExecutor(bountyCommand);
        getCommand("bounty").setTabCompleter(bountyCommand);
        getCommand("team").setExecutor(teamCommand);
        getCommand("team").setTabCompleter(teamCommand);
        getCommand("stats").setExecutor(statsCommand);
        getCommand("stats").setTabCompleter(statsCommand);

        getLogger().info("MobSMP enabled!");
    }

    @Override
    public void onDisable() {
        if (mobClassManager != null) mobClassManager.saveAll();
        if (bountyManager != null) bountyManager.save();
        if (teamManager != null) teamManager.save();
        getLogger().info("MobSMP disabled!");
    }

    public static MobSMP getInstance() { return instance; }
    public MobClassManager getMobClassManager() { return mobClassManager; }
    public KillManager getKillManager() { return killManager; }
    public AbilityManager getAbilityManager() { return abilityManager; }
    public BountyManager getBountyManager() { return bountyManager; }
    public MostWantedManager getMostWantedManager() { return mostWantedManager; }
    public TeamManager getTeamManager() { return teamManager; }
    public StatsManager getStatsManager() { return statsManager; }
    public RerollManager getRerollManager() { return rerollManager; }
    public EventManager getEventManager() { return eventManager; }
    public AbilityHandler getAbilityHandler() { return abilityHandler; }
}
