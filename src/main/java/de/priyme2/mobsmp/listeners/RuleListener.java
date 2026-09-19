package de.priyme2.mobsmp.listeners;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.utils.Msg;
import de.priyme2.mobsmp.models.Team;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RuleListener implements Listener {
    private final MobSMP plugin;
    private final Map<UUID, Long> maceCooldowns = new HashMap<>();
    private final Map<UUID, Long> pearlCooldowns = new HashMap<>();
    private static final int MAX_PEARLS = 4;

    public RuleListener(MobSMP plugin) { 
        this.plugin = plugin;
        
        // Periodic pearl enforcement - runs every 10 ticks (0.5s), catches ALL cases
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                enforcePearlLimit(p);
            }
        }, 20L, 10L);
    }

    @EventHandler public void onPlace(BlockPlaceEvent e) {
        Material type = e.getBlock().getType();
        if(type == Material.END_CRYSTAL || type == Material.RESPAWN_ANCHOR) {
            e.setCancelled(true);
            e.getPlayer().sendMessage(Msg.error("Dieses Item ist deaktiviert!"));
        }
        if(type == Material.COBWEB) {
            ItemStack item = e.getItemInHand();
            if(item.hasItemMeta() && item.getItemMeta().getPersistentDataContainer().has(new NamespacedKey(plugin, "spider_web"), PersistentDataType.BYTE)) {
                // allow
            } else {
                e.setCancelled(true);
                e.getPlayer().sendMessage(Msg.error("Nur Spinnen duerfen Cobwebs platzieren!"));
            }
        }
    }

    @EventHandler public void onInteract(PlayerInteractEvent e) {
        if(e.getItem() != null && (e.getItem().getType() == Material.END_CRYSTAL || e.getItem().getType() == Material.RESPAWN_ANCHOR)) {
            e.setCancelled(true);
        }
        // Ender pearl throw cooldown (5s) - does NOT affect Enderman ability pearls
        if(e.getItem() != null && e.getItem().getType() == Material.ENDER_PEARL && (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            Player p = e.getPlayer();
            if(!p.hasPermission("mobsmp.bypass.cooldown")) {
                long cd = pearlCooldowns.getOrDefault(p.getUniqueId(), 0L);
                if(System.currentTimeMillis() < cd) {
                    e.setCancelled(true);
                    int secs = (int) Math.ceil((cd - System.currentTimeMillis()) / 1000.0);
                    p.sendMessage(Msg.warn("Enderperle Cooldown! (" + secs + "s)"));
                } else {
                    pearlCooldowns.put(p.getUniqueId(), System.currentTimeMillis() + 10000L);
                }
            }
        }
    }

    @EventHandler public void onDamage(EntityDamageByEntityEvent e) {
        if(e.getEntity() instanceof Player victim && e.getDamager() instanceof Player attacker) {
            Team t1 = plugin.getTeamManager().getTeamOf(victim.getUniqueId());
            Team t2 = plugin.getTeamManager().getTeamOf(attacker.getUniqueId());
            if(t1 != null && t2 != null && t1.name.equals(t2.name)) {
                e.setCancelled(true);
                return;
            }

            if(attacker.getInventory().getItemInMainHand().getType() == Material.MACE) {
                long cd = maceCooldowns.getOrDefault(attacker.getUniqueId(), 0L);
                if(System.currentTimeMillis() < cd) {
                    e.setCancelled(true);
                    attacker.sendMessage(Msg.warn("Mace Cooldown!"));
                } else {
                    maceCooldowns.put(attacker.getUniqueId(), System.currentTimeMillis() + 3000L);
                }
            }
        }
    }

    // Paper API: fires BEFORE the item enters inventory - most reliable pickup prevention
    @EventHandler(priority = EventPriority.LOWEST)
    public void onAttemptPickup(PlayerAttemptPickupItemEvent e) {
        if(e.getItem().getItemStack().getType() != Material.ENDER_PEARL) return;
        int count = countPearls(e.getPlayer());
        int incoming = e.getItem().getItemStack().getAmount();
        if(count >= MAX_PEARLS) {
            e.setCancelled(true);
            return;
        }
        if(count + incoming > MAX_PEARLS) {
            int canTake = MAX_PEARLS - count;
            e.setCancelled(true);
            ItemStack give = new ItemStack(Material.ENDER_PEARL, canTake);
            e.getPlayer().getInventory().addItem(give);
            e.getItem().getItemStack().setAmount(incoming - canTake);
            e.getPlayer().sendMessage(Msg.warn("Max. " + MAX_PEARLS + " Enderperlen!"));
        }
    }

    @EventHandler public void onInvClick(InventoryClickEvent e) {
        String title = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(e.getView().title());
        if (title.contains("\u1d0d\u1d0f\u0299\ua731\u1d0d\u1d18 \u1d0b\u029f\u1d00\ua731\ua731\u1d07\u0274")) {
            e.setCancelled(true);
        }
    }

    private int countPearls(Player p) {
        int count = 0;
        for(ItemStack i : p.getInventory().getContents()) {
            if(i != null && i.getType() == Material.ENDER_PEARL) count += i.getAmount();
        }
        return count;
    }

    private void enforcePearlLimit(Player p) {
        int count = countPearls(p);
        if(count <= MAX_PEARLS) return;
        
        int toRemove = count - MAX_PEARLS;
        for(int i = p.getInventory().getSize() - 1; i >= 0; i--) {
            ItemStack item = p.getInventory().getItem(i);
            if(item != null && item.getType() == Material.ENDER_PEARL) {
                if(item.getAmount() <= toRemove) {
                    toRemove -= item.getAmount();
                    p.getInventory().setItem(i, null);
                } else {
                    item.setAmount(item.getAmount() - toRemove);
                    toRemove = 0;
                }
                if(toRemove <= 0) break;
            }
        }
        p.updateInventory();
        p.sendMessage(Msg.error("Max. " + MAX_PEARLS + " Enderperlen!"));
    }

    @EventHandler public void onTarget(org.bukkit.event.entity.EntityTargetEvent e) {
        if(e.getEntity().hasMetadata("summoner") && e.getTarget() instanceof Player target) {
            String summonerUuid = e.getEntity().getMetadata("summoner").get(0).asString();
            if(target.getUniqueId().toString().equals(summonerUuid)) {
                e.setCancelled(true);
            }
        }
    }
}
