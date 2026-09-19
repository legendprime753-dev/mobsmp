package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.Bounty;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BountyManager {
    private final MobSMP plugin;
    private final Map<UUID, Bounty> bounties = new HashMap<>();
    private File bountyFile;

    public BountyManager(MobSMP plugin) {
        this.plugin = plugin;
        bountyFile = new File(plugin.getDataFolder(), "bounties.yml");
        load();
    }

    private void load() {
        if (!bountyFile.exists()) return;
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(bountyFile);
        if (cfg.getConfigurationSection("bounties") == null) return;
        for (String key : cfg.getConfigurationSection("bounties").getKeys(false)) {
            UUID targetUUID = UUID.fromString(key);
            Bounty bounty = new Bounty(targetUUID);
            List<String> contributors = cfg.getStringList("bounties." + key + ".contributors");
            for (String c : contributors) bounty.addContributor(UUID.fromString(c));
            bounties.put(targetUUID, bounty);
        }
    }

    public void save() {
        FileConfiguration cfg = new YamlConfiguration();
        for (Map.Entry<UUID, Bounty> entry : bounties.entrySet()) {
            String key = "bounties." + entry.getKey();
            List<String> contributors = new ArrayList<>();
            for (UUID uuid : entry.getValue().getContributors()) contributors.add(uuid.toString());
            cfg.set(key + ".contributors", contributors);
        }
        try {
            cfg.save(bountyFile);
        } catch (IOException e) {
            plugin.getLogger().warning("Could not save bounties!");
        }
    }

    public boolean setBounty(Player setter, Player target) {
        int xpCost = plugin.getConfig().getInt("bounty-xp-cost", 5);
        if (setter.getLevel() < xpCost) {
            setter.sendMessage(ChatColor.RED + "You need " + xpCost + " XP levels to set a bounty!");
            return false;
        }
        if (setter.equals(target)) {
            setter.sendMessage(ChatColor.RED + "You cannot set a bounty on yourself!");
            return false;
        }
        Bounty bounty = bounties.computeIfAbsent(target.getUniqueId(), Bounty::new);
        if (bounty.getContributorCount() >= 4) {
            setter.sendMessage(ChatColor.RED + "This bounty already has the maximum number of contributors!");
            return false;
        }
        if (bounty.hasContributor(setter.getUniqueId())) {
            setter.sendMessage(ChatColor.RED + "You have already contributed to this bounty!");
            return false;
        }
        setter.setLevel(setter.getLevel() - xpCost);
        bounty.addContributor(setter.getUniqueId());
        save();
        setter.sendMessage(ChatColor.GOLD + "Bounty set on " + target.getName() + "!");
        target.sendMessage(ChatColor.RED + "A bounty has been placed on you by " + setter.getName() + "!");
        return true;
    }

    public boolean hasBounty(UUID target) {
        return bounties.containsKey(target);
    }

    public Bounty getBounty(UUID target) {
        return bounties.get(target);
    }

    public void onBountyKilled(Player killer, Player target) {
        Bounty bounty = bounties.remove(target.getUniqueId());
        if (bounty == null) return;

        // Killer gets 2 kills
        plugin.getKillManager().addKill(killer.getUniqueId(), 2);
        killer.sendMessage(ChatColor.GOLD + "Bounty collected! +2 kills!");

        // Distribute hotbar items to online contributors
        List<UUID> contributors = bounty.getContributors();
        if (contributors.isEmpty()) {
            save();
            return;
        }

        ItemStack[] hotbar = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            hotbar[i] = target.getInventory().getItem(i);
        }
        ItemStack[] armor = target.getInventory().getArmorContents();

        List<Player> onlineContributors = new ArrayList<>();
        for (UUID uuid : contributors) {
            Player p = Bukkit.getPlayer(uuid);
            if (p != null && p.isOnline()) onlineContributors.add(p);
        }

        if (!onlineContributors.isEmpty()) {
            // Give items to contributors round-robin
            int idx = 0;
            for (ItemStack item : hotbar) {
                if (item != null && item.getType() != org.bukkit.Material.AIR) {
                    Player recipient = onlineContributors.get(idx % onlineContributors.size());
                    giveRewardItem(recipient, item.clone());
                    recipient.sendMessage(ChatColor.GOLD + "Bounty reward: received item from " + target.getName() + "'s hotbar/armor!");
                    idx++;
                }
            }
            for (ItemStack item : armor) {
                if (item != null && item.getType() != org.bukkit.Material.AIR) {
                    Player recipient = onlineContributors.get(idx % onlineContributors.size());
                    giveRewardItem(recipient, item.clone());
                    recipient.sendMessage(ChatColor.GOLD + "Bounty reward: received armor from " + target.getName() + "!");
                    idx++;
                }
            }
        }

        Bukkit.broadcastMessage(ChatColor.GOLD + "[Bounty] " + target.getName() + " has been killed and their bounty collected by " + killer.getName() + "!");
        save();
    }

    public Map<UUID, Bounty> getAllBounties() {
        return Collections.unmodifiableMap(bounties);
    }

    private void giveRewardItem(Player recipient, ItemStack item) {
        Map<Integer, ItemStack> invOverflow = recipient.getInventory().addItem(item.clone());
        if (invOverflow.isEmpty()) return;

        for (ItemStack overflowItem : invOverflow.values()) {
            Map<Integer, ItemStack> ecOverflow = recipient.getEnderChest().addItem(overflowItem);
            for (ItemStack stillOverflow : ecOverflow.values()) {
                recipient.getWorld().dropItemNaturally(recipient.getLocation(), stillOverflow);
            }
        }
    }
}
