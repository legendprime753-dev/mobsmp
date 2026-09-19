package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.Team;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class TeamManager {
    private final MobSMP plugin;
    private final Map<String, Team> teams = new HashMap<>();
    private final Map<UUID, String> playerTeam = new HashMap<>();
    private final Map<UUID, String> pendingInvites = new HashMap<>(); // invitee -> team name
    private File teamFile;

    public TeamManager(MobSMP plugin) {
        this.plugin = plugin;
        teamFile = new File(plugin.getDataFolder(), "teams.yml");
        load();
    }

    private void load() {
        if (!teamFile.exists()) return;
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(teamFile);
        if (cfg.getConfigurationSection("teams") == null) return;
        for (String name : cfg.getConfigurationSection("teams").getKeys(false)) {
            String leaderStr = cfg.getString("teams." + name + ".leader");
            if (leaderStr == null) continue;
            UUID leader = UUID.fromString(leaderStr);
            Team team = new Team(name, leader);
            team.getMembers().clear();
            List<String> members = cfg.getStringList("teams." + name + ".members");
            for (String m : members) team.addMember(UUID.fromString(m));
            teams.put(name, team);
            for (UUID uuid : team.getMembers()) playerTeam.put(uuid, name);
        }
    }

    public void save() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<String, Team> entry : teams.entrySet()) {
            Team team = entry.getValue();
            cfg.set("teams." + team.getName() + ".leader", team.getLeader().toString());
            List<String> members = new ArrayList<>();
            for (UUID uuid : team.getMembers()) members.add(uuid.toString());
            cfg.set("teams." + team.getName() + ".members", members);
        }
        try {
            cfg.save(teamFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save teams!");
        }
    }

    public boolean createTeam(Player player, String name) {
        if (playerTeam.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You are already in a team!");
            return false;
        }
        if (!name.matches("[A-Za-z0-9_]{2,9}")) {
            player.sendMessage(ChatColor.RED + "Team names must be 2-9 characters long and contain only letters, numbers, and underscores.");
            return false;
        }
        if (teams.containsKey(name)) {
            player.sendMessage(ChatColor.RED + "A team with that name already exists!");
            return false;
        }
        Team team = new Team(name, player.getUniqueId());
        teams.put(name, team);
        playerTeam.put(player.getUniqueId(), name);
        updateScoreboard(team);
        save();
        player.sendMessage(ChatColor.GREEN + "Team '" + name + "' created!");
        return true;
    }

    public boolean invitePlayer(Player leader, Player invitee) {
        String teamName = playerTeam.get(leader.getUniqueId());
        if (teamName == null) {
            leader.sendMessage(ChatColor.RED + "You are not in a team!");
            return false;
        }
        Team team = teams.get(teamName);
        if (!team.isLeader(leader.getUniqueId())) {
            leader.sendMessage(ChatColor.RED + "Only the team leader can invite players!");
            return false;
        }
        int maxSize = plugin.getConfig().getInt("max-team-size", 3);
        if (team.getSize() >= maxSize) {
            leader.sendMessage(ChatColor.RED + "Your team is full! (max " + maxSize + " players)");
            return false;
        }
        if (playerTeam.containsKey(invitee.getUniqueId())) {
            leader.sendMessage(ChatColor.RED + invitee.getName() + " is already in a team!");
            return false;
        }
        pendingInvites.put(invitee.getUniqueId(), teamName);
        leader.sendMessage(ChatColor.GREEN + "Invited " + invitee.getName() + " to your team!");
        invitee.sendMessage(ChatColor.GREEN + "You have been invited to team '" + teamName + "' by " + leader.getName() + "! Use /team accept");
        return true;
    }

    public boolean acceptInvite(Player player) {
        String teamName = pendingInvites.remove(player.getUniqueId());
        if (teamName == null) {
            player.sendMessage(ChatColor.RED + "You have no pending team invite!");
            return false;
        }
        Team team = teams.get(teamName);
        if (team == null) {
            player.sendMessage(ChatColor.RED + "Team no longer exists!");
            return false;
        }
        int maxSize = plugin.getConfig().getInt("max-team-size", 3);
        if (team.getSize() >= maxSize) {
            player.sendMessage(ChatColor.RED + "Team is now full!");
            return false;
        }
        team.addMember(player.getUniqueId());
        playerTeam.put(player.getUniqueId(), teamName);
        updateScoreboard(team);
        save();
        player.sendMessage(ChatColor.GREEN + "Joined team '" + teamName + "'!");
        for (UUID uuid : team.getMembers()) {
            Player member = Bukkit.getPlayer(uuid);
            if (member != null && !member.equals(player)) {
                member.sendMessage(ChatColor.GREEN + player.getName() + " joined your team!");
            }
        }
        return true;
    }

    public boolean leaveTeam(Player player) {
        String teamName = playerTeam.remove(player.getUniqueId());
        if (teamName == null) {
            player.sendMessage(ChatColor.RED + "You are not in a team!");
            return false;
        }
        Team team = teams.get(teamName);
        team.removeMember(player.getUniqueId());
        if (team.getSize() == 0) {
            teams.remove(teamName);
            removeScoreboardTeam(teamName);
        } else if (team.isLeader(player.getUniqueId())) {
            // Transfer leadership
            team.setLeader(team.getMembers().get(0));
            updateScoreboard(team);
        } else {
            updateScoreboard(team);
        }
        save();
        player.sendMessage(ChatColor.YELLOW + "Left team '" + teamName + "'!");
        return true;
    }

    public boolean disbandTeam(Player player) {
        String teamName = playerTeam.get(player.getUniqueId());
        if (teamName == null) {
            player.sendMessage(ChatColor.RED + "You are not in a team!");
            return false;
        }
        Team team = teams.get(teamName);
        if (!team.isLeader(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Only the team leader can disband!");
            return false;
        }
        for (UUID uuid : new ArrayList<>(team.getMembers())) {
            playerTeam.remove(uuid);
            Player m = Bukkit.getPlayer(uuid);
            if (m != null) m.sendMessage(ChatColor.RED + "Team '" + teamName + "' has been disbanded!");
        }
        teams.remove(teamName);
        removeScoreboardTeam(teamName);
        save();
        return true;
    }

    public boolean isTeammate(UUID a, UUID b) {
        String teamA = playerTeam.get(a);
        if (teamA == null) return false;
        String teamB = playerTeam.get(b);
        return teamA.equals(teamB);
    }

    public Team getTeam(UUID uuid) {
        String name = playerTeam.get(uuid);
        return name != null ? teams.get(name) : null;
    }

    public Team getTeamByName(String name) {
        return teams.get(name);
    }

    public Map<String, Team> getAllTeams() {
        return Collections.unmodifiableMap(teams);
    }

    public boolean sendTeamChat(Player sender, String message) {
        String teamName = playerTeam.get(sender.getUniqueId());
        if (teamName == null) {
            sender.sendMessage(ChatColor.RED + "You are not in a team!");
            return false;
        }
        Team team = teams.get(teamName);
        if (team == null) {
            sender.sendMessage(ChatColor.RED + "Your team was not found.");
            return false;
        }
        String formatted = ChatColor.DARK_GREEN + "[Team] " + ChatColor.GREEN + sender.getName() + ChatColor.WHITE + ": " + message;
        for (UUID uuid : team.getMembers()) {
            Player member = Bukkit.getPlayer(uuid);
            if (member != null && member.isOnline()) {
                member.sendMessage(formatted);
            }
        }
        return true;
    }

    private void updateScoreboard(Team team) {
        ScoreboardManager sbm = Bukkit.getScoreboardManager();
        if (sbm == null) return;
        Scoreboard sb = sbm.getMainScoreboard();
        org.bukkit.scoreboard.Team sbTeam = sb.getTeam("mobsmp_" + team.getName());
        if (sbTeam == null) sbTeam = sb.registerNewTeam("mobsmp_" + team.getName());
        sbTeam.setColor(ChatColor.GREEN);
        sbTeam.setPrefix(ChatColor.GREEN.toString());
        for (String entry : new ArrayList<>(sbTeam.getEntries())) {
            sbTeam.removeEntry(entry);
        }
        for (UUID uuid : team.getMembers()) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null) sbTeam.addEntry(p.getName());
        }
    }

    private void removeScoreboardTeam(String name) {
        ScoreboardManager sbm = Bukkit.getScoreboardManager();
        if (sbm == null) return;
        Scoreboard sb = sbm.getMainScoreboard();
        org.bukkit.scoreboard.Team sbTeam = sb.getTeam("mobsmp_" + name);
        if (sbTeam != null) sbTeam.unregister();
    }
}
