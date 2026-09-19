package de.priyme2.mobsmp.recipes;

import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

public class RerollRecipe implements Listener {
    
    private static MobSMP pluginRef;
    private static NamespacedKey recipeKey;
    
    public static void register(MobSMP plugin) {
        pluginRef = plugin;
        recipeKey = new NamespacedKey(plugin, "reroll_book");
        ShapedRecipe recipe = new ShapedRecipe(recipeKey, plugin.getRerollManager().createRerollBook());
        
        recipe.shape("BDB", "NFN", "BDB");
        recipe.setIngredient('B', Material.BOOK);
        recipe.setIngredient('D', Material.DIAMOND_BLOCK);
        recipe.setIngredient('N', Material.NETHERITE_INGOT);
        recipe.setIngredient('F', Material.AMETHYST_SHARD); // Accept any amethyst shard in grid
        Bukkit.addRecipe(recipe);
        
        // Register listener to validate the center slot is a Kill Fragment
        Bukkit.getPluginManager().registerEvents(new RerollRecipe(), plugin);
    }
    
    @EventHandler
    public void onPrepareCraft(PrepareItemCraftEvent e) {
        if(e.getRecipe() == null) return;
        if(!(e.getRecipe() instanceof ShapedRecipe shaped)) return;
        if(!shaped.getKey().equals(recipeKey)) return;
        
        CraftingInventory inv = e.getInventory();
        // Slot 5 is the center slot in a 3x3 grid (slots 1-9, index 0 is result)
        ItemStack center = inv.getMatrix()[4]; // 0-indexed: row1(0,1,2) row2(3,4,5) row3(6,7,8)
        
        if(center == null || !pluginRef.getRerollManager().isFragment(center)) {
            // Center is not a Kill Fragment - block the craft
            inv.setResult(null);
        }
    }
}
