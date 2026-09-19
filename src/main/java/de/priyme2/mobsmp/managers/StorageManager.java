package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.Bounty;
import de.priyme2.mobsmp.models.MobClass;
import de.priyme2.mobsmp.models.PlayerData;
import de.priyme2.mobsmp.models.Team;
import de.priyme2.mobsmp.models.StatsData;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class StorageManager {
    private final MobSMP plugin;
    private final Map<UUID, PlayerData> playerData = new ConcurrentHashMap<>();
    private final Map<UUID, Bounty> bounties = new ConcurrentHashMap<>();
    private final Map<String, Team> teams = new ConcurrentHashMap<>();
    private final Map<UUID, StatsData> stats = new ConcurrentHashMap<>();
    
    private File dataFolder;

    public StorageManager(MobSMP plugin) {
        this.plugin = plugin;
        this.dataFolder = plugin.getDataFolder();
        if(!dataFolder.exists()) dataFolder.mkdirs();
    }

    public PlayerData getData(UUID uuid) { return playerData.computeIfAbsent(uuid, id -> new PlayerData(id, "unknown", MobClass.CHICKEN)); }
    public void setData(PlayerData data) { playerData.put(data.uuid, data); }
    public Collection<PlayerData> getAllPlayerData() { return playerData.values(); }

    public Map<UUID, Bounty> getBounties() { return bounties; }
    public Map<String, Team> getTeams() { return teams; }
    public Map<UUID, StatsData> getStats() { return stats; }

    public void loadAll() {
        loadPlayerData();
        loadBounties();
        loadTeams();
        loadStats();
        plugin.getLogger().info("Data loaded.");
    }

    public void saveAllSync() {
        savePlayerData();
        saveBounties();
        saveTeams();
        saveStats();
    }

    public void saveAll() {
        // Create snapshots for async save
        List<PlayerData> pSnapshot = new ArrayList<>(playerData.values());
        List<Bounty> bSnapshot = new ArrayList<>(bounties.values());
        List<Team> tSnapshot = new ArrayList<>(teams.values());
        List<StatsData> sSnapshot = new ArrayList<>(stats.values());

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            savePlayerDataSnapshot(pSnapshot);
            saveBountiesSnapshot(bSnapshot);
            saveTeamsSnapshot(tSnapshot);
            saveStatsSnapshot(sSnapshot);
        });
    }

    private void loadPlayerData() {
        File file = new File(dataFolder, "playerdata.yml");
        if(!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for(String key : cfg.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            MobClass clazz = MobClass.CHICKEN;
            try {
                clazz = MobClass.valueOf(cfg.getString(key+".mobClass", "CHICKEN"));
            } catch (Exception ignored) {}
            PlayerData d = new PlayerData(uuid, cfg.getString(key+".name", "unknown"), clazz);
            d.kills = cfg.getInt(key+".kills");
            d.totalKills = cfg.getInt(key+".totalKills");
            d.deaths = cfg.getInt(key+".deaths");
            d.consecutiveDeathsAtZeroKills = cfg.getInt(key+".consecutiveDeathsAtZeroKills");
            d.reducedMaxHealth = cfg.getBoolean(key+".reducedMaxHealth");
            playerData.put(uuid, d);
        }
    }

    private void savePlayerDataSnapshot(List<PlayerData> data) {
        File file = new File(dataFolder, "playerdata.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for(PlayerData d : data) {
            String key = d.uuid.toString();
            cfg.set(key+".name", d.name);
            cfg.set(key+".mobClass", d.mobClass.name());
            cfg.set(key+".kills", d.kills);
            cfg.set(key+".totalKills", d.totalKills);
            cfg.set(key+".deaths", d.deaths);
            cfg.set(key+".consecutiveDeathsAtZeroKills", d.consecutiveDeathsAtZeroKills);
            cfg.set(key+".reducedMaxHealth", d.reducedMaxHealth);
        }
        try { cfg.save(file); } catch (IOException ignored) {}
    }
    private void savePlayerData() { savePlayerDataSnapshot(new ArrayList<>(playerData.values())); }

    private void loadBounties() {
        File file = new File(dataFolder, "bounties.yml");
        if(!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for(String key : cfg.getKeys(false)) {
            UUID target = UUID.fromString(key);
            Bounty b = new Bounty(target);
            
            // Bukkit automatically deserializes List<ItemStack>
            List<?> items = cfg.getList(key+".rewardItems");
            if(items != null) {
                for(Object obj : items) {
                    if(obj instanceof org.bukkit.inventory.ItemStack item) {
                        b.addReward(item);
                    }
                }
            }
            bounties.put(target, b);
        }
    }

    private void saveBountiesSnapshot(List<Bounty> data) {
        File file = new File(dataFolder, "bounties.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for(Bounty b : data) {
            String key = b.target.toString();
            cfg.set(key+".rewardItems", b.rewardItems);
        }
        try { cfg.save(file); } catch (IOException ignored) {}
    }
    private void saveBounties() { saveBountiesSnapshot(new ArrayList<>(bounties.values())); }

    private void loadTeams() {
        File file = new File(dataFolder, "teams.yml");
        if(!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for(String name : cfg.getKeys(false)) {
            Team t = new Team(name);
            List<String> mems = cfg.getStringList(name+".members");
            for(String m : mems) t.members.add(UUID.fromString(m));
            teams.put(name, t);
        }
    }

    private void saveTeamsSnapshot(List<Team> data) {
        File file = new File(dataFolder, "teams.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for(Team t : data) {
            List<String> mems = new ArrayList<>();
            for(UUID u : t.members) mems.add(u.toString());
            cfg.set(t.name+".members", mems);
        }
        try { cfg.save(file); } catch (IOException ignored) {}
    }
    private void saveTeams() { saveTeamsSnapshot(new ArrayList<>(teams.values())); }

    private void loadStats() {
        File file = new File(dataFolder, "stats.yml");
        if(!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        for(String key : cfg.getKeys(false)) {
            UUID uuid = UUID.fromString(key);
            StatsData d = new StatsData(uuid);
            d.playerKills = cfg.getInt(key+".playerKills");
            d.mobKills = cfg.getInt(key+".mobKills");
            d.blocksPlaced = cfg.getInt(key+".blocksPlaced");
            d.blocksBroken = cfg.getInt(key+".blocksBroken");
            d.deaths = cfg.getInt(key+".deaths");
            d.cropsHarvested = cfg.getInt(key+".cropsHarvested");
            stats.put(uuid, d);
        }
    }

    private void saveStatsSnapshot(List<StatsData> data) {
        File file = new File(dataFolder, "stats.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        for(StatsData d : data) {
            String key = d.uuid.toString();
            cfg.set(key+".playerKills", d.playerKills);
            cfg.set(key+".mobKills", d.mobKills);
            cfg.set(key+".blocksPlaced", d.blocksPlaced);
            cfg.set(key+".blocksBroken", d.blocksBroken);
            cfg.set(key+".deaths", d.deaths);
            cfg.set(key+".cropsHarvested", d.cropsHarvested);
        }
        try { cfg.save(file); } catch (IOException ignored) {}
    }
    private void saveStats() { saveStatsSnapshot(new ArrayList<>(stats.values())); }
}
