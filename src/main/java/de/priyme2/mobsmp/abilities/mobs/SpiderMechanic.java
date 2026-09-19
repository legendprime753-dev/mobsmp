package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Snowball;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.Material;
import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Bukkit;

public class SpiderMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player) { 
        effect(player, PotionEffectType.SPEED, 3, 1);
    }
    
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability1", 20000); 
        
        ItemStack potion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 100, 1), true);
        potion.setItemMeta(meta);
        
        ThrownPotion thrown = player.launchProjectile(ThrownPotion.class);
        thrown.setItem(potion);
        playCast(player);
    }
    
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability2", 30000);
        
        Snowball s = player.launchProjectile(Snowball.class);
        s.setCustomName("CobwebProjectile");
        playCast(player);
    }
}
