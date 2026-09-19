package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.Material;
import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Bukkit;
import java.util.Random;

public class WitchMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player) { 
        player.removePotionEffect(PotionEffectType.POISON);
        player.removePotionEffect(PotionEffectType.SLOWNESS);
        player.removePotionEffect(PotionEffectType.WEAKNESS);
        player.removePotionEffect(PotionEffectType.WITHER);
    }
    
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability1", 20000); 
        
        ItemStack potion = new ItemStack(Material.SPLASH_POTION);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        PotionEffectType[] types = {PotionEffectType.POISON, PotionEffectType.SLOWNESS, PotionEffectType.WEAKNESS};
        PotionEffectType type = types[new Random().nextInt(types.length)];
        meta.addCustomEffect(new PotionEffect(type, 160, 0), true);
        potion.setItemMeta(meta);
        
        ThrownPotion thrown = player.launchProjectile(ThrownPotion.class);
        thrown.setItem(potion);
        playCast(player);
    }
    
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability2", 40000);
        
        player.setHealth(Math.min(player.getHealth() + 8.0, player.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH).getValue()));
        effect(player, PotionEffectType.REGENERATION, 10, 1);
        playCast(player);
    }
}
