package de.priyme2.mobsmp.listeners;

import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

public class AbilityTriggerListener implements Listener {
    private final MobSMP plugin;

    public AbilityTriggerListener(MobSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e) {
        if (e.getEntity() instanceof Snowball snowball && "CobwebProjectile".equals(snowball.getCustomName())) {
            if (e.getHitEntity() instanceof Player target) {
                placeCobweb(target);
            } else if (e.getHitBlock() != null) {
                // If it hits a block, maybe place cobweb there if wanted, or just skip
            }
        }
        
        if (e.getEntity() instanceof Egg egg && "ChickenBomb".equals(egg.getCustomName())) {
            if (e.getHitEntity() instanceof Player target) {
                target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
            }
        }
    }

    private void placeCobweb(Player target) {
        org.bukkit.block.Block b = target.getEyeLocation().getBlock();
        if(b.getType() == Material.AIR || b.getType() == Material.WATER) {
            b.setType(Material.COBWEB);
            // Remove after 5 seconds
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if(b.getType() == Material.COBWEB) b.setType(Material.AIR);
            }, 100L);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player attacker && e.getEntity() instanceof Player victim) {
            
            // Zombie Infection
            if (attacker.hasMetadata("zombie_infect")) {
                attacker.removeMetadata("zombie_infect", plugin);
                victim.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 200, 2));
                victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1));
                attacker.sendMessage(de.priyme2.mobsmp.utils.Msg.success("Ziel infiziert!"));
            }
            
            // Creeper Lightning
            if (attacker.hasMetadata("creeper_lightning")) {
                attacker.removeMetadata("creeper_lightning", plugin);
                victim.getWorld().strikeLightning(victim.getLocation());
                attacker.sendMessage(de.priyme2.mobsmp.utils.Msg.success("Blitz heraufbeschworen!"));
            }
            
            // Generic Bodyguard: any summoned mob retargets to the attacker
            for (org.bukkit.entity.Entity ent : victim.getNearbyEntities(20, 20, 20)) {
                if (ent instanceof org.bukkit.entity.Mob mob && mob.hasMetadata("summoner")) {
                    String summonerId = mob.getMetadata("summoner").get(0).asString();
                    if (summonerId.equals(victim.getUniqueId().toString())) {
                        mob.setTarget(attacker);
                    }
                }
            }
        }
        
        // Generic: summoned mobs don't damage their summoner
        if (e.getDamager() instanceof org.bukkit.entity.Mob mob && e.getEntity() instanceof Player victim) {
            if (mob.hasMetadata("summoner")) {
                String summonerId = mob.getMetadata("summoner").get(0).asString();
                if (summonerId.equals(victim.getUniqueId().toString())) {
                    e.setCancelled(true);
                }
            }
        }
        
        // Creeper no self TNT damage
        if (e.getEntity() instanceof Player victim && e.getDamager() instanceof org.bukkit.entity.TNTPrimed tnt) {
            if(tnt.hasMetadata("no_damage_" + victim.getUniqueId())) {
                e.setCancelled(true);
            }
        }
        
        // Wither Skull longer duration
        if (e.getEntity() instanceof Player victim && e.getDamager() instanceof org.bukkit.entity.WitherSkull skull) {
            if (skull.getShooter() instanceof Player) {
                // Apply a longer wither effect
                plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                    victim.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 240, 1));
                }, 1L);
            }
        }
    }
}
