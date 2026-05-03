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

public class PlayerKillListener implements Listener {
    private final MobSMP plugin; private final KillManager killManager; private final BountyManager bountyManager;
    public PlayerKillListener(MobSMP plugin, KillManager killManager, BountyManager bountyManager){this.plugin=plugin;this.killManager=killManager;this.bountyManager=bountyManager;}
    @EventHandler public void onKill(PlayerDeathEvent e){
        Player killer=e.getEntity().getKiller(); if(killer==null) return;
        killManager.addKill(killer,1); bountyManager.handleBountyKill(killer, e.getEntity());
        int kills = plugin.getData(killer.getUniqueId()).kills;
        if(kills==3||kills==5){ killer.showTitle(net.kyori.adventure.title.Title.title(Component.text("Fähigkeit freigeschaltet!"), Component.text(""))); killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP,1,1); }
    }
}
