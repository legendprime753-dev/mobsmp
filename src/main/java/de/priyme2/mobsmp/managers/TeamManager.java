package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.models.Team;

import java.util.*;

public class TeamManager {
    private final Map<String, Team> teams = new HashMap<>();
    private final Map<UUID, String> playerTeam = new HashMap<>();
    public boolean create(String name, UUID owner){ if(teams.containsKey(name)) return false; Team t=new Team(name); t.addMember(owner); teams.put(name,t); playerTeam.put(owner,name); return true; }
    public boolean join(String name, UUID player){ Team t=teams.get(name); if(t==null||!t.addMember(player)) return false; playerTeam.put(player,name); return true; }
    public void leave(UUID player){ String n=playerTeam.remove(player); if(n!=null){ Team t=teams.get(n); if(t!=null) t.members.remove(player);} }
    public boolean sameTeam(UUID a, UUID b){ return Objects.equals(playerTeam.get(a), playerTeam.get(b)); }
}
