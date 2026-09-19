package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.utils.Msg;
import de.priyme2.mobsmp.models.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class MostWantedManager {
    private final MobSMP plugin;
    private UUID mostWanted = null;
    private long timeRemaining = 3600; // default 60 mins in seconds
    private BukkitTask timerTask;

    public MostWantedManager(MobSMP plugin) {
        this.plugin = plugin;
        loadTimer();
        startTimer();
    }

    public void checkMostWanted(Player trigger) {
        int highest = 0;
        UUID highestUuid = null;
        for(PlayerData d : plugin.getStorageManager().getAllPlayerData()) {
            if(d.totalKills > highest) {
                highest = d.totalKills;
                highestUuid = d.uuid;
            }
        }
        if(highestUuid != null && !highestUuid.equals(mostWanted)) {
            mostWanted = highestUuid;
            timeRemaining = plugin.getConfig().getLong("most-wanted-interval", 3600);
            Player p = Bukkit.getPlayer(mostWanted);
            String name = p != null ? p.getName() : plugin.getStorageManager().getData(mostWanted).name;
            Bukkit.broadcast(Msg.error(name + " ist nun Most Wanted! (" + highest + " Kills)"));
        }
    }

    private void startTimer() {
        timerTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if(mostWanted == null) return;
            Player p = Bukkit.getPlayer(mostWanted);
            if(p == null || !p.isOnline()) return; // timer pauses

            timeRemaining--;
            if(timeRemaining <= 0) {
                timeRemaining = plugin.getConfig().getLong("most-wanted-interval", 3600);
                Bukkit.broadcast(Msg.error("Most Wanted: " + p.getName() + " bei " +
                        p.getWorld().getName() + " X:" + p.getLocation().getBlockX() + " Y:" + p.getLocation().getBlockY() + " Z:" + p.getLocation().getBlockZ()));
                p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.GLOWING, 1200, 0)); // 60s
                p.sendMessage(Msg.error("Du leuchtest 60 Sekunden!"));
                for(Player pl : Bukkit.getOnlinePlayers()) pl.playSound(pl.getLocation(), org.bukkit.Sound.ENTITY_WITHER_DEATH, 1, 1);
                p.getWorld().strikeLightningEffect(p.getLocation());
            }
        }, 20L, 20L);
    }

    public void onDisable() {
        if(timerTask != null) timerTask.cancel();
        saveTimer();
    }

    private void saveTimer() {
        File file = new File(plugin.getDataFolder(), "mostwanted.yml");
        YamlConfiguration cfg = new YamlConfiguration();
        if(mostWanted != null) {
            cfg.set("uuid", mostWanted.toString());
            cfg.set("timeRemaining", timeRemaining);
        }
        try { cfg.save(file); } catch (IOException ignored) {}
    }

    private void loadTimer() {
        File file = new File(plugin.getDataFolder(), "mostwanted.yml");
        if(!file.exists()) return;
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        if(cfg.contains("uuid")) {
            mostWanted = UUID.fromString(cfg.getString("uuid"));
            timeRemaining = cfg.getLong("timeRemaining", 3600);
        }
    }
}
