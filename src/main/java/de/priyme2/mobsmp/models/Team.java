package de.priyme2.mobsmp.models;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Team {
    public final String name;
    public final Set<UUID> members = new HashSet<>();
    public Team(String name){this.name=name;}
    public boolean addMember(UUID uuid){return members.size()<3 && members.add(uuid);}    
}
