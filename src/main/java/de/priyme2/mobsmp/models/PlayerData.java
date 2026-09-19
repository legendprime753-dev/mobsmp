package de.priyme2.mobsmp.models;

import java.util.UUID;

public class PlayerData {
    public final UUID uuid;
    public String name;
    public MobClass mobClass;
    public MobClass previousMobClass;
    public int kills, totalKills, deaths, consecutiveDeathsAtZeroKills;
    public boolean reducedMaxHealth;

    public PlayerData(UUID uuid, String name, MobClass mobClass) {
        this.uuid = uuid; this.name = name; this.mobClass = mobClass; this.previousMobClass = mobClass;
    }
}
