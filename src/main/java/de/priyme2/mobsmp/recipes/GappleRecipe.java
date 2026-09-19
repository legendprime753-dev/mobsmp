package de.priyme2.mobsmp.recipes;

import de.priyme2.mobsmp.MobSMP;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Iterator;
import java.util.List;

public class GappleRecipe {
    public static void register(MobSMP plugin) {
        NamespacedKey key = new NamespacedKey(plugin, "custom_gapple");
        Bukkit.removeRecipe(NamespacedKey.minecraft("golden_apple"));

        ShapedRecipe recipe = new ShapedRecipe(key, new ItemStack(Material.GOLDEN_APPLE));
        recipe.shape(" G ", "GAG", " G ");
        recipe.setIngredient('G', Material.GOLD_INGOT);
        recipe.setIngredient('A', Material.APPLE);
        Bukkit.addRecipe(recipe);
    }
}
