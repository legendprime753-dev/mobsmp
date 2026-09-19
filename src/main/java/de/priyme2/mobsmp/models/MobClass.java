package de.priyme2.mobsmp.models;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public enum MobClass {
    ZOMBIE("Zombie", ChatColor.GREEN, Material.ROTTEN_FLESH,
            "No Sunburn", "Zombie Horde", "Undead Regen", "Fire damage +50%"),
    SKELETON("Skeleton", ChatColor.WHITE, Material.BONE,
            "Sharp Arrows", "Bone Shield", "Arrow Rain", "Melee damage received +25%"),
    CREEPER("Creeper", ChatColor.GREEN, Material.GUNPOWDER,
            "Explosion Resistance", "Mini Explosion", "Super Charge", "Cats cause slowness nearby"),
    SPIDER("Spider", ChatColor.DARK_RED, Material.SPIDER_EYE,
            "Web Immunity", "Web Trap", "Venom Bite", "Sunlight: slowness I"),
    ENDERMAN("Enderman", ChatColor.DARK_PURPLE, Material.ENDER_PEARL,
            "Water Alert", "Teleport Assault", "Void Grasp", "Water/rain deals 1 damage/s"),
    BLAZE("Blaze", ChatColor.GOLD, Material.BLAZE_ROD,
            "Fire Immunity", "Fireball", "Blaze Storm", "Water/rain deals 2 damage/s"),
    WITCH("Witch", ChatColor.DARK_PURPLE, Material.POTION,
            "Potion Resistance", "Potion Splash", "Hex Curse", "Deal 25% less melee damage"),
    IRON_GOLEM("Iron Golem", ChatColor.GRAY, Material.IRON_INGOT,
            "Knockback Resistance", "Earthquake", "Iron Defense", "Movement speed -10%"),
    PHANTOM("Phantom", ChatColor.BLUE, Material.PHANTOM_MEMBRANE,
            "Fall Immunity", "Phantom Dive", "Night Terror", "Daytime: +20% all damage"),
    WITHER_SKELETON("Wither Skeleton", ChatColor.BLACK, Material.WITHER_SKELETON_SKULL,
            "Wither Resistance", "Wither Touch", "Soul Harvest", "Smite deals extra damage");

    private final String displayName;
    private final ChatColor color;
    private final Material icon;
    private final String passiveName;
    private final String ability3kName;
    private final String ability5kName;
    private final String weaknessDesc;

    MobClass(String displayName, ChatColor color, Material icon,
             String passiveName, String ability3kName, String ability5kName, String weaknessDesc) {
        this.displayName = displayName;
        this.color = color;
        this.icon = icon;
        this.passiveName = passiveName;
        this.ability3kName = ability3kName;
        this.ability5kName = ability5kName;
        this.weaknessDesc = weaknessDesc;
    }

    public String getDisplayName() { return displayName; }
    public ChatColor getColor() { return color; }
    public Material getIcon() { return icon; }
    public String getPassiveName() { return passiveName; }
    public String getAbility3kName() { return ability3kName; }
    public String getAbility5kName() { return ability5kName; }
    public String getWeaknessDesc() { return weaknessDesc; }
    public String getColoredName() { return color + displayName; }
}
