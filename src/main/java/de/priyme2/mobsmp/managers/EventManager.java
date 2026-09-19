package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class EventManager {
    private final MobSMP plugin;

    public enum EventType {
        DOUBLE_KILLS, BOUNTY_RUSH, SPECIAL_DROPS
    }

    private EventType activeEvent = null;
    private BukkitTask eventTask = null;

    public EventManager(MobSMP plugin) {
        this.plugin = plugin;
    }

    public boolean startEvent(EventType type) {
        if (activeEvent != null) return false;
        activeEvent = type;

        String eventName = type.name().replace("_", " ");
        Bukkit.broadcastMessage(ChatColor.GOLD + "[Event] " + ChatColor.YELLOW + eventName + " has started!");
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ChatColor.GOLD + "EVENT START", ChatColor.YELLOW + eventName, 10, 60, 20);
        }

        if (type == EventType.BOUNTY_RUSH) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "[Event] " + ChatColor.RED + "Bounty Rush: Every player kill grants +2 kills!");
        }

        // Auto-stop after 30 minutes for DOUBLE_KILLS and SPECIAL_DROPS
        if (type != EventType.BOUNTY_RUSH) {
            eventTask = new BukkitRunnable() {
                @Override
                public void run() {
                    stopEvent();
                }
            }.runTaskLater(plugin, 20L * 60 * 30);
        }
        return true;
    }

    public boolean stopEvent() {
        if (activeEvent == null) return false;
        String eventName = activeEvent.name().replace("_", " ");
        activeEvent = null;
        if (eventTask != null) { eventTask.cancel(); eventTask = null; }
        Bukkit.broadcastMessage(ChatColor.RED + "[Event] " + eventName + " has ended!");
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.sendTitle(ChatColor.RED + "EVENT ENDED", ChatColor.GRAY + eventName, 10, 60, 20);
        }
        return true;
    }

    public boolean isDoubleKillsActive() {
        return activeEvent == EventType.DOUBLE_KILLS;
    }

    public boolean isSpecialDropsActive() {
        return activeEvent == EventType.SPECIAL_DROPS;
    }

    public boolean isBountyRushActive() {
        return activeEvent == EventType.BOUNTY_RUSH;
    }

    public EventType getActiveEvent() {
        return activeEvent;
    }
}
