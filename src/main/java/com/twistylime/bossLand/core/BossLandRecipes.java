package com.twistylime.bossLand.core;

import com.twistylime.bossLand.config.BossLandConfiguration;
import com.twistylime.bossLand.utility.CompatibilityResolver;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getServer;

public class BossLandRecipes {

    private final JavaPlugin plugin;
    private final BossLandConfiguration config;
    private final BossLandItems itemManager;

    public BossLandRecipes(JavaPlugin plugin, BossLandConfiguration config, BossLandItems itemManager) {
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;
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
            if(resultItemName == null) return;
            ItemStack resultItem = getResultItem(resultItemName);
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
                    if(materialName == null) return;
                    if(materialName.equals("AIR")) continue;
                    Material mat = CompatibilityResolver.resolveMaterial(materialName);
                    shapedRecipe.setIngredient(charKey.charAt(0), mat);
                }
            }

            // ===== Register Recipe =====
            Bukkit.addRecipe(shapedRecipe);
        }
    }

    public void restrictCraftingForBossLandItems(CraftingInventory ci) {
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
                } else if (craftingResultMeta.getDisplayName().contains(config.getLang("items.spellbook"))) {
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
            plugin.getLogger().log(Level.WARNING, "Error while restricting crafting of items using normal dyes instead of shards.");
        }
    }

    public void disableCrafterRecipes() {
        try {
            Class<?> crafterCraftEventClass = Class.forName("org.bukkit.event.block.CrafterCraftEvent");

            @SuppressWarnings("unchecked")
            Class<? extends Event> eventClass = (Class<? extends Event>) crafterCraftEventClass;

            Listener dummyListener = new Listener() {};

            EventExecutor executor = (listener, event) -> {
                try {
                    Method getBlockMethod = event.getClass().getMethod("getBlock");
                    Method getRecipeMethod = event.getClass().getMethod("getRecipe");
                    Method getResultMethod = event.getClass().getMethod("getResult");
                    Method setCancelledMethod = event.getClass().getMethod("setCancelled", boolean.class);

                    Object block = getBlockMethod.invoke(event);
                    Object recipe = getRecipeMethod.invoke(event);
                    ItemStack result = (ItemStack) getResultMethod.invoke(event);

                    if (recipe != null && result != null) {
                        Method getStateMethod = block.getClass().getMethod("getState");
                        Object blockState = getStateMethod.invoke(block);
                        Method getInventoryMethod = blockState.getClass().getMethod("getInventory");
                        Object inventory = getInventoryMethod.invoke(blockState);

                        if (result.getItemMeta() != null) {
                            validateCrafterRecipe((Inventory) inventory, result, setCancelledMethod, event);
                        }
                    }
                } catch (Exception e) {
                    plugin.getLogger().log(Level.WARNING, "Error in crafter event handler: " + e.getMessage());
                }
            };

            getServer().getPluginManager().registerEvent(
                    eventClass,
                    dummyListener,
                    EventPriority.NORMAL,
                    executor,
                    plugin
            );

        } catch (ClassNotFoundException e) {
            plugin.getLogger().info("Crafter events not supported in this version");
        }
    }

    private void validateCrafterRecipe(Inventory inv, ItemStack result, Method setCancelledMethod, Object event) throws Exception {
        ItemMeta craftingResultMeta = result.getItemMeta();
        String displayName = craftingResultMeta.getDisplayName();

        if (displayName.contains(config.getLang("items.bell"))) {
            // Bell of Doom
            if (isInvalidShard(inv.getItem(3), config.getLang("items.whiteshard")) ||
                    isInvalidShard(inv.getItem(4), config.getLang("items.greenshard")) ||
                    isInvalidShard(inv.getItem(5), config.getLang("items.greyshard"))) {
                setCancelledMethod.invoke(event, true);
            }
        } else if (displayName.contains(config.getLang("items.spellbook"))) {
            // Wizard Book
            if (isInvalidShard(inv.getItem(3), config.getLang("items.blackshard")) ||
                    isInvalidShard(inv.getItem(5), config.getLang("items.redshard"))) {
                setCancelledMethod.invoke(event, true);
            }
        } else if (displayName.contains(config.getLang("items.giantpotion"))) {
            // Giant Potion
            if (isInvalidShard(inv.getItem(3), config.getLang("items.greenshard")) ||
                    isInvalidShard(inv.getItem(4), config.getLang("items.redshard")) ||
                    isInvalidShard(inv.getItem(5), config.getLang("items.brownshard"))) {
                setCancelledMethod.invoke(event, true);
            }
        } else if (displayName.contains(config.getLang("items.elderegg"))) {
            // Dragon Egg
            if (isInvalidShard(inv.getItem(4), config.getLang("items.whiteshard")) ||
                    isInvalidShard(inv.getItem(7), config.getLang("items.blackshard"))) {
                setCancelledMethod.invoke(event, true);
            }
        } else if (displayName.contains(config.getLang("items.forbiddenfruit"))) {
            // Forbidden Fruit
            if (isInvalidShard(inv.getItem(6), config.getLang("items.emeraldshard")) ||
                    isInvalidShard(inv.getItem(7), config.getLang("items.goldshard")) ||
                    isInvalidShard(inv.getItem(8), config.getLang("items.blueshard"))) {
                setCancelledMethod.invoke(event, true);
            }
        } else if (displayName.contains(config.getLang("items.abhorrentfruit"))) {
            // Abhorrent Fruit
            if (isInvalidShard(inv.getItem(1), config.getLang("items.demonicshard")) ||
                    isInvalidShard(inv.getItem(3), config.getLang("items.demonicshard")) ||
                    isInvalidShard(inv.getItem(5), config.getLang("items.demonicshard")) ||
                    isInvalidShard(inv.getItem(7), config.getLang("items.demonicshard")) ||
                    isInvalidShard(inv.getItem(4), config.getLang("items.forbiddenfruit"))) {
                setCancelledMethod.invoke(event, true);
            }
        } else if (displayName.contains(config.getLang("items.deathnote"))) {
            // Death Note
            if (isInvalidShard(inv.getItem(4), config.getLang("items.knowledgebook"))) {
                setCancelledMethod.invoke(event, true);
            }
        }
    }

    private boolean isInvalidShard(ItemStack item, String expectedName) {
        if (item == null || item.getItemMeta() == null) return true;
        String displayName = item.getItemMeta().getDisplayName();
        return !displayName.equals(expectedName);
    }

    private ItemStack getResultItem(String name) {
        return switch (name) {
            case "DEATH_NOTE" -> itemManager.getDeathItem();
            case "FORBIDDEN_FRUIT" -> itemManager.getGodItem();
            case "ABHORRENT_FRUIT" -> itemManager.getDevilItem();
            case "BELL_OF_DOOM" -> itemManager.getIllagerItem();
            case "BOOK_OF_SPELLS" -> itemManager.getWizardItem();
            case "POTION_OF_GIANT_GROWTH" -> itemManager.getGiantIem();
            case "ELDER_EGG" -> itemManager.getDragonItem();
            default -> new ItemStack(CompatibilityResolver.resolveMaterial(name), 1);
        };
    }
}