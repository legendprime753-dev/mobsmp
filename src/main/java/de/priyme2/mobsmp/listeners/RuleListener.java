package de.priyme2.mobsmp.listeners;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class RuleListener implements Listener {
    @EventHandler public void onPlace(BlockPlaceEvent e){
        if(e.getBlock().getType()==Material.END_CRYSTAL||e.getBlock().getType()==Material.RESPAWN_ANCHOR||e.getBlock().getType()==Material.COBWEB) e.setCancelled(true);
    }
}
