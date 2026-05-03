package de.priyme2.mobsmp.models;

import java.util.UUID;

public class PlayerData {
    public final UUID uuid;
    public String name;
    public MobClass mobClass;
    public int kills, totalKills, deaths, consecutiveDeathsAtZeroKills, blocksPlaced, blocksBroken, playerKills, mobKills;
    public boolean reducedMaxHealth;

    public PlayerData(UUID uuid, String name, MobClass mobClass) {
        this.uuid = uuid; this.name = name; this.mobClass = mobClass;
    }
}
