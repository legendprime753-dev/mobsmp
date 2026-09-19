package de.priyme2.mobsmp.listeners;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class PlayerDeathListener implements Listener {
    private final MobSMP plugin;
    public PlayerDeathListener(MobSMP plugin){this.plugin=plugin;}
    @EventHandler public void onDeath(PlayerDeathEvent e){
        PlayerData d=plugin.getStorageManager().getData(e.getEntity().getUniqueId());
        
        e.getEntity().getWorld().playSound(e.getEntity().getLocation(), org.bukkit.Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1f, 0.5f);
        e.getEntity().getWorld().spawnParticle(org.bukkit.Particle.DUST, e.getEntity().getLocation().add(0, 1, 0), 150, 0.5, 1, 0.5, new org.bukkit.Particle.DustOptions(org.bukkit.Color.RED, 2f));
        
        int oldKills = d.kills;
        plugin.getKillManager().applyDeathPenalty(e.getEntity());
        int lost = oldKills - d.kills;
        if(lost > 0) {
            e.getEntity().sendMessage(de.priyme2.mobsmp.utils.Msg.error("-" + lost + " Kill Progression"));
        }
        
        if(d.consecutiveDeathsAtZeroKills>=3){ 
            var a=e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH); 
            if(a!=null) a.setBaseValue(14.0); 
            d.reducedMaxHealth=true; 
        }
        if(d.reducedMaxHealth && d.kills>=2){ 
            var a=e.getEntity().getAttribute(Attribute.GENERIC_MAX_HEALTH); 
            if(a!=null) a.setBaseValue(20.0); 
            d.reducedMaxHealth=false; 
        }
    }
}
