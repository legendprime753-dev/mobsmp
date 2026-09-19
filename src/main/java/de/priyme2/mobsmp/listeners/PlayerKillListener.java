package de.priyme2.mobsmp.listeners;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.managers.BountyManager;
import de.priyme2.mobsmp.managers.KillManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Random;

public class PlayerKillListener implements Listener {
    private final MobSMP plugin;
    public PlayerKillListener(MobSMP plugin){this.plugin=plugin;}
    @EventHandler public void onKill(PlayerDeathEvent e){
        Player killer=e.getEntity().getKiller(); if(killer==null) return;
        
        // Handle bounty kills & normal kills
        if(plugin.getBountyManager().hasBounty(e.getEntity().getUniqueId())) {
            plugin.getBountyManager().handleBountyKill(killer, e.getEntity());
        } else {
            plugin.getKillManager().addKill(killer, e.getEntity(), 1);
        }

        // Reroll Fragment Drop
        double dropChance = plugin.getEventManager().isEventActive("SPECIAL_DROPS") ? 0.40 : 0.20;
        if(new Random().nextDouble() < dropChance) {
            e.getDrops().add(plugin.getRerollManager().createFragment());
        }

    }
}
