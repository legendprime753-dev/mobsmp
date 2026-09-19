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
        org.bukkit.Particle part = org.bukkit.Particle.ENCHANT;
        org.bukkit.Sound sound = org.bukkit.Sound.ENTITY_EVOKER_CAST_SPELL;
        
        String name = this.getClass().getSimpleName().replace("Mechanic", "").toUpperCase();
        switch(name) {
            case "BLAZE": part = org.bukkit.Particle.FLAME; sound = org.bukkit.Sound.ENTITY_BLAZE_SHOOT; break;
            case "CREEPER": part = org.bukkit.Particle.EXPLOSION; sound = org.bukkit.Sound.ENTITY_CREEPER_PRIMED; break;
            case "ENDERMAN": part = org.bukkit.Particle.PORTAL; sound = org.bukkit.Sound.ENTITY_ENDERMAN_TELEPORT; break;
            case "WARDEN": part = org.bukkit.Particle.SONIC_BOOM; sound = org.bukkit.Sound.ENTITY_WARDEN_SONIC_BOOM; break;
            case "SPIDER": part = org.bukkit.Particle.WHITE_ASH; sound = org.bukkit.Sound.ENTITY_SPIDER_AMBIENT; break;
            case "ZOMBIE": part = org.bukkit.Particle.ENTITY_EFFECT; sound = org.bukkit.Sound.ENTITY_ZOMBIE_INFECT; break;
            case "WITHER": part = org.bukkit.Particle.SMOKE; sound = org.bukkit.Sound.ENTITY_WITHER_SHOOT; break;
            case "PIGLIN": part = org.bukkit.Particle.SOUL_FIRE_FLAME; sound = org.bukkit.Sound.ENTITY_PIGLIN_ANGRY; break;
            case "CHICKEN": part = org.bukkit.Particle.ITEM_SNOWBALL; sound = org.bukkit.Sound.ENTITY_CHICKEN_EGG; break;
            case "TURTLE": part = org.bukkit.Particle.SPLASH; sound = org.bukkit.Sound.ENTITY_TURTLE_AMBIENT_LAND; break;
            case "SHULKER": part = org.bukkit.Particle.END_ROD; sound = org.bukkit.Sound.ENTITY_SHULKER_SHOOT; break;
            case "WITCH": part = org.bukkit.Particle.WITCH; sound = org.bukkit.Sound.ENTITY_WITCH_THROW; break;
            case "DRAGON": part = org.bukkit.Particle.DRAGON_BREATH; sound = org.bukkit.Sound.ENTITY_ENDER_DRAGON_GROWL; break;
            case "VILLAGER": part = org.bukkit.Particle.HAPPY_VILLAGER; sound = org.bukkit.Sound.ENTITY_VILLAGER_YES; break;
        }

        p.getWorld().playSound(p.getLocation(), sound, 1f, 1.2f);
        p.getWorld().spawnParticle(part, p.getLocation().add(0, 1, 0), 30, .4, .4, .4, 0.05);
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
