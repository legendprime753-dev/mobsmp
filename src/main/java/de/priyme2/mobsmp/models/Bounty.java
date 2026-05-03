package de.priyme2.mobsmp.models;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Bounty {
    public final UUID target;
    public final Set<UUID> requester = new HashSet<>();
    public Bounty(UUID target){this.target=target;}
    public boolean addRequester(UUID uuid){return requester.size()<4 && requester.add(uuid);}    
}
