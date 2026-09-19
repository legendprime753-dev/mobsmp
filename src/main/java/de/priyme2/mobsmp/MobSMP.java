package de.priyme2.mobsmp;

import de.priyme2.mobsmp.abilities.AbilityHandler;
import de.priyme2.mobsmp.commands.*;
import de.priyme2.mobsmp.utils.Msg;
import de.priyme2.mobsmp.listeners.*;
import de.priyme2.mobsmp.managers.*;
import de.priyme2.mobsmp.models.MobClass;
import de.priyme2.mobsmp.models.PlayerData;
import de.priyme2.mobsmp.recipes.GappleRecipe;
import de.priyme2.mobsmp.recipes.RerollRecipe;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class MobSMP extends JavaPlugin {
    private AbilityHandler abilityHandler;
    private KillManager killManager;
    private BountyManager bountyManager;
    private TeamManager teamManager;
    private StorageManager storageManager;
    private TokenManager tokenManager;
    private MostWantedManager mostWantedManager;
    private RerollManager rerollManager;
    private StatsManager statsManager;
    private EventManager eventManager;
    
    // Shared Random instance - avoids creating new Random() on every call
    private final Random random = new Random();

    @Override public void onEnable() {
        saveDefaultConfig();
        
        storageManager = new StorageManager(this);
        storageManager.loadAll();

        abilityHandler = new AbilityHandler();
        tokenManager = new TokenManager(this);
        killManager = new KillManager(this);
        bountyManager = new BountyManager(this);
        teamManager = new TeamManager(this);
        mostWantedManager = new MostWantedManager(this);
        rerollManager = new RerollManager(this);
        statsManager = new StatsManager(this);
        eventManager = new EventManager(this);

        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerKillListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerDeathListener(this), this);
        Bukkit.getPluginManager().registerEvents(new RuleListener(this), this);
        Bukkit.getPluginManager().registerEvents(new StatsListener(this), this);
        Bukkit.getPluginManager().registerEvents(new TokenListener(this), this);
        Bukkit.getPluginManager().registerEvents(new RerollListener(this), this);
        Bukkit.getPluginManager().registerEvents(new AbilityTriggerListener(this), this);

        // Dragon Egg Check - runs every second
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerData d = storageManager.getData(p.getUniqueId());
                if (d == null) continue;
                
                boolean hasEgg = p.getInventory().contains(org.bukkit.Material.DRAGON_EGG);
                if (hasEgg && d.mobClass != MobClass.DRAGON) {
                    d.previousMobClass = d.mobClass;
                    d.mobClass = MobClass.DRAGON;
                    p.sendMessage(Msg.special("Du spuerst die Macht des Drachens!"));
                    p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL, 1f, 1f);
                    p.getWorld().spawnParticle(org.bukkit.Particle.PORTAL, p.getLocation().add(0, 1, 0), 200, 1, 1, 1, 0.1);
                    for(org.bukkit.potion.PotionEffect effect : p.getActivePotionEffects()) p.removePotionEffect(effect.getType());
                    var attr = p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
                    if(attr != null) attr.setBaseValue(26.0);
                } else if (!hasEgg && d.mobClass == MobClass.DRAGON) {
                    d.mobClass = d.previousMobClass != null ? d.previousMobClass : MobClass.ZOMBIE;
                    p.sendMessage(Msg.info("Die Macht des Drachens hat dich verlassen."));
                    p.setAllowFlight(false);
                    for(org.bukkit.potion.PotionEffect effect : p.getActivePotionEffects()) p.removePotionEffect(effect.getType());
                    var attr = p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
                    if(attr != null) attr.setBaseValue(20.0);
                }
            }
        }, 20L, 20L);

        new CombatManager(this);
        new UIManager(this);

        // Reuse single command instances instead of creating duplicates
        MobSMPCommand mobsmpCmd = new MobSMPCommand(this);
        getCommand("mobsmp").setExecutor(mobsmpCmd);
        getCommand("mobsmp").setTabCompleter(mobsmpCmd);
        
        BountyCommand bountyCmd = new BountyCommand(this);
        getCommand("bounty").setExecutor(bountyCmd);
        getCommand("bounty").setTabCompleter(bountyCmd);
        
        TeamCommand teamCmd = new TeamCommand(this);
        getCommand("team").setExecutor(teamCmd);
        getCommand("team").setTabCompleter(teamCmd);
        
        getCommand("tc").setExecutor(new TeamChatCommand(this));
        
        StatsCommand statsCmd = new StatsCommand(this);
        getCommand("stats").setExecutor(statsCmd);
        getCommand("stats").setTabCompleter(statsCmd);
        
        AbilityCommand abilityCmd = new AbilityCommand(this);
        getCommand("ability").setExecutor(abilityCmd);
        getCommand("ability").setTabCompleter(abilityCmd);

        GappleRecipe.register(this);
        RerollRecipe.register(this);

        // Passive stats + tick - runs every second
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                PlayerData data = storageManager.getData(p.getUniqueId());
                if(data.mobClass != null) {
                    var mech = abilityHandler.get(data.mobClass);
                    if(mech != null) {
                        mech.onTick(p);
                        mech.applyPassiveStats(p);
                    }
                }
            }
        }, 20L, 20L);
        
        // Auto-save every 5 minutes
        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            storageManager.saveAll();
        }, 6000L, 6000L);
        
        // Cooldown cleanup for offline players - every 2 minutes  
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            abilityHandler.cleanupOffline();
        }, 2400L, 2400L);
    }

    @Override public void onDisable() {
        // Cancel ALL scheduled tasks to prevent leaks
        Bukkit.getScheduler().cancelTasks(this);
        mostWantedManager.onDisable();
        storageManager.saveAllSync();
    }

    public StorageManager getStorageManager() { return storageManager; }
    public AbilityHandler getAbilityHandler() { return abilityHandler; }
    public KillManager getKillManager() { return killManager; }
    public BountyManager getBountyManager() { return bountyManager; }
    public TeamManager getTeamManager() { return teamManager; }
    public TokenManager getTokenManager() { return tokenManager; }
    public MostWantedManager getMostWantedManager() { return mostWantedManager; }
    public RerollManager getRerollManager() { return rerollManager; }
    public StatsManager getStatsManager() { return statsManager; }
    public EventManager getEventManager() { return eventManager; }

    public MobClass randomClass() {
        int chance = random.nextInt(100);
        
        if(chance < 10) {
            MobClass[] rares = {MobClass.WARDEN, MobClass.WITHER, MobClass.ENDERMAN};
            return rares[random.nextInt(rares.length)];
        }
        if(chance < 35) {
            MobClass[] uncommons = {MobClass.BLAZE, MobClass.CREEPER, MobClass.SHULKER, MobClass.PIGLIN};
            return uncommons[random.nextInt(uncommons.length)];
        }
        MobClass[] commons = {MobClass.ZOMBIE, MobClass.SPIDER, MobClass.CHICKEN, MobClass.TURTLE, MobClass.WITCH, MobClass.VILLAGER};
        return commons[random.nextInt(commons.length)];
    }
}
