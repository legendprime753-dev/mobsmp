package de.priyme2.mobsmp.models;

import java.util.UUID;

public class StatsData {
    public final UUID uuid;
    public int playerKills, mobKills, blocksPlaced, blocksBroken, deaths, cropsHarvested;

    public StatsData(UUID uuid) { this.uuid = uuid; }
}
