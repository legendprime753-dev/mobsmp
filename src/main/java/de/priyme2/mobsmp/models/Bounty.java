package de.priyme2.mobsmp.models;

import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Bounty {
    public final UUID target;
    public final List<ItemStack> rewardItems = new ArrayList<>();
    
    public Bounty(UUID target){this.target=target;}
    
    public void addReward(ItemStack item) {
        if(item != null && item.getType() != org.bukkit.Material.AIR) {
            rewardItems.add(item.clone());
        }
    }
}
