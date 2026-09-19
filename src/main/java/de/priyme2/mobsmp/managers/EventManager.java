package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.utils.Msg;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class EventManager {
    private final MobSMP plugin;
    private final Map<String, BukkitTask> activeEvents = new HashMap<>();

    public EventManager(MobSMP plugin) {
        this.plugin = plugin;

        // Random Event Task
        if (plugin.getConfig().getBoolean("events.random-events-enabled", true)) {
            long intervalTicks = plugin.getConfig().getLong("events.interval-seconds", 3600) * 20L;
            Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                int online = Bukkit.getOnlinePlayers().size();
                int minPlayers = plugin.getConfig().getInt("events.minimum-players-required", 4);
                if (online >= minPlayers) {
                    if (activeEvents.isEmpty()) {
                        String[] possible = {"DOUBLE_KILLS", "BOUNTY_RUSH", "SPECIAL_DROPS"};
                        String randomEvent = possible[new Random().nextInt(possible.length)];
                        long durationSeconds = plugin.getConfig().getLong("events.duration-seconds", 600);
                        startEvent(randomEvent, durationSeconds);
                    }
                }
            }, intervalTicks, intervalTicks);
        }
    }

    public void startEvent(String eventType, long durationSeconds) {
        String key = eventType.toUpperCase();
        if(activeEvents.containsKey(key)) stopEvent(key);
        
        Bukkit.broadcast(Msg.special("Global-Event " + key + " gestartet! (" + (durationSeconds/60) + " Min)"));
        for(org.bukkit.entity.Player p : Bukkit.getOnlinePlayers()) {
            p.playSound(p.getLocation(), org.bukkit.Sound.ENTITY_WITHER_SPAWN, 1f, 1f);
        }
        
        BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            stopEvent(key);
        }, durationSeconds * 20L);
        
        activeEvents.put(key, task);
    }

    public void stopEvent(String eventType) {
        String key = eventType.toUpperCase();
        BukkitTask task = activeEvents.remove(key);
        if(task != null) task.cancel();
        Bukkit.broadcast(Msg.info("Global-Event " + key + " beendet."));
    }

    public void stopAll() {
        for(String event : new java.util.ArrayList<>(activeEvents.keySet())) {
            stopEvent(event);
        }
    }

    public boolean isEventActive(String eventType) {
        return activeEvents.containsKey(eventType.toUpperCase());
    }
}
