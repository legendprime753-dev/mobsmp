package de.priyme2.mobsmp.listeners;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.StatsData;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.entity.Player;

public class StatsListener implements Listener {
    private final MobSMP plugin;

    public StatsListener(MobSMP plugin) { this.plugin = plugin; }

    @EventHandler public void onPlace(BlockPlaceEvent e) {
        StatsData s = plugin.getStorageManager().getStats().computeIfAbsent(e.getPlayer().getUniqueId(), StatsData::new);
        s.blocksPlaced++;
    }

    @EventHandler public void onBreak(BlockBreakEvent e) {
        StatsData s = plugin.getStorageManager().getStats().computeIfAbsent(e.getPlayer().getUniqueId(), StatsData::new);
        s.blocksBroken++;
        
        if(e.getBlock().getBlockData() instanceof Ageable ageable) {
            if(ageable.getAge() == ageable.getMaximumAge()) {
                s.cropsHarvested++;
            }
        }
    }

    @EventHandler public void onEntityDeath(EntityDeathEvent e) {
        if(e.getEntity().getKiller() != null && !(e.getEntity() instanceof Player)) {
            StatsData s = plugin.getStorageManager().getStats().computeIfAbsent(e.getEntity().getKiller().getUniqueId(), StatsData::new);
            s.mobKills++;
        }
    }
}
