package de.priyme2.mobsmp.listeners;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.PlayerData;
import net.kyori.adventure.text.Component;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {
    private final MobSMP plugin;
    public PlayerJoinListener(MobSMP plugin){this.plugin=plugin;}
    @EventHandler public void onJoin(PlayerJoinEvent e){
        Player p=e.getPlayer();
        PlayerData d = plugin.getData(p.getUniqueId());
        d.name = p.getName();
        if(!p.hasPlayedBefore()){
            d.mobClass = plugin.randomClass();
            p.showTitle(net.kyori.adventure.title.Title.title(Component.text("MobSMP"), Component.text("Du bist jetzt: "+d.mobClass.name())));
            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1f,1f);
        }
    }
}
