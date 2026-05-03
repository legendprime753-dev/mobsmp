package de.priyme2.mobsmp.abilities.mobs;

import de.priyme2.mobsmp.abilities.MobMechanic;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public abstract class BaseMobMechanic implements MobMechanic {
    @Override public void applyPassiveStats(Player player) {}
    @Override public void onTick(Player player) {}
    @Override public void onAbilityUnlock1(Player player, PlayerInteractEvent event) {}
    @Override public void onAbilityUnlock2(Player player, PlayerInteractEvent event) {}

    protected void effect(Player p, PotionEffectType type, int seconds, int amplifier) {
        p.addPotionEffect(new PotionEffect(type, seconds * 20, amplifier, true, false, true));
    }

    protected void damageNearby(Player source, double radius, double damage, Vector knockback) {
        for (LivingEntity entity : source.getLocation().getNearbyLivingEntities(radius)) {
            if (entity.equals(source)) continue;
            entity.damage(damage, source);
            if (knockback != null) entity.setVelocity(knockback);
        }
    }

    protected void playCast(Player p) {
        p.getWorld().playSound(p.getLocation(), Sound.ENTITY_EVOKER_CAST_SPELL, 1f, 1.2f);
        p.getWorld().spawnParticle(Particle.ENCHANT, p.getLocation().add(0, 1, 0), 25, .3, .3, .3, 0.01);
    }

    protected void setMaxHealth(Player p, double hp) {
        var attr = p.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (attr != null) {
            attr.setBaseValue(hp);
            p.setHealth(Math.min(hp, p.getHealth()));
        }
    }

    protected Player rayTargetPlayer(Player p, double maxDist) {
        var hit = p.getWorld().rayTraceEntities(p.getEyeLocation(), p.getEyeLocation().getDirection(), maxDist, e -> e instanceof Player && !e.equals(p));
        return hit != null && hit.getHitEntity() instanceof Player target ? target : null;
    }
}
