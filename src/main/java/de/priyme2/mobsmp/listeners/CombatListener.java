package de.priyme2.mobsmp.listeners;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.MobClass;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.projectiles.ProjectileSource;

public class CombatListener implements Listener {
    private final MobSMP plugin;
    private long globalMaceCooldownUntil = 0L;

    public CombatListener(MobSMP plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        MobClass cls = plugin.getMobClassManager().getMobClass(player.getUniqueId());
        if (cls == null) return;
        EntityDamageEvent.DamageCause cause = event.getCause();

        switch (cls) {
            case ZOMBIE -> {
                // Fire damage +50%
                if (cause == EntityDamageEvent.DamageCause.FIRE ||
                        cause == EntityDamageEvent.DamageCause.FIRE_TICK ||
                        cause == EntityDamageEvent.DamageCause.HOT_FLOOR) {
                    event.setDamage(event.getDamage() * 1.5);
                }
                // Prevent sun burn (fire tick from sun)
                if (cause == EntityDamageEvent.DamageCause.FIRE_TICK &&
                        player.getLocation().getBlock().getLightFromSky() >= 15 &&
                        player.getWorld().isDayTime()) {
                    event.setCancelled(true);
                    return;
                }
            }
            case BLAZE -> {
                // Fire immunity
                if (cause == EntityDamageEvent.DamageCause.FIRE ||
                        cause == EntityDamageEvent.DamageCause.FIRE_TICK ||
                        cause == EntityDamageEvent.DamageCause.LAVA ||
                        cause == EntityDamageEvent.DamageCause.HOT_FLOOR) {
                    event.setCancelled(true);
                    return;
                }
            }
            case CREEPER -> {
                // 50% explosion resistance
                if (cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
                        cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                    event.setDamage(event.getDamage() * 0.5);
                }
            }
            case PHANTOM -> {
                // Fall immunity
                if (cause == EntityDamageEvent.DamageCause.FALL) {
                    event.setCancelled(true);
                    return;
                }
                // Daytime: +20% all damage
                if (player.getWorld().isDayTime()) {
                    event.setDamage(event.getDamage() * 1.2);
                }
            }
            case WITHER_SKELETON -> {
                // Immune to wither effect - handled by potion application listener
            }
        }

        // IronDefense immunity
        if (plugin.getAbilityManager().isImmune(player.getUniqueId())) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        // Get attacker and victim
        Entity attacker = event.getDamager();
        Entity victimEntity = event.getEntity();

        Player attackerPlayer = null;
        if (attacker instanceof Player) {
            attackerPlayer = (Player) attacker;
        } else if (attacker instanceof org.bukkit.entity.Projectile proj) {
            ProjectileSource source = proj.getShooter();
            if (source instanceof Player) attackerPlayer = (Player) source;
        }

        // Skeleton passive: sharp arrows +20%
        if (attackerPlayer != null && attacker instanceof org.bukkit.entity.Projectile) {
            MobClass aCls = plugin.getMobClassManager().getMobClass(attackerPlayer.getUniqueId());
            if (aCls == MobClass.SKELETON) {
                event.setDamage(event.getDamage() * 1.2);
            }
        }

        if (attackerPlayer != null && victimEntity instanceof Player victimPlayer
                && plugin.getTeamManager().isTeammate(attackerPlayer.getUniqueId(), victimPlayer.getUniqueId())) {
            event.setCancelled(true);
            return;
        }

        // SKELETON weakness: melee damage +25% when receiving
        if (victimEntity instanceof Player victim && attacker instanceof Player) {
            MobClass vCls = plugin.getMobClassManager().getMobClass(victim.getUniqueId());
            if (vCls == MobClass.SKELETON) {
                event.setDamage(event.getDamage() * 1.25);
            }
        }

        // WITCH weakness: deal 25% less melee damage
        if (attackerPlayer != null && attacker instanceof Player) {
            MobClass aCls = plugin.getMobClassManager().getMobClass(attackerPlayer.getUniqueId());
            if (aCls == MobClass.WITCH && victimEntity instanceof Player) {
                event.setDamage(event.getDamage() * 0.75);
            }
        }

        // WITHER_SKELETON weakness: Smite deals extra damage
        if (victimEntity instanceof Player victim) {
            MobClass vCls = plugin.getMobClassManager().getMobClass(victim.getUniqueId());
            if (vCls == MobClass.WITHER_SKELETON && attackerPlayer != null) {
                ItemStack weapon = attackerPlayer.getInventory().getItemInMainHand();
                Enchantment smite = Enchantment.getByKey(NamespacedKey.minecraft("smite"));
                if (smite != null && weapon.containsEnchantment(smite)) {
                    int level = weapon.getEnchantmentLevel(smite);
                    event.setDamage(event.getDamage() + level * 2.5);
                }
            }
        }

        // IronDefense immunity
        if (victimEntity instanceof Player victim && plugin.getAbilityManager().isImmune(victim.getUniqueId())) {
            event.setCancelled(true);
        }

        if (attacker instanceof Player) {
            ItemStack weapon = attackerPlayer.getInventory().getItemInMainHand();
            if (weapon.getType() == Material.MACE) {
                long now = System.currentTimeMillis();
                int seconds = plugin.getConfig().getInt("mace-global-cooldown-seconds", 12);
                if (now < globalMaceCooldownUntil) {
                    long remaining = Math.max(1, (globalMaceCooldownUntil - now) / 1000L);
                    event.setCancelled(true);
                    attackerPlayer.sendActionBar(Component.text("§cMace Cooldown aktiv: " + remaining + "s"));
                    attackerPlayer.playSound(attackerPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.7f);
                    return;
                }
                globalMaceCooldownUntil = now + (seconds * 1000L);
                attackerPlayer.sendActionBar(Component.text("§6Mace Cooldown gestartet (" + seconds + "s global)"));
                attackerPlayer.playSound(attackerPlayer.getLocation(), Sound.ITEM_MACE_SMASH_GROUND_HEAVY, 1.0f, 1.0f);
            }
        }
    }
}
