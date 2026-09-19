package de.priyme2.mobsmp.managers;

import de.priyme2.mobsmp.MobSMP;
import de.priyme2.mobsmp.models.MobClass;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public class RerollManager {
    private final MobSMP plugin;
    public static final String REROLL_GUI_TITLE = "Choose Your Mob Class";

    public RerollManager(MobSMP plugin) {
        this.plugin = plugin;
        registerRecipes();
    }

    private void registerRecipes() {
        // Reroll Book recipe
        ItemStack mobSoul = createMobSoulItem();
        ItemStack rerollBook = createRerollBookItem();
        NamespacedKey key = new NamespacedKey(plugin, "reroll_book");
        ShapedRecipe recipe = new ShapedRecipe(key, rerollBook);
        recipe.shape("S S", " B ", "S S");
        recipe.setIngredient('S', new RecipeChoice.ExactChoice(mobSoul));
        recipe.setIngredient('B', Material.BOOK);
        Bukkit.addRecipe(recipe);

        // Golden Apple recipe
        if (plugin.getConfig().getBoolean("golden-apple-recipe", true)) {
            Bukkit.removeRecipe(NamespacedKey.minecraft("golden_apple"));
            NamespacedKey gaKey = new NamespacedKey(plugin, "golden_apple_custom");
            ItemStack goldenApple = new ItemStack(Material.GOLDEN_APPLE);
            ShapedRecipe gaRecipe = new ShapedRecipe(gaKey, goldenApple);
            gaRecipe.shape(" G ", "GAG", " G ");
            gaRecipe.setIngredient('G', Material.GOLD_INGOT);
            gaRecipe.setIngredient('A', Material.APPLE);
            Bukkit.addRecipe(gaRecipe);
        }
    }

    public ItemStack createMobSoulItem() {
        ItemStack item = new ItemStack(Material.NETHER_STAR);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.LIGHT_PURPLE + "Mob Soul");
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "mob_soul"),
                    PersistentDataType.BOOLEAN, true
            );
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack createRerollBookItem() {
        ItemStack item = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Reroll Book");
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "reroll_book"),
                    PersistentDataType.BOOLEAN, true
            );
            item.setItemMeta(meta);
        }
        return item;
    }

    public boolean isRerollBook(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(new NamespacedKey(plugin, "reroll_book"), PersistentDataType.BOOLEAN);
    }

    public boolean isMobSoul(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(new NamespacedKey(plugin, "mob_soul"), PersistentDataType.BOOLEAN);
    }

    public Inventory createRerollGUI() {
        Inventory inv = Bukkit.createInventory(null, 54, REROLL_GUI_TITLE);

        // Fill with gray glass panes
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta paneMeta = pane.getItemMeta();
        if (paneMeta != null) {
            paneMeta.setDisplayName(" ");
            pane.setItemMeta(paneMeta);
        }
        for (int i = 0; i < 54; i++) inv.setItem(i, pane.clone());

        // Place class icons
        MobClass[] classes = MobClass.values();
        int[] slots = {10, 11, 12, 13, 14, 19, 20, 21, 22, 23};
        for (int i = 0; i < Math.min(classes.length, slots.length); i++) {
            inv.setItem(slots[i], createClassItem(classes[i]));
        }
        return inv;
    }

    private ItemStack createClassItem(MobClass cls) {
        ItemStack item = new ItemStack(cls.getIcon());
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(cls.getColor() + cls.getDisplayName());
            java.util.List<String> lore = new java.util.ArrayList<>();
            lore.add(ChatColor.GRAY + "Passive: " + ChatColor.WHITE + cls.getPassiveName());
            lore.add(ChatColor.GRAY + "3k Ability: " + ChatColor.WHITE + cls.getAbility3kName());
            lore.add(ChatColor.GRAY + "5k Ability: " + ChatColor.WHITE + cls.getAbility5kName());
            lore.add(ChatColor.RED + "Weakness: " + cls.getWeaknessDesc());
            meta.setLore(lore);
            meta.getPersistentDataContainer().set(
                    new NamespacedKey(plugin, "reroll_class"),
                    PersistentDataType.STRING, cls.name()
            );
            item.setItemMeta(meta);
        }
        return item;
    }
}
