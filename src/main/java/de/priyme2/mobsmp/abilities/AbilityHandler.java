package de.priyme2.mobsmp.abilities;

import de.priyme2.mobsmp.abilities.mobs.*;
import de.priyme2.mobsmp.models.MobClass;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AbilityHandler {
    private final EnumMap<MobClass, MobMechanic> mechanics = new EnumMap<>(MobClass.class);
    private final Map<UUID, Map<String, Long>> cooldowns = new HashMap<>();
    public AbilityHandler() {
        mechanics.put(MobClass.CHICKEN, new ChickenMechanic());
        mechanics.put(MobClass.TURTLE, new TurtleMechanic());
        mechanics.put(MobClass.VILLAGER, new VillagerMechanic());
        mechanics.put(MobClass.ZOMBIE, new ZombieMechanic());
        mechanics.put(MobClass.CREEPER, new CreeperMechanic());
        mechanics.put(MobClass.SPIDER, new SpiderMechanic());
        mechanics.put(MobClass.WITCH, new WitchMechanic());
        mechanics.put(MobClass.DRAGON, new DragonMechanic());
        mechanics.put(MobClass.WARDEN, new WardenMechanic());
        mechanics.put(MobClass.WITHER, new WitherMechanic());
        mechanics.put(MobClass.ENDERMAN, new EndermanMechanic());
        mechanics.put(MobClass.SHULKER, new ShulkerMechanic());
        mechanics.put(MobClass.PIGLIN, new PiglinMechanic());
        mechanics.put(MobClass.BLAZE, new BlazeMechanic());
    }
    public MobMechanic get(MobClass mobClass){return mechanics.get(mobClass);}    
    public boolean isOnCooldown(UUID uuid,String key){
        org.bukkit.entity.Player p = org.bukkit.Bukkit.getPlayer(uuid);
        if(p != null && p.hasPermission("mobsmp.bypass.cooldown")) return false;
        return cooldowns.getOrDefault(uuid, Map.of()).getOrDefault(key,0L)>System.currentTimeMillis();
    }
    public void setCooldown(UUID uuid,String key,long millis){cooldowns.computeIfAbsent(uuid,k->new HashMap<>()).put(key,System.currentTimeMillis()+millis);}    
    public long getCooldownLeft(UUID uuid,String key){return Math.max(0, cooldowns.getOrDefault(uuid, Map.of()).getOrDefault(key,0L) - System.currentTimeMillis());}
    
    /** Remove cooldown data for players who are no longer online */
    public void cleanupOffline() {
        long now = System.currentTimeMillis();
        cooldowns.entrySet().removeIf(entry -> {
            if(org.bukkit.Bukkit.getPlayer(entry.getKey()) != null) return false;
            // Remove if all cooldowns expired
            return entry.getValue().values().stream().allMatch(expire -> expire < now);
        });
    }
}
