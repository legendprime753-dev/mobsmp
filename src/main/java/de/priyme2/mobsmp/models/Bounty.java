package de.priyme2.mobsmp.models;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Bounty {
    private UUID target;
    private List<UUID> contributors;

    public Bounty(UUID target) {
        this.target = target;
        this.contributors = new ArrayList<>();
    }

    public UUID getTarget() { return target; }
    public List<UUID> getContributors() { return contributors; }

    public void addContributor(UUID uuid) {
        if (!contributors.contains(uuid)) contributors.add(uuid);
    }

    public boolean hasContributor(UUID uuid) { return contributors.contains(uuid); }
    public int getContributorCount() { return contributors.size(); }
}
