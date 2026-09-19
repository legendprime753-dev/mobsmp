package de.priyme2.mobsmp.models;

import java.util.UUID;

public class PlayerData {
    private final UUID uuid;
    private String name;
    private MobClass mobClass;
    private int kills;
    private int deaths;
    private int consecutiveDeathsAtZeroKills;
    private boolean reducedMaxHealth;
    private int killsForHealthRestore;
    private long killCountTimestamp; // when current kill count was reached

    // Stats
    private int totalKills;
    private int totalDeaths;
    private int blocksPlaced;
    private int blocksBroken;
    private int playerKills;
    private int mobKills;

    public PlayerData(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.mobClass = null;
        this.kills = 0;
        this.deaths = 0;
        this.consecutiveDeathsAtZeroKills = 0;
        this.reducedMaxHealth = false;
        this.killsForHealthRestore = 0;
        this.killCountTimestamp = System.currentTimeMillis();
    }

    public UUID getUuid() { return uuid; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public MobClass getMobClass() { return mobClass; }
    public void setMobClass(MobClass mobClass) { this.mobClass = mobClass; }
    public int getKills() { return kills; }
    public void setKills(int kills) {
        this.kills = kills;
        this.killCountTimestamp = System.currentTimeMillis();
    }
    public int getDeaths() { return deaths; }
    public void setDeaths(int deaths) { this.deaths = deaths; }
    public int getConsecutiveDeathsAtZeroKills() { return consecutiveDeathsAtZeroKills; }
    public void setConsecutiveDeathsAtZeroKills(int v) { this.consecutiveDeathsAtZeroKills = v; }
    public boolean isReducedMaxHealth() { return reducedMaxHealth; }
    public void setReducedMaxHealth(boolean v) { this.reducedMaxHealth = v; }
    public int getKillsForHealthRestore() { return killsForHealthRestore; }
    public void setKillsForHealthRestore(int v) { this.killsForHealthRestore = v; }
    public long getKillCountTimestamp() { return killCountTimestamp; }
    public void setKillCountTimestamp(long t) { this.killCountTimestamp = t; }

    public int getTotalKills() { return totalKills; }
    public void setTotalKills(int v) { this.totalKills = v; }
    public int getTotalDeaths() { return totalDeaths; }
    public void setTotalDeaths(int v) { this.totalDeaths = v; }
    public int getBlocksPlaced() { return blocksPlaced; }
    public void setBlocksPlaced(int v) { this.blocksPlaced = v; }
    public int getBlocksBroken() { return blocksBroken; }
    public void setBlocksBroken(int v) { this.blocksBroken = v; }
    public int getPlayerKills() { return playerKills; }
    public void setPlayerKills(int v) { this.playerKills = v; }
    public int getMobKills() { return mobKills; }
    public void setMobKills(int v) { this.mobKills = v; }
}
