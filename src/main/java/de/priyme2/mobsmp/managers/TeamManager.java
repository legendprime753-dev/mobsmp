package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.Team;

import java.util.UUID;

public class TeamManager {
    private final MobSMP plugin;

    public TeamManager(MobSMP plugin){this.plugin = plugin;}

    public Team getTeam(String name) { return plugin.getStorageManager().getTeams().get(name); }
    
    public Team getTeamOf(UUID uuid) {
        for(Team t : plugin.getStorageManager().getTeams().values()) {
            if(t.members.contains(uuid)) return t;
        }
        return null;
    }

    public boolean createTeam(String name, UUID creator) {
        if(getTeam(name) != null || getTeamOf(creator) != null) return false;
        Team t = new Team(name);
        t.members.add(creator);
        plugin.getStorageManager().getTeams().put(name, t);
        return true;
    }

    public boolean joinTeam(String name, UUID player) {
        Team t = getTeam(name);
        if(t == null || t.members.size() >= 3 || getTeamOf(player) != null) return false;
        t.members.add(player);
        return true;
    }

    public void leaveTeam(UUID player) {
        Team t = getTeamOf(player);
        if(t != null) {
            t.members.remove(player);
            if(t.members.isEmpty()) plugin.getStorageManager().getTeams().remove(t.name);
        }
    }
}
