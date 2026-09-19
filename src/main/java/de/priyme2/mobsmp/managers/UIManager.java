package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.PlayerData;
import de.priyme2.mobsmp.utils.Msg;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UIManager implements Listener {
    private final MobSMP plugin;
    private final Map<UUID, Scoreboard> playerBoards = new HashMap<>();

    // Cached config values
    private boolean actionbarEnabled;
    private boolean scoreboardEnabled;
    
    // Pre-built static components (avoid re-creating every tick)
    private static final Component LOCKED_1 = Component.text("⚡ ɢᴇꜱᴘᴇʀʀᴛ", TextColor.color(0x555555));
    private static final Component LOCKED_2 = Component.text("🔥 ɢᴇꜱᴘᴇʀʀᴛ", TextColor.color(0x555555));
    private static final Component READY_1 = Component.text("⚡ ʙᴇʀᴇɪᴛ", TextColor.color(0x55FF55));
    private static final Component READY_2 = Component.text("🔥 ʙᴇʀᴇɪᴛ", TextColor.color(0x55FF55));
    private static final Component SEPARATOR = Component.text(" | ", TextColor.color(0x555555));
    
    private static final Component SCOREBOARD_TITLE = Component.text("「", TextColor.color(0x555555))
            .append(Component.text("ᴍᴏʙꜱᴍᴘ", TextColor.color(0xFFAA00), TextDecoration.BOLD))
            .append(Component.text("」", TextColor.color(0x555555)));

    public UIManager(MobSMP plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        reloadConfig();

        // Actionbar Task (4 ticks instead of 2 - still smooth enough, halves CPU)
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!actionbarEnabled) return;
            for (Player p : Bukkit.getOnlinePlayers()) {
                updateActionBar(p);
            }
        }, 4L, 4L);

        // Scoreboard Task (40 ticks instead of 20 - scoreboard doesn't change that fast)
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!scoreboardEnabled) return;
            for (Player p : Bukkit.getOnlinePlayers()) {
                updateScoreboard(p);
            }
        }, 20L, 40L);
    }

    public void reloadConfig() {
        actionbarEnabled = plugin.getConfig().getBoolean("features.actionbar-cooldowns", true);
        scoreboardEnabled = plugin.getConfig().getBoolean("features.scoreboard", true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        playerBoards.remove(e.getPlayer().getUniqueId());
    }

    private void updateActionBar(Player p) {
        PlayerData d = plugin.getStorageManager().getData(p.getUniqueId());
        if(d.mobClass == null) return;

        long cd1 = plugin.getAbilityHandler().getCooldownLeft(p.getUniqueId(), "ability1");
        long cd2 = plugin.getAbilityHandler().getCooldownLeft(p.getUniqueId(), "ability2");

        // Build components directly instead of MiniMessage parsing every tick
        Component a1;
        if(d.kills < 3) {
            a1 = LOCKED_1;
        } else if(cd1 > 0) {
            a1 = Component.text("⚡ " + String.format("%.1f", cd1 / 1000.0) + "ꜱ", TextColor.color(0xFF5555));
        } else {
            a1 = READY_1;
        }

        Component a2;
        if(d.kills < 5) {
            a2 = LOCKED_2;
        } else if(cd2 > 0) {
            a2 = Component.text("🔥 " + String.format("%.1f", cd2 / 1000.0) + "ꜱ", TextColor.color(0xFF5555));
        } else {
            a2 = READY_2;
        }

        p.sendActionBar(a1.append(SEPARATOR).append(a2));
    }

    private void updateScoreboard(Player p) {
        Scoreboard board = p.getScoreboard();
        if (board.equals(Bukkit.getScoreboardManager().getMainScoreboard())) {
            board = Bukkit.getScoreboardManager().getNewScoreboard();
            p.setScoreboard(board);
            playerBoards.put(p.getUniqueId(), board);
            
            Objective obj = board.registerNewObjective("mobsmp", Criteria.DUMMY, SCOREBOARD_TITLE);
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            
            obj.getScore("§a").setScore(6);
            obj.getScore("§7ᴋʟᴀꜱꜱᴇ:").setScore(5);
            registerTeam(board, "class", "§b", 4);
            
            obj.getScore("§c").setScore(3);
            obj.getScore("§7ᴋɪʟʟ ᴘʀᴏɢʀᴇꜱꜱɪᴏɴ:").setScore(2);
            registerTeam(board, "kills", "§d", 1);
            
            registerTeam(board, "bounty", "§e", 0);
        }

        PlayerData d = plugin.getStorageManager().getData(p.getUniqueId());
        
        String className = d.mobClass != null ? d.mobClass.name() : "None";
        Component prefix = d.mobClass != null ? d.mobClass.getColoredName() : Component.text("None", NamedTextColor.GRAY);
        p.playerListName(Component.text("「", TextColor.color(0x555555))
            .append(prefix)
            .append(Component.text("」 " + p.getName(), TextColor.color(0xAAAAAA))));
        
        setTeamPrefix(board, "class", "§e" + Msg.small(className));
        setTeamPrefix(board, "kills", "§c" + d.kills + " / 5");
        
        if(plugin.getBountyManager().hasBounty(p.getUniqueId())) {
            Objective obj = board.getObjective("mobsmp");
            if(obj != null) obj.getScore("§e").setScore(0);
            setTeamPrefix(board, "bounty", "§4☠ ʙᴏᴜɴᴛʏ ᴀᴋᴛɪᴠ ☠");
        } else {
            board.resetScores("§e");
        }
    }
    
    private void registerTeam(Scoreboard board, String name, String entry, int score) {
        Team t = board.registerNewTeam(name);
        t.addEntry(entry);
        Objective obj = board.getObjective("mobsmp");
        if(obj != null) obj.getScore(entry).setScore(score);
    }
    
    private void setTeamPrefix(Scoreboard board, String name, String prefix) {
        Team t = board.getTeam(name);
        if(t != null) {
            t.prefix(Component.text(prefix));
        }
    }
}
