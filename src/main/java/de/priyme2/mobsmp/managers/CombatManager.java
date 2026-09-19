package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.utils.Msg;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class CombatManager implements Listener {
    private final MobSMP plugin;
    private final Map<UUID, Long> combatTags = new HashMap<>();
    private final Map<UUID, BossBar> combatBars = new HashMap<>();

    // Cached config
    private boolean enabled;
    private long durationMs;
    private boolean killOnLogout;

    public CombatManager(MobSMP plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        reloadConfig();
        
        // Timer to update boss bars and expire combat - every 4 ticks
        Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 4L, 4L);
    }

    public void reloadConfig() {
        enabled = plugin.getConfig().getBoolean("features.combat-log-system", true);
        durationMs = plugin.getConfig().getLong("combat-log.duration-seconds", 15) * 1000L;
        killOnLogout = plugin.getConfig().getBoolean("combat-log.kill-on-logout", true);
    }

    private void tick() {
        if(!enabled || combatTags.isEmpty()) return;
        long now = System.currentTimeMillis();
        
        Iterator<Map.Entry<UUID, Long>> it = combatTags.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<UUID, Long> entry = it.next();
            if(now > entry.getValue()) {
                Player p = Bukkit.getPlayer(entry.getKey());
                if(p != null) {
                    p.sendMessage(Msg.success("Du bist nicht mehr im Kampf."));
                }
                removeBossBar(entry.getKey());
                it.remove();
            } else {
                Player p = Bukkit.getPlayer(entry.getKey());
                if(p != null) {
                    long left = entry.getValue() - now;
                    double progress = Math.max(0, Math.min(1, (double) left / durationMs));
                    BossBar bar = combatBars.get(entry.getKey());
                    if(bar != null) {
                        int secs = (int) Math.ceil(left / 1000.0);
                        bar.setTitle("§c⚔ ɪᴍ ᴋᴀᴍᴘꜰ §7- §f" + secs + "ꜱ");
                        bar.setProgress(progress);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if(!enabled) return;
        if (e.getEntity() instanceof Player victim && e.getDamager() instanceof Player attacker) {
            long expire = System.currentTimeMillis() + durationMs;

            if(!combatTags.containsKey(victim.getUniqueId())) victim.sendMessage(Msg.error("Du bist im Kampf! Ausloggen = Tod."));
            if(!combatTags.containsKey(attacker.getUniqueId())) attacker.sendMessage(Msg.error("Du bist im Kampf! Ausloggen = Tod."));

            combatTags.put(victim.getUniqueId(), expire);
            combatTags.put(attacker.getUniqueId(), expire);
            
            showBossBar(victim);
            showBossBar(attacker);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if(!enabled) return;
        Player p = e.getPlayer();
        if (combatTags.remove(p.getUniqueId()) != null) {
            removeBossBar(p.getUniqueId());
            if(killOnLogout) {
                p.setHealth(0);
                Bukkit.broadcast(Msg.error(p.getName() + " hat sich im Kampf ausgeloggt!"));
            }
        }
    }
    
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        if(!enabled) return;
        Player p = e.getEntity();
        if (combatTags.remove(p.getUniqueId()) != null) {
            removeBossBar(p.getUniqueId());
        }
    }
    
    private void showBossBar(Player p) {
        BossBar bar = combatBars.get(p.getUniqueId());
        if (bar == null) {
            bar = Bukkit.createBossBar("§c⚔ ɪᴍ ᴋᴀᴍᴘꜰ", BarColor.RED, BarStyle.SOLID);
            bar.addPlayer(p);
            combatBars.put(p.getUniqueId(), bar);
        }
        bar.setProgress(1.0);
        bar.setVisible(true);
    }
    
    private void removeBossBar(UUID uuid) {
        BossBar bar = combatBars.remove(uuid);
        if (bar != null) {
            bar.removeAll();
        }
    }
    
    public boolean isInCombat(UUID uuid) {
        return combatTags.containsKey(uuid);
    }
    
    public long getCombatTimeLeft(UUID uuid) {
        Long expire = combatTags.get(uuid);
        if(expire == null) return 0;
        return Math.max(0, expire - System.currentTimeMillis());
    }
}
