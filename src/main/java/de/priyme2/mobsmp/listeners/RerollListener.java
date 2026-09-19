package de.priyme2.mobsmp.listeners;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.utils.Msg;
import de.priyme2.mobsmp.models.MobClass;
import de.priyme2.mobsmp.models.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Random;

public class RerollListener implements Listener {
    private final MobSMP plugin;
    public RerollListener(MobSMP plugin) { this.plugin = plugin; }

    @EventHandler public void onInteract(PlayerInteractEvent e) {
        if(e.getItem() == null) return;
        if(plugin.getRerollManager().isRerollBook(e.getItem())) {
            e.setCancelled(true);
            if(e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Player p = e.getPlayer();
                PlayerData d = plugin.getStorageManager().getData(p.getUniqueId());
                
                MobClass oldClass = d.mobClass;
                MobClass newClass;
                do {
                    newClass = plugin.randomClass();
                } while (newClass == oldClass);

                ItemStack item = e.getItem();
                item.setAmount(item.getAmount() - 1);
                
                p.addPotionEffect(new org.bukkit.potion.PotionEffect(org.bukkit.potion.PotionEffectType.RESISTANCE, 100, 4));

                final MobClass finalNewClass = newClass;

                new org.bukkit.scheduler.BukkitRunnable() {
                    int ticks = 0;
                    int maxTicks = 40;
                    int delay = 2;
                    
                    @Override
                    public void run() {
                        if(ticks >= maxTicks) {
                            d.mobClass = finalNewClass;
                            // Reset health from previous class (e.g. Enderman 26hp)
                            var attr = p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
                            if (attr != null) { attr.setBaseValue(20.0); p.setHealth(Math.min(20.0, p.getHealth())); }
                            for(org.bukkit.potion.PotionEffect eff : p.getActivePotionEffects()) p.removePotionEffect(eff.getType());
                            
                            p.showTitle(net.kyori.adventure.title.Title.title(Component.text("REROLL", NamedTextColor.GOLD), Component.text("Neue Klasse: ", NamedTextColor.WHITE).append(finalNewClass.getColoredName())));
                            p.playSound(p.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1f, 0.8f);
                            p.getWorld().spawnParticle(org.bukkit.Particle.EXPLOSION_EMITTER, p.getLocation().add(0, 1, 0), 1);
                            p.sendMessage(Msg.withComponent("Neue Klasse: ", net.kyori.adventure.text.format.TextColor.color(0x55FF55), finalNewClass.getColoredName()));
                            
                            for(ItemStack i : p.getInventory().getContents()) {
                                if(i != null && plugin.getTokenManager().isToken(i)) i.setAmount(0);
                            }
                            plugin.getTokenManager().giveTokenIfNotPresent(p);
                            this.cancel();
                            return;
                        }
                        
                        if(ticks % delay == 0) {
                            MobClass randomSpin = plugin.randomClass();
                            p.showTitle(net.kyori.adventure.title.Title.title(Component.text("REROLL", NamedTextColor.GOLD), randomSpin.getColoredName()));
                            p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.5f);
                            
                            if(ticks > 20) delay = 3;
                            if(ticks > 30) delay = 5;
                        }
                        ticks++;
                    }
                }.runTaskTimer(plugin, 0L, 1L);
            }
        }
    }
}
