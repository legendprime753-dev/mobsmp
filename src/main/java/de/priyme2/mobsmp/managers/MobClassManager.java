package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.MobClass;
import de.priyme2.mobsmp.models.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MobClassManager {
    private final MobSMP plugin;
    private final Map<UUID, PlayerData> playerDataMap = new HashMap<>();
    private File dataFolder;

    public MobClassManager(MobSMP plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "playerdata");
        if (!dataFolder.exists()) dataFolder.mkdirs();
    }

    public void loadPlayer(Player player) {
        File file = new File(dataFolder, player.getUniqueId() + ".yml");
        PlayerData data;
        if (file.exists()) {
            FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
            data = new PlayerData(player.getUniqueId(), player.getName());
            String className = cfg.getString("mobClass");
            if (className != null) {
                try {
                    data.setMobClass(MobClass.valueOf(className));
                } catch (IllegalArgumentException ignored) {}
            }
            data.setKills(cfg.getInt("kills", 0));
            data.setDeaths(cfg.getInt("deaths", 0));
            data.setConsecutiveDeathsAtZeroKills(cfg.getInt("consecutiveDeathsAtZeroKills", 0));
            data.setReducedMaxHealth(cfg.getBoolean("reducedMaxHealth", false));
            data.setKillsForHealthRestore(cfg.getInt("killsForHealthRestore", 0));
            data.setKillCountTimestamp(cfg.getLong("killCountTimestamp", System.currentTimeMillis()));
            data.setTotalKills(cfg.getInt("totalKills", 0));
            data.setTotalDeaths(cfg.getInt("totalDeaths", 0));
            data.setBlocksPlaced(cfg.getInt("blocksPlaced", 0));
            data.setBlocksBroken(cfg.getInt("blocksBroken", 0));
            data.setPlayerKills(cfg.getInt("playerKills", 0));
            data.setMobKills(cfg.getInt("mobKills", 0));
        } else {
            data = new PlayerData(player.getUniqueId(), player.getName());
            // Assign random class on first join
            MobClass[] classes = MobClass.values();
            data.setMobClass(classes[(int)(Math.random() * classes.length)]);
        }
        data.setName(player.getName());
        playerDataMap.put(player.getUniqueId(), data);
    }

    public void savePlayer(UUID uuid) {
        PlayerData data = playerDataMap.get(uuid);
        if (data == null) return;
        saveSnapshot(createSnapshot(data));
    }

    public void savePlayerAsync(UUID uuid) {
        PlayerData data = playerDataMap.get(uuid);
        if (data == null) return;
        PlayerDataSnapshot snapshot = createSnapshot(data);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> saveSnapshot(snapshot));
    }

    private void saveSnapshot(PlayerDataSnapshot data) {
        File file = new File(dataFolder, data.uuid + ".yml");
        FileConfiguration cfg = new YamlConfiguration();
        if (data.mobClass != null) cfg.set("mobClass", data.mobClass.name());
        cfg.set("kills", data.kills);
        cfg.set("deaths", data.deaths);
        cfg.set("consecutiveDeathsAtZeroKills", data.consecutiveDeathsAtZeroKills);
        cfg.set("reducedMaxHealth", data.reducedMaxHealth);
        cfg.set("killsForHealthRestore", data.killsForHealthRestore);
        cfg.set("killCountTimestamp", data.killCountTimestamp);
        cfg.set("totalKills", data.totalKills);
        cfg.set("totalDeaths", data.totalDeaths);
        cfg.set("blocksPlaced", data.blocksPlaced);
        cfg.set("blocksBroken", data.blocksBroken);
        cfg.set("playerKills", data.playerKills);
        cfg.set("mobKills", data.mobKills);
        try {
            cfg.save(file);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save player data for " + data.uuid);
        }
    }

    public void saveAll() {
        for (UUID uuid : playerDataMap.keySet()) savePlayer(uuid);
    }

    public void unloadPlayer(UUID uuid) {
        PlayerData data = playerDataMap.get(uuid);
        if (data == null) return;
        PlayerDataSnapshot snapshot = createSnapshot(data);
        playerDataMap.remove(uuid);
        if (plugin.isEnabled()) {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> saveSnapshot(snapshot));
        } else {
            saveSnapshot(snapshot);
        }
    }

    public PlayerData getPlayerData(UUID uuid) {
        return playerDataMap.get(uuid);
    }

    public MobClass getMobClass(UUID uuid) {
        PlayerData data = playerDataMap.get(uuid);
        return data != null ? data.getMobClass() : null;
    }

    public void setMobClass(UUID uuid, MobClass cls) {
        PlayerData data = playerDataMap.get(uuid);
        if (data != null) {
            data.setMobClass(cls);
            // Apply class attributes
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                plugin.getAbilityManager().applyClassAttributes(player, cls);
                plugin.getAbilityHandler().ensureAbilityActivator(player);
            }
        }
    }

    public Map<UUID, PlayerData> getAllData() {
        return playerDataMap;
    }

    private PlayerDataSnapshot createSnapshot(PlayerData data) {
        return new PlayerDataSnapshot(
                data.getUuid(),
                data.getMobClass(),
                data.getKills(),
                data.getDeaths(),
                data.getConsecutiveDeathsAtZeroKills(),
                data.isReducedMaxHealth(),
                data.getKillsForHealthRestore(),
                data.getKillCountTimestamp(),
                data.getTotalKills(),
                data.getTotalDeaths(),
                data.getBlocksPlaced(),
                data.getBlocksBroken(),
                data.getPlayerKills(),
                data.getMobKills()
        );
    }

    /**
     * Immutable snapshot of player data used for async persistence to avoid reading mutable state off-thread.
     */
    private record PlayerDataSnapshot(
            UUID uuid,
            MobClass mobClass,
            int kills,
            int deaths,
            int consecutiveDeathsAtZeroKills,
            boolean reducedMaxHealth,
            int killsForHealthRestore,
            long killCountTimestamp,
            int totalKills,
            int totalDeaths,
            int blocksPlaced,
            int blocksBroken,
            int playerKills,
            int mobKills
    ) {}
}
