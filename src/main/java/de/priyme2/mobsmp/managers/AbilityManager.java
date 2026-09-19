package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.MobClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class AbilityManager {
    private final MobSMP plugin;
    private final Map<UUID, Long> ability3kCooldowns = new HashMap<>();
    private final Map<UUID, Long> ability5kCooldowns = new HashMap<>();
    private final Set<UUID> immunePlayers = new HashSet<>();
    private final Set<UUID> superChargedPlayers = new HashSet<>();

    public AbilityManager(MobSMP plugin) {
        this.plugin = plugin;
        startPassiveTick();
    }

    private void startPassiveTick() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    MobClass cls = plugin.getMobClassManager().getMobClass(player.getUniqueId());
                    if (cls == null) continue;
                    applyPassiveTick(player, cls);
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }

    private void applyPassiveTick(Player player, MobClass cls) {
        switch (cls) {
            case SPIDER -> {
                // In sunlight: slowness I
                if (player.getWorld().isDayTime() && player.getLocation().getBlock().getLightFromSky() >= 15) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0, false, false));
                }
            }
            case ENDERMAN -> {
                // Water/rain damage
                if (isInWaterOrRain(player)) {
                    player.damage(1.0);
                }
            }
            case BLAZE -> {
                // Water/rain damage (2 per second)
                if (isInWaterOrRain(player)) {
                    player.damage(2.0);
                }
            }
            case IRON_GOLEM -> {
                // Ensure knockback resistance and movement speed
                var kbAttr = player.getAttribute(Attribute.KNOCKBACK_RESISTANCE);
                if (kbAttr != null && kbAttr.getBaseValue() < 0.75) kbAttr.setBaseValue(0.75);
                var msAttr = player.getAttribute(Attribute.MOVEMENT_SPEED);
                if (msAttr != null && msAttr.getBaseValue() > 0.18) msAttr.setBaseValue(0.18);
            }
            case CREEPER -> {
                // Cat weakness: check for nearby cat/ocelot
                for (Entity e : player.getNearbyEntities(4, 4, 4)) {
                    if (e.getType() == EntityType.CAT || e.getType() == EntityType.OCELOT) {
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 0, false, false));
                        break;
                    }
                }
            }
        }
    }

    private boolean isInWaterOrRain(Player player) {
        if (player.getLocation().getBlock().getType() == Material.WATER) return true;
        return player.getWorld().hasStorm() && player.getLocation().getBlock().getLightFromSky() >= 14;
    }

    public void applyClassAttributes(Player player, MobClass cls) {
        // Reset defaults first
        var kbAttr = player.getAttribute(Attribute.KNOCKBACK_RESISTANCE);
        if (kbAttr != null) kbAttr.setBaseValue(0.0);
        var msAttr = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (msAttr != null) msAttr.setBaseValue(0.2);
        var atkSpeedAttr = player.getAttribute(Attribute.ATTACK_SPEED);
        if (atkSpeedAttr != null) {
            double configuredAttackSpeed = plugin.getConfig().getDouble("global-attack-speed-base", 3.2);
            atkSpeedAttr.setBaseValue(configuredAttackSpeed);
        }

        if (cls == MobClass.IRON_GOLEM) {
            if (kbAttr != null) kbAttr.setBaseValue(0.75);
            if (msAttr != null) msAttr.setBaseValue(0.18);
        }
    }

    public boolean useAbility1(Player player) {
        MobClass cls = plugin.getMobClassManager().getMobClass(player.getUniqueId());
        if (cls == null) { player.sendMessage(ChatColor.RED + "You have no mob class!"); return false; }
        if (!plugin.getKillManager().hasAbility3k(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You need 3 kills to use this ability!");
            return false;
        }
        boolean bypassCooldown = player.hasPermission("mobsmp.ability.nocooldown");
        long now = System.currentTimeMillis();
        long cd = getAbility1Cooldown(cls) * 1000L;
        if (!bypassCooldown && ability3kCooldowns.containsKey(player.getUniqueId())) {
            long remaining = (ability3kCooldowns.get(player.getUniqueId()) + cd) - now;
            if (remaining > 0) {
                player.sendMessage(ChatColor.RED + "Ability on cooldown! " + (remaining / 1000) + "s remaining.");
                return false;
            }
        }
        boolean used = activate3kAbility(player, cls);
        if (used && !bypassCooldown) ability3kCooldowns.put(player.getUniqueId(), now);
        return used;
    }

    public boolean useAbility2(Player player) {
        MobClass cls = plugin.getMobClassManager().getMobClass(player.getUniqueId());
        if (cls == null) { player.sendMessage(ChatColor.RED + "You have no mob class!"); return false; }
        if (!plugin.getKillManager().hasAbility5k(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You need 5 kills to use this ability!");
            return false;
        }
        boolean bypassCooldown = player.hasPermission("mobsmp.ability.nocooldown");
        long now = System.currentTimeMillis();
        long cd = getAbility2Cooldown(cls) * 1000L;
        if (!bypassCooldown && ability5kCooldowns.containsKey(player.getUniqueId())) {
            long remaining = (ability5kCooldowns.get(player.getUniqueId()) + cd) - now;
            if (remaining > 0) {
                player.sendMessage(ChatColor.RED + "Ability on cooldown! " + (remaining / 1000) + "s remaining.");
                return false;
            }
        }
        boolean used = activate5kAbility(player, cls);
        if (used && !bypassCooldown) ability5kCooldowns.put(player.getUniqueId(), now);
        return used;
    }

    private int getAbility1Cooldown(MobClass cls) {
        return switch (cls) {
            case ZOMBIE -> 120;
            case SKELETON -> 90;
            case CREEPER -> 90;
            case SPIDER -> 60;
            case ENDERMAN -> 45;
            case BLAZE -> 30;
            case WITCH -> 45;
            case IRON_GOLEM -> 60;
            case PHANTOM -> 60;
            case WITHER_SKELETON -> 45;
        };
    }

    private int getAbility2Cooldown(MobClass cls) {
        return switch (cls) {
            case ZOMBIE -> 60;
            case SKELETON -> 60;
            case CREEPER -> 120;
            case SPIDER -> 90;
            case ENDERMAN -> 120;
            case BLAZE -> 90;
            case WITCH -> 90;
            case IRON_GOLEM -> 120;
            case PHANTOM -> 90;
            case WITHER_SKELETON -> 90;
        };
    }

    private boolean activate3kAbility(Player player, MobClass cls) {
        switch (cls) {
            case ZOMBIE -> {
                // ZombieHorde: summon 3 zombies for 15s
                player.sendMessage(ChatColor.GREEN + "Zombie Horde summoned!");
                List<Entity> zombies = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    Entity e = player.getWorld().spawnEntity(player.getLocation(), EntityType.ZOMBIE);
                    zombies.add(e);
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        for (Entity e : zombies) e.remove();
                    }
                }.runTaskLater(plugin, 300L);
            }
            case SKELETON -> {
                // BoneShield: resistance 2 for 8s (damage reduction ~40%)
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 160, 1));
                player.sendMessage(ChatColor.WHITE + "Bone Shield activated!");
            }
            case CREEPER -> {
                // MiniExplosion at target location
                var targetBlock = player.getTargetBlockExact(10);
                if (targetBlock == null) {
                    player.sendMessage(ChatColor.RED + "No target block in range!");
                    return false;
                }
                Location target = targetBlock.getLocation();
                player.getWorld().createExplosion(target, 2.0f, false, false, player);
                player.sendMessage(ChatColor.GREEN + "Mini Explosion!");
            }
            case SPIDER -> {
                // WebTrap
                Player target = getNearestEnemy(player, 10.0);
                if (target != null) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 160, 1));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.MINING_FATIGUE, 160, 1));
                    player.sendMessage(ChatColor.DARK_RED + "Web Trap applied to " + target.getName() + "!");
                } else {
                    player.sendMessage(ChatColor.RED + "No enemy nearby!");
                    return false;
                }
            }
            case ENDERMAN -> {
                // TeleportAssault
                Player target = getNearestEnemy(player, 20.0);
                if (target != null) {
                    Location behind = target.getLocation().add(target.getLocation().getDirection().multiply(-2));
                    behind.setYaw(target.getLocation().getYaw() + 180);
                    player.teleport(behind);
                    target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
                    player.sendMessage(ChatColor.DARK_PURPLE + "Teleported behind " + target.getName() + "!");
                } else {
                    player.sendMessage(ChatColor.RED + "No enemy nearby!");
                    return false;
                }
            }
            case BLAZE -> {
                // Fireball
                Location loc = player.getEyeLocation();
                Vector dir = loc.getDirection().normalize();
                Fireball fireball = player.getWorld().spawn(loc.add(dir.clone().multiply(2)), Fireball.class);
                fireball.setDirection(dir.clone().multiply(2));
                fireball.setYield(4.0f);
                fireball.setIsIncendiary(true);
                fireball.setShooter(player);
                player.sendMessage(ChatColor.GOLD + "Fireball launched!");
            }
            case WITCH -> {
                // PotionSplash at nearest enemy
                Player target = getNearestEnemy(player, 10.0);
                if (target != null) {
                    ThrownPotion potion = player.launchProjectile(ThrownPotion.class);
                    // Set it to splash harming
                    org.bukkit.inventory.ItemStack potionItem = new org.bukkit.inventory.ItemStack(Material.SPLASH_POTION);
                    org.bukkit.inventory.meta.PotionMeta meta = (org.bukkit.inventory.meta.PotionMeta) potionItem.getItemMeta();
                    if (meta != null) {
                        meta.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 1), true);
                        potionItem.setItemMeta(meta);
                    }
                    potion.setItem(potionItem);
                    Vector vel = target.getLocation().subtract(player.getLocation()).toVector().normalize().multiply(0.5).add(new Vector(0, 0.3, 0));
                    potion.setVelocity(vel);
                    player.sendMessage(ChatColor.DARK_PURPLE + "Potion Splash!");
                } else {
                    player.sendMessage(ChatColor.RED + "No enemy nearby!");
                    return false;
                }
            }
            case IRON_GOLEM -> {
                // Earthquake
                for (Entity e : player.getNearbyEntities(5, 5, 5)) {
                    if (e instanceof Player target) {
                        if (plugin.getTeamManager().isTeammate(player.getUniqueId(), target.getUniqueId())) continue;
                        target.damage(6.0, player);
                        Vector knockback = target.getLocation().subtract(player.getLocation()).toVector().normalize().multiply(2).add(new Vector(0, 0.5, 0));
                        target.setVelocity(knockback);
                    }
                }
                player.sendMessage(ChatColor.GRAY + "Earthquake!");
            }
            case PHANTOM -> {
                // PhantomDive
                Location up = player.getLocation().clone().add(0, 10, 0);
                player.teleport(up);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Location landLoc = player.getLocation();
                        for (Entity e : player.getNearbyEntities(4, 4, 4)) {
                            if (e instanceof Player target) {
                                if (!plugin.getTeamManager().isTeammate(player.getUniqueId(), target.getUniqueId())) {
                                    target.damage(8.0, player);
                                }
                            }
                        }
                        player.getWorld().createExplosion(landLoc, 0, false, false, player);
                    }
                }.runTaskLater(plugin, 20L);
                player.sendMessage(ChatColor.BLUE + "Phantom Dive!");
            }
            case WITHER_SKELETON -> {
                // WitherTouch
                Player target = getNearestEnemy(player, 10.0);
                if (target != null) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 100, 1));
                    player.sendMessage(ChatColor.BLACK + "Wither Touch applied to " + target.getName() + "!");
                } else {
                    player.sendMessage(ChatColor.RED + "No enemy nearby!");
                    return false;
                }
            }
        }
        return true;
    }

    private boolean activate5kAbility(Player player, MobClass cls) {
        switch (cls) {
            case ZOMBIE -> {
                // UndeadRegen: regen when <50% HP for 10s
                if (player.getHealth() < player.getAttribute(Attribute.MAX_HEALTH).getValue() * 0.5) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
                    player.sendMessage(ChatColor.GREEN + "Undead Regen activated!");
                } else {
                    player.sendMessage(ChatColor.RED + "You need to be below 50% health!");
                    return false;
                }
            }
            case SKELETON -> {
                // ArrowRain
                for (int i = 0; i < 8; i++) {
                    Arrow arrow = player.launchProjectile(Arrow.class);
                    double spread = 0.3;
                    Vector dir = player.getLocation().getDirection().clone();
                    dir.add(new Vector(
                            (Math.random() - 0.5) * spread,
                            (Math.random() - 0.5) * spread,
                            (Math.random() - 0.5) * spread
                    )).normalize();
                    arrow.setVelocity(dir.multiply(3));
                    arrow.setShooter(player);
                }
                player.sendMessage(ChatColor.WHITE + "Arrow Rain!");
            }
            case CREEPER -> {
                // SuperCharge: next explosion is 2x
                superChargedPlayers.add(player.getUniqueId());
                player.sendMessage(ChatColor.GREEN + "Super Charge ready! Next explosion will be 2x power.");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        superChargedPlayers.remove(player.getUniqueId());
                    }
                }.runTaskLater(plugin, 2400L);
            }
            case SPIDER -> {
                // VenomBite
                for (Entity e : player.getNearbyEntities(4, 4, 4)) {
                    if (e instanceof Player target) {
                        if (plugin.getTeamManager().isTeammate(player.getUniqueId(), target.getUniqueId())) continue;
                        target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 200, 1));
                    }
                }
                player.sendMessage(ChatColor.DARK_RED + "Venom Bite!");
            }
            case ENDERMAN -> {
                // VoidGrasp
                Player nearest = getNearestEnemy(player, 15.0);
                if (nearest != null) {
                    nearest.teleport(player.getLocation());
                    player.sendMessage(ChatColor.DARK_PURPLE + "You pulled " + nearest.getName() + " to you!");
                } else {
                    player.sendMessage(ChatColor.RED + "No enemy nearby!");
                    return false;
                }
            }
            case BLAZE -> {
                // BlazeStorm
                for (int i = 0; i < 12; i++) {
                    double angle = (2 * Math.PI / 12) * i;
                    double x = Math.cos(angle) * 5;
                    double z = Math.sin(angle) * 5;
                    Location spawnLoc = player.getLocation().clone().add(x, 10, z);
                    SmallFireball sf = player.getWorld().spawn(spawnLoc, SmallFireball.class);
                    Vector dir = player.getLocation().subtract(spawnLoc).toVector().normalize().add(new Vector(0, -1, 0)).normalize();
                    sf.setDirection(dir);
                    sf.setShooter(player);
                }
                player.sendMessage(ChatColor.GOLD + "Blaze Storm!");
            }
            case WITCH -> {
                // HexCurse: apply debuffs to nearest 3 enemies within 8 blocks
                List<Player> enemies = getNearestEnemies(player, 8.0, 3);
                if (enemies.isEmpty()) {
                    player.sendMessage(ChatColor.RED + "No enemies nearby!");
                    return false;
                }
                for (Player target : enemies) {
                    target.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 160, 1));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 160, 1));
                    target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 160, 0));
                }
                player.sendMessage(ChatColor.DARK_PURPLE + "Hex Curse cast on " + enemies.size() + " enemies!");
            }
            case IRON_GOLEM -> {
                // IronDefense
                immunePlayers.add(player.getUniqueId());
                player.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 4));
                player.sendMessage(ChatColor.GRAY + "Iron Defense activated!");
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        immunePlayers.remove(player.getUniqueId());
                    }
                }.runTaskLater(plugin, 100L);
            }
            case PHANTOM -> {
                // NightTerror: only at night
                if (player.getWorld().isDayTime()) {
                    player.sendMessage(ChatColor.RED + "Night Terror can only be used at night!");
                    return false;
                }
                for (Entity e : player.getNearbyEntities(6, 6, 6)) {
                    if (e instanceof Player target) {
                        if (plugin.getTeamManager().isTeammate(player.getUniqueId(), target.getUniqueId())) continue;
                        target.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 200, 0));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 200, 0));
                    }
                }
                player.sendMessage(ChatColor.BLUE + "Night Terror unleashed!");
            }
            case WITHER_SKELETON -> {
                // SoulHarvest
                for (Entity e : player.getNearbyEntities(4, 4, 4)) {
                    if (e instanceof Player target) {
                        if (plugin.getTeamManager().isTeammate(player.getUniqueId(), target.getUniqueId())) continue;
                        target.damage(4.0, player);
                        double maxHp = player.getAttribute(Attribute.MAX_HEALTH).getValue();
                        player.setHealth(Math.min(player.getHealth() + 4.0, maxHp));
                    }
                }
                player.sendMessage(ChatColor.BLACK + "Soul Harvest!");
            }
        }
        return true;
    }

    private Player getNearestEnemy(Player player, double range) {
        Player nearest = null;
        double minDist = range;
        for (Player p : player.getWorld().getPlayers()) {
            if (p == player) continue;
            if (plugin.getTeamManager().isTeammate(player.getUniqueId(), p.getUniqueId())) continue;
            double dist = p.getLocation().distance(player.getLocation());
            if (dist < minDist) {
                minDist = dist;
                nearest = p;
            }
        }
        return nearest;
    }

    private List<Player> getNearestEnemies(Player player, double range, int maxCount) {
        List<Player> result = new ArrayList<>();
        for (Player p : player.getWorld().getPlayers()) {
            if (p == player) continue;
            if (plugin.getTeamManager().isTeammate(player.getUniqueId(), p.getUniqueId())) continue;
            if (p.getLocation().distance(player.getLocation()) <= range) {
                result.add(p);
                if (result.size() >= maxCount) break;
            }
        }
        return result;
    }

    public boolean isImmune(UUID uuid) {
        return immunePlayers.contains(uuid);
    }

    public boolean isSuperCharged(UUID uuid) {
        return superChargedPlayers.contains(uuid);
    }

    public void removeSuperCharge(UUID uuid) {
        superChargedPlayers.remove(uuid);
    }
}
