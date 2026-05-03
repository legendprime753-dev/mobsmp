package de.priyme2.mobsmp.listeners;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.PlayerData;
import net.kyori.adventure.text.Component;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final MobSMP plugin;
    public PlayerDeathListener(MobSMP plugin){this.plugin=plugin;}
    @EventHandler public void onDeath(PlayerDeathEvent e){
        PlayerData d=plugin.getData(e.getEntity().getUniqueId());
        plugin.getKillManager().applyDeathPenalty(e.getEntity());
        e.getEntity().sendMessage(Component.text("-2 Kills Progression"));
        if(d.consecutiveDeathsAtZeroKills>=3){ var a=e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH); if(a!=null) a.setBaseValue(14.0); d.reducedMaxHealth=true; }
        if(d.reducedMaxHealth && d.kills>=2){ var a=e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH); if(a!=null) a.setBaseValue(20.0); d.reducedMaxHealth=false; }
    }
}
