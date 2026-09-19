package de.priyme2.mobsmp.commands;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.utils.Msg;
import de.priyme2.mobsmp.models.MobClass;
import de.priyme2.mobsmp.models.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MobSMPCommand implements CommandExecutor, TabCompleter {
    private final MobSMP plugin;
    public MobSMPCommand(MobSMP plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("concept")) {
            if (!(sender instanceof Player p)) return true;
            
            org.bukkit.inventory.Inventory inv = Bukkit.createInventory(null, 27, 
                Component.text("「", net.kyori.adventure.text.format.TextColor.color(0x555555))
                    .append(Component.text("ᴍᴏʙꜱᴍᴘ ᴋʟᴀꜱꜱᴇɴ", net.kyori.adventure.text.format.TextColor.color(0xFFAA00)))
                    .append(Component.text("」", net.kyori.adventure.text.format.TextColor.color(0x555555))));
            
            for (MobClass c : MobClass.values()) {
                org.bukkit.Material mat = org.bukkit.Material.STONE;
                switch(c) {
                    case ZOMBIE: mat = org.bukkit.Material.ZOMBIE_HEAD; break;
                    case CREEPER: mat = org.bukkit.Material.CREEPER_HEAD; break;
                    case DRAGON: mat = org.bukkit.Material.DRAGON_HEAD; break;
                    case WITHER: mat = org.bukkit.Material.WITHER_SKELETON_SKULL; break;
                    case SPIDER: mat = org.bukkit.Material.COBWEB; break;
                    case ENDERMAN: mat = org.bukkit.Material.ENDER_PEARL; break;
                    case WARDEN: mat = org.bukkit.Material.SCULK_SHRIEKER; break;
                    case VILLAGER: mat = org.bukkit.Material.EMERALD; break;
                    case BLAZE: mat = org.bukkit.Material.BLAZE_POWDER; break;
                    case PIGLIN: mat = org.bukkit.Material.GOLD_INGOT; break;
                    case CHICKEN: mat = org.bukkit.Material.FEATHER; break;
                    case TURTLE: mat = org.bukkit.Material.TURTLE_SCUTE; break;
                    case SHULKER: mat = org.bukkit.Material.SHULKER_SHELL; break;
                    case WITCH: mat = org.bukkit.Material.SPLASH_POTION; break;
                }
                
                org.bukkit.inventory.ItemStack item = new org.bukkit.inventory.ItemStack(mat);
                org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();
                meta.displayName(Component.text(Msg.small("Klasse: "), net.kyori.adventure.text.format.TextColor.color(0xAAAAAA)).append(c.getColoredName()));
                meta.lore(c.getLore());
                item.setItemMeta(meta);
                inv.addItem(item);
            }
            
            p.openInventory(inv);
            return true;
        }

        if(!sender.hasPermission("mobsmp.admin")) {
            sender.sendMessage(Msg.error("Keine Rechte!"));
            return true;
        }

        if(args[0].equalsIgnoreCase("setclass") && args.length == 3) {
            Player p = Bukkit.getPlayer(args[1]);
            if(p == null) { sender.sendMessage(Msg.error("Spieler nicht gefunden.")); return true; }
            try {
                MobClass c = MobClass.valueOf(args[2].toUpperCase());
                PlayerData d = plugin.getStorageManager().getData(p.getUniqueId());
                d.mobClass = c;
                var attr = p.getAttribute(org.bukkit.attribute.Attribute.GENERIC_MAX_HEALTH);
                if (attr != null) { attr.setBaseValue(20.0); p.setHealth(Math.min(20.0, p.getHealth())); }
                for(org.bukkit.potion.PotionEffect eff : p.getActivePotionEffects()) p.removePotionEffect(eff.getType());
                sender.sendMessage(Msg.success("Klasse von " + p.getName() + " auf " + c.name() + " gesetzt."));
                p.sendMessage(Msg.special("Deine Klasse wurde zu " + c.name() + " geaendert!"));
                plugin.getTokenManager().giveTokenIfNotPresent(p);
            } catch (Exception e) {
                sender.sendMessage(Msg.error("Ungueltige Klasse."));
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("reset") && args.length == 2) {
            Player p = Bukkit.getPlayer(args[1]);
            if(p != null) {
                PlayerData d = plugin.getStorageManager().getData(p.getUniqueId());
                d.kills = 0; d.totalKills = 0; d.deaths = 0; d.consecutiveDeathsAtZeroKills = 0;
                sender.sendMessage(Msg.success("Werte von " + p.getName() + " zurueckgesetzt."));
            }
            return true;
        }

        if(args[0].equalsIgnoreCase("setkills") && args.length == 3) {
            Player p = Bukkit.getPlayer(args[1]);
            if(p == null) { sender.sendMessage(Msg.error("Spieler nicht gefunden.")); return true; }
            try {
                int kills = Integer.parseInt(args[2]);
                if(kills < 0 || kills > 5) { sender.sendMessage(Msg.error("Kills muessen zwischen 0-5 sein.")); return true; }
                PlayerData d = plugin.getStorageManager().getData(p.getUniqueId());
                d.kills = kills;
                sender.sendMessage(Msg.success("Kills von " + p.getName() + " auf " + kills + " gesetzt."));
                p.sendMessage(Msg.special("Deine Kill-Progression wurde auf " + kills + " gesetzt."));
            } catch(NumberFormatException ex) {
                sender.sendMessage(Msg.error("Ungueltige Zahl."));
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("event")) {
            if(args.length == 3 && args[1].equalsIgnoreCase("start")) {
                plugin.getEventManager().startEvent(args[2], plugin.getConfig().getLong("events.duration-seconds", 600));
                return true;
            } else if(args[1].equalsIgnoreCase("stop")) {
                if(args.length == 3) {
                    plugin.getEventManager().stopEvent(args[2]);
                    Bukkit.broadcast(Msg.info("Event beendet: " + args[2].toUpperCase()));
                } else {
                    plugin.getEventManager().stopAll();
                    Bukkit.broadcast(Msg.info("Alle Events beendet."));
                }
                return true;
            }
        }
        
        if (args[0].equalsIgnoreCase("give") && args.length >= 3) {
            Player target = Bukkit.getPlayer(args[2]);
            if(target == null) { sender.sendMessage(Msg.error("Spieler nicht gefunden.")); return true; }
            
            int amount = 1;
            if(args.length == 4) {
                try { amount = Integer.parseInt(args[3]); } catch(Exception ignored) {}
            }
            
            if (args[1].equalsIgnoreCase("killshard")) {
                org.bukkit.inventory.ItemStack item = plugin.getRerollManager().createFragment();
                item.setAmount(amount);
                target.getInventory().addItem(item);
                sender.sendMessage(Msg.success(amount + "x Kill Fragment an " + target.getName() + " gegeben."));
            } else if (args[1].equalsIgnoreCase("rerollbook")) {
                org.bukkit.inventory.ItemStack item = plugin.getRerollManager().createRerollBook();
                item.setAmount(amount);
                target.getInventory().addItem(item);
                sender.sendMessage(Msg.success(amount + "x Reroll Buch an " + target.getName() + " gegeben."));
            }
            return true;
        }

        return false;
    }

    private void sendConcept(CommandSender sender) {
        sender.sendMessage(Msg.special("Mob SMP - Creator Only"));
        sender.sendMessage(Msg.info("Jeder Spieler startet als zufaelliger Mob mit eigenen Faehigkeiten."));
        sender.sendMessage(Msg.info("Durch Kills schaltest du Abilities frei - bei Tod verlierst du Fortschritt."));
        sender.sendMessage(Msg.info("Starke Spieler werden durch Bounty- und Tracking-Systeme zur Zielscheibe."));
        sender.sendMessage(Msg.info("Progression - PvP - Strategie - Allianzen - Content"));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        List<String> comps = new ArrayList<>();
        if(args.length == 1) {
            comps.add("info"); comps.add("concept");
            if(sender.hasPermission("mobsmp.admin")) {
                comps.add("setclass"); comps.add("setkills"); comps.add("reset"); comps.add("event"); comps.add("give");
            }
        } else if(args.length == 2 && args[0].equalsIgnoreCase("setclass")) {
            for(Player p : Bukkit.getOnlinePlayers()) comps.add(p.getName());
        } else if(args.length == 3 && args[0].equalsIgnoreCase("setclass")) {
            for(MobClass c : MobClass.values()) comps.add(c.name());
        } else if(args.length == 2 && args[0].equalsIgnoreCase("setkills")) {
            for(Player p : Bukkit.getOnlinePlayers()) comps.add(p.getName());
        } else if(args.length == 3 && args[0].equalsIgnoreCase("setkills")) {
            for(int i = 0; i <= 5; i++) comps.add(String.valueOf(i));
        } else if(args.length == 2 && args[0].equalsIgnoreCase("event")) {
            comps.add("start"); comps.add("stop");
        } else if(args.length == 3 && args[0].equalsIgnoreCase("event")) {
            comps.add("DOUBLE_KILLS"); comps.add("BOUNTY_RUSH"); comps.add("SPECIAL_DROPS");
        } else if(args.length == 2 && args[0].equalsIgnoreCase("give")) {
            comps.add("killshard"); comps.add("rerollbook");
        } else if(args.length == 3 && args[0].equalsIgnoreCase("give")) {
            for(Player p : Bukkit.getOnlinePlayers()) comps.add(p.getName());
        } else if(args.length == 4 && args[0].equalsIgnoreCase("give")) {
            comps.add("1"); comps.add("64");
        }
        return comps;
    }
}
