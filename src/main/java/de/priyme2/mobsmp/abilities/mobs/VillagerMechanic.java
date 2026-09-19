package de.priyme2.mobsmp.abilities.mobs;

import org.bukkit.entity.Player;
import org.bukkit.entity.IronGolem;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Bukkit;
import java.util.Random;

public class VillagerMechanic extends BaseMobMechanic {
    @Override public void applyPassiveStats(Player player) { 
        effect(player, PotionEffectType.HERO_OF_THE_VILLAGE, 3, 0);
    }
    
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) MobSMP.getPlugin(MobSMP.class);
        
        ItemStack targetItem = player.getInventory().getItemInMainHand();
        if(plugin.getTokenManager().isToken(targetItem)) {
            targetItem = player.getInventory().getItemInOffHand();
        }
        
        if (targetItem == null || targetItem.getType() == Material.AIR) {
            player.sendMessage(de.priyme2.mobsmp.utils.Msg.error("Halte ein verzauberbares Item in der Hand!"));
            return;
        }

        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability1", 60000); 

        ItemStack toEnchant = targetItem;
        if(targetItem.getAmount() > 1) {
            toEnchant = targetItem.clone();
            toEnchant.setAmount(1);
            targetItem.setAmount(targetItem.getAmount() - 1);
        }

        Random r = new Random();
        Enchantment[] allEnchants = Enchantment.values();
        
        boolean isBook = toEnchant.getType() == Material.BOOK || toEnchant.getType() == Material.ENCHANTED_BOOK;
        if (toEnchant.getType() == Material.BOOK) toEnchant.setType(Material.ENCHANTED_BOOK);
        
        org.bukkit.inventory.meta.ItemMeta meta = toEnchant.getItemMeta();
        
        // Find one valid enchantment
        Enchantment chosen = null;
        for (int attempts = 0; attempts < 50; attempts++) {
            Enchantment ench = allEnchants[r.nextInt(allEnchants.length)];
            if (!isBook && !ench.canEnchantItem(toEnchant)) continue;
            chosen = ench;
            break;
        }
        
        if (chosen != null) {
            int level = r.nextInt(chosen.getMaxLevel()) + 1;
            if (isBook) {
                ((EnchantmentStorageMeta) meta).addStoredEnchant(chosen, level, false);
            } else {
                meta.addEnchant(chosen, level, false);
            }
        }
        
        toEnchant.setItemMeta(meta);
        if(toEnchant != targetItem) {
            player.getInventory().addItem(toEnchant).values().forEach(i -> player.getWorld().dropItem(player.getLocation(), i));
        }
        player.sendMessage(de.priyme2.mobsmp.utils.Msg.success("Item verzaubert!"));
        playCast(player);
    }
    
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) { 
        MobSMP plugin = (MobSMP) Bukkit.getPluginManager().getPlugin("MobSMP");
        plugin.getAbilityHandler().setCooldown(player.getUniqueId(), "ability2", 120000);
        
        IronGolem golem = player.getWorld().spawn(player.getLocation(), IronGolem.class);
        golem.setCustomName("§6ᴠɪʟʟᴀɢᴇʀ ᴅᴇꜰᴇɴᴅᴇʀ");
        golem.setPlayerCreated(false);
        golem.setMetadata("summoner", new org.bukkit.metadata.FixedMetadataValue(plugin, player.getUniqueId().toString()));
        
        Bukkit.getScheduler().runTaskLater(plugin, () -> { if(golem.isValid()) golem.remove(); }, 20*20L);
        playCast(player);
    }
}
