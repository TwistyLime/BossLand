package com.twistylime.bossLand.core;
import com.twistylime.bossLand.config.BossLandConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;

public class BossLandRecipes {

    private final JavaPlugin plugin;
    private final BossLandConfiguration config;

    public BossLandRecipes(JavaPlugin plugin, BossLandConfiguration config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void addRecipes(ConfigurationSection recipesSection) {
        if (recipesSection == null) {
            plugin.getLogger().warning("No item_recipes section found in config!");
            return;
        }

        for (String recipeName : recipesSection.getKeys(false)) {
            ConfigurationSection recipeData = recipesSection.getConfigurationSection(recipeName);
            if (recipeData == null) continue;

            // ===== Result =====
            String resultItemName = Objects.requireNonNull(recipeData.getConfigurationSection("result")).getString("item");
            int resultAmount = Objects.requireNonNull(recipeData.getConfigurationSection("result")).getInt("amount", 1);

            ItemStack resultItem = getItem(resultItemName);
            if (resultItem == null) {
                plugin.getLogger().warning("Could not find custom item: " + resultItemName);
                continue;
            }
            resultItem.setAmount(resultAmount);

            // ===== Shape =====
            java.util.List<String> shapeList = recipeData.getStringList("shape");
            if (shapeList.size() != 3) {
                plugin.getLogger().warning("Invalid shape for recipe: " + recipeName);
                continue;
            }
            String[] shape = shapeList.toArray(new String[0]);

            // ===== Create ShapedRecipe =====
            NamespacedKey key = new NamespacedKey(plugin, recipeName.toLowerCase());
            ShapedRecipe shapedRecipe = new ShapedRecipe(key, resultItem);
            shapedRecipe.shape(shape);

            // ===== Ingredients =====
            ConfigurationSection ingredientsSection = recipeData.getConfigurationSection("ingredients");
            if (ingredientsSection != null) {
                for (String charKey : ingredientsSection.getKeys(false)) {
                    String materialName = ingredientsSection.getString(charKey);
                    ItemStack ingredientItem = getItem(materialName);
                    if (ingredientItem == null) {
                        Material mat = Material.matchMaterial(materialName);
                        if (mat != null) {
                            shapedRecipe.setIngredient(charKey.charAt(0), mat);
                        } else {
                            plugin.getLogger().warning("Invalid ingredient: " + materialName + " in recipe " + recipeName);
                        }
                    } else {
                        Material mat = ingredientItem.getType();
                        shapedRecipe.setIngredient(charKey.charAt(0), mat);
                    }
                }
            }

            // ===== Register Recipe =====
            Bukkit.addRecipe(shapedRecipe);
            plugin.getLogger().info("Registered recipe: " + recipeName);
        }
    }

    public void restrictCraftingForBossLandItems(CraftingInventory ci){
        try {
            ItemStack craftingResult = ci.getResult();
            if (craftingResult != null && craftingResult.getItemMeta() != null) {
                ItemMeta craftingResultMeta = craftingResult.getItemMeta();
                if (craftingResultMeta.getDisplayName().contains(config.getLang("items.bell"))) {
                    // Bell of Doom
                    if (!ci.getItem(4).getItemMeta().getDisplayName().equals(config.getLang("items.whiteshard"))) {
                        ci.setResult(null);
                    } else if (!ci.getItem(5).getItemMeta().getDisplayName().equals(config.getLang("items.greenshard"))) {
                        ci.setResult(null);
                    } else if (!ci.getItem(6).getItemMeta().getDisplayName().equals(config.getLang("items.greyshard"))) {
                        ci.setResult(null);
                    }
                } else if (craftingResultMeta.getDisplayName().contains(config.getLang("items.spelbook"))) {
                    // Wizard Book
                    if (!ci.getItem(4).getItemMeta().getDisplayName().equals(config.getLang("items.blackshard")))
                        ci.setResult(null);
                    if (!ci.getItem(6).getItemMeta().getDisplayName().equals(config.getLang("items.redshard")))
                        ci.setResult(null);
                } else if (craftingResultMeta.getDisplayName().contains(config.getLang("items.giantpotion"))) {
                    // Giant Potion
                    if (!ci.getItem(4).getItemMeta().getDisplayName().equals(config.getLang("items.greenshard")))
                        ci.setResult(null);
                    if (!ci.getItem(5).getItemMeta().getDisplayName().equals(config.getLang("items.redshard")))
                        ci.setResult(null);
                    if (!ci.getItem(6).getItemMeta().getDisplayName().equals(config.getLang("items.brownshard")))
                        ci.setResult(null);
                } else if (craftingResultMeta.getDisplayName().contains(config.getLang("items.elderegg"))) {
                    // Dragon Egg
                    if (!ci.getItem(5).getItemMeta().getDisplayName().equals(config.getLang("items.whiteshard")))
                        ci.setResult(null);
                    if (!ci.getItem(8).getItemMeta().getDisplayName().equals(config.getLang("items.blackshard")))
                        ci.setResult(null);
                } else if (craftingResultMeta.getDisplayName().contains(config.getLang("items.forbiddenfruit"))) {
                    // Forbidden Fruit
                    if (!ci.getItem(7).getItemMeta().getDisplayName().equals(config.getLang("items.emeraldshard")))
                        ci.setResult(null);
                    if (!ci.getItem(8).getItemMeta().getDisplayName().equals(config.getLang("items.goldshard")))
                        ci.setResult(null);
                    if (!ci.getItem(9).getItemMeta().getDisplayName().equals(config.getLang("items.blueshard")))
                        ci.setResult(null);
                } else if (craftingResultMeta.getDisplayName().contains(config.getLang("items.abhorrentfruit"))) {
                    // Abhorrent Fruit
                    for (ItemStack s : Arrays.asList(ci.getItem(2), ci.getItem(4), ci.getItem(6), ci.getItem(8)))
                        if (!s.getItemMeta().getDisplayName().equals(config.getLang("items.demonicshard")))
                            ci.setResult(null);
                    if (!ci.getItem(5).getItemMeta().getDisplayName().equals(config.getLang("items.forbiddenfruit")))
                        ci.setResult(null);
                } else if (craftingResultMeta.getDisplayName().contains(config.getLang("items.deathnote"))) {
                    // Death Note
                    if (!ci.getItem(5).getItemMeta().getDisplayName().equals(config.getLang("items.knowledgebook"))) {
                        ci.setResult(null);
                    }
                }
            }
        } catch (Exception localException) {
            plugin.getLogger().log(Level.WARNING,"Error while restricting crafting of items using normal dyes instead of shards.");
        }
    }


    private ItemStack getItem(String name) {
        return new ItemStack(Material.BLACK_STAINED_GLASS_PANE,1); // Temporary function for now
    }
}
