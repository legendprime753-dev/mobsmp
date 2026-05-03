package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.Bounty;
import org.bukkit.entity.Player;

import java.util.*;

public class BountyManager {
    private final MobSMP plugin;
    private final Map<UUID, Bounty> bounties = new HashMap<>();
    public BountyManager(MobSMP plugin){this.plugin=plugin;}
    public boolean addBounty(Player requester, Player target){
        return bounties.computeIfAbsent(target.getUniqueId(), Bounty::new).addRequester(requester.getUniqueId());
    }
    public boolean hasBounty(UUID target){ return bounties.containsKey(target); }
    public void handleBountyKill(Player killer, Player target){ if(hasBounty(target.getUniqueId())) { plugin.getKillManager().addKill(killer, 2); bounties.remove(target.getUniqueId()); } }
}
