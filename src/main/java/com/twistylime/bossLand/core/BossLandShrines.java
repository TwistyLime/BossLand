package com.twistylime.bossLand.core;

import com.twistylime.bossLand.config.BossLandConfiguration;
import com.twistylime.bossLand.utility.CompatibilityResolver;
import com.twistylime.bossLand.utility.MCUtility;
import org.bukkit.*;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class BossLandShrines {
    private final JavaPlugin plugin;
    private final BossLandConfiguration config;
    private final BossLandItems itemManager;
    private final BossLandBosses bossManager;
    private final ConfigurationSection recipesSection;

    public BossLandShrines(JavaPlugin plugin, BossLandConfiguration config, BossLandItems itemManager, BossLandBosses bossManager, ConfigurationSection recipesSection) {
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;
        this.bossManager = bossManager;
        this.recipesSection = recipesSection;
    }

    public Map<String, Object> getShrineRecipe(String bossName){
        if (recipesSection == null) {
            plugin.getLogger().warning("No shrine_recipes section found in config!");
            return null;
        }

        ConfigurationSection requiredBossShrineRecipe = (ConfigurationSection) recipesSection.get(bossName);
        if (requiredBossShrineRecipe == null) {
            plugin.getLogger().warning("No shrine recipe for boss found in config!");
            return null;
        }

        Map<String, Object> recipeMap = new HashMap<>();

        // ------------------- RESULT -------------------
        ConfigurationSection resultSec = requiredBossShrineRecipe.getConfigurationSection("result");
        ItemStack resultItem = null;
        if (resultSec != null) {
            String item = resultSec.getString("item", "STONE");
            int amount = resultSec.getInt("amount", 1);
            String displayName = resultSec.getString("display_name", null);
            List<String> lore = resultSec.getStringList("lore");
            Material material = Material.getMaterial(item);
            resultItem = new ItemStack(material, amount);
            ItemMeta meta = resultItem.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(displayName);
                meta.setLore(lore);
                meta.addEnchant(Enchantment.MENDING,1,true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                resultItem.setItemMeta(meta);
            }
        }

        if(bossName.contains("god") || bossName.contains("devil") || bossName.contains("death")){
            recipeMap.put("result", resultItem);

            // ------------------- ACTIVATOR -------------------
            ConfigurationSection activatorSec = requiredBossShrineRecipe.getConfigurationSection("activator");
            if (activatorSec != null) {
                String item = activatorSec.getString("item", "STONE");
                int amount = activatorSec.getInt("amount", 1);

                ItemStack activatorItem = getMentionedItem(item, amount);

                recipeMap.put("recipe", activatorItem);
            }

            // ------------------- Instructor -------------------
            ConfigurationSection instructorSec = requiredBossShrineRecipe.getConfigurationSection("instructor");
            if (instructorSec != null) {
                String item = instructorSec.getString("item", "STONE");
                int amount = instructorSec.getInt("amount", 1);
                List<String> lore = instructorSec.getStringList("lore");

                ItemStack instructorItem = getMentionedItem(item, amount);
                ItemMeta meta = instructorItem.getItemMeta();
                if(meta != null){
                    meta.setLore(lore);
                    meta.setDisplayName("§6§lInstructions");
                    instructorItem.setItemMeta(meta);
                }

                recipeMap.put("instructions", instructorItem);
            }
        }
        else{
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("item", resultItem);

            recipeMap.put("result", resultMap);

            // ------------------- RECIPE (SHAPE + INGREDIENTS) -------------------
            Map<String, Object> recipeDetails = new HashMap<>();

            // Shape
            List<String> shapeList = requiredBossShrineRecipe.getStringList("shape");
            List<List<String>> shapedGrid = new ArrayList<>();
            for (String row : shapeList) {
                List<String> rowChars = new ArrayList<>();
                for (char c : row.toCharArray()) {
                    rowChars.add(String.valueOf(c).toLowerCase()); // normalize to lowercase
                }
                shapedGrid.add(rowChars);
            }
            recipeDetails.put("shape", shapedGrid);

            // Ingredients
            Map<String, ItemStack> ingredientItems = new HashMap<>();
            ConfigurationSection ingSec = requiredBossShrineRecipe.getConfigurationSection("ingredients");
            if (ingSec != null) {
                for (String key : ingSec.getKeys(false)) {
                    String mat = ingSec.getString(key,"STONE");
                    ItemStack ingItem = getMentionedItem(mat, 1); // use get item from bosslanditems class
                    ingredientItems.put(key.toLowerCase(), ingItem);
                }
            }
            recipeDetails.put("items", ingredientItems);

            recipeMap.put("recipe", recipeDetails);


            // ------------------- ACTIVATOR -------------------
            ConfigurationSection activatorSec = requiredBossShrineRecipe.getConfigurationSection("activator");
            if (activatorSec != null) {
                String item = activatorSec.getString("item", "STONE");
                int amount = activatorSec.getInt("amount", 1);

                ItemStack activatorItem = getMentionedItem(item, amount);

                Map<String, Object> activatorMap = new HashMap<>();
                activatorMap.put("item", activatorItem);

                recipeMap.put("activator", activatorMap);
            }
        }

        // ------------------- LOOT -------------------
        List<String> lootIds = requiredBossShrineRecipe.getStringList("loot");
        String lootName = requiredBossShrineRecipe.getString("lootName");

        List<ItemStack> loot = new ArrayList<>();
        for(String id: lootIds){
            ItemStack lootItem = itemManager.getItem(lootName,id);
            loot.add(lootItem);
        }
        recipeMap.put("loot",loot);

        return recipeMap;
    }

    public ItemStack getMentionedItem(String name, int amount) {
        return switch (name) {
            case "BELL_OF_DOOM" -> itemManager.getIllagerItem();
            case "BOOK_OF_SPELLS" -> itemManager.getWizardItem();
            case "POTION_OF_GIANT_GROWTH" -> itemManager.getGiantIem();
            case "FORBIDDEN_FRUIT" -> itemManager.getGodItem();
            case "ABHORRENT_FRUIT" -> itemManager.getDevilItem();
            case "DEATH_NOTE" -> itemManager.getDeathItem();
            case "FIRE" -> representFireItem();
            default -> new ItemStack(CompatibilityResolver.resolveMaterial(name), amount);
        };
    }

    private ItemStack representFireItem(){
        ItemStack fireItem = new ItemStack(Material.FLINT_AND_STEEL,1);
        ItemMeta meta = fireItem.getItemMeta();
        if(meta!= null){
            meta.addEnchant(Enchantment.MENDING,1,true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.setDisplayName(ChatColor.GOLD+"Lit Fire");

            List<String> lore = new ArrayList<>();
            lore.add(ChatColor.GRAY + "Represents fire in the shrine recipe.");
            meta.setLore(lore);

            fireItem.setItemMeta(meta);
        }

        return fireItem;
    }

    public void detectShrineRecipe(PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        try {
            assert e.getClickedBlock() != null;
            Location l = e.getClickedBlock().getLocation();
            String biome = Objects.requireNonNull(l.getWorld()).getBiome((int) l.getX(), (int) l.getY(), (int) l.getZ()).toString();
            // Boss Rituals
            if (p.getInventory().getItemInMainHand().getType().equals(Material.ENDER_EYE)
                    && Objects.requireNonNull(l.getWorld()).getEnvironment().equals(World.Environment.NORMAL) && biome.contains("SWAMP")) {
                if (checkBlockRecipe(l, "SLIME_BLOCK:SLIME_BLOCK:SLIME_BLOCK", "SLIME_BLOCK:DIAMOND_BLOCK:SLIME_BLOCK",
                        "SLIME_BLOCK:SLIME_BLOCK:SLIME_BLOCK", true)) {
                    e.setCancelled(true);
                    l.getWorld().createExplosion(l, 3, false);
                    l.setY(l.getY() + 2);
                    final Location bs = l.clone();
                    MCUtility.takeItem(p, 1);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> bossManager.spawnBoss(p, bs, "KingSlime"),
                            (40));
                }
            } else if (p.getInventory().getItemInMainHand().getType().equals(Material.MAGMA_CREAM)
                    && Objects.requireNonNull(l.getWorld()).getEnvironment().equals(World.Environment.NETHER)) {
                if (checkBlockRecipe(l, "FIRE:FIRE:FIRE", "FIRE:WITHER_SKELETON_SKULL:FIRE", "FIRE:FIRE:FIRE", true)) {
                    e.setCancelled(true);
                    l.getWorld().createExplosion(l, 2, true);
                    final Location bs = l.clone();
                    MCUtility.takeItem(p, 1);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
                            () -> bossManager.spawnBoss(p, bs, "WitherSkeletonKing"), (20));
                }
            } else if (p.getInventory().getItemInMainHand().getType().equals(Material.CAKE)
                    && Objects.requireNonNull(l.getWorld()).getEnvironment().equals(World.Environment.NORMAL) && (biome.contains("BAMBOO"))) {
                if (checkBlockRecipe(l, "MOSSY_COBBLESTONE:MOSSY_COBBLESTONE:MOSSY_COBBLESTONE",
                        "MOSSY_COBBLESTONE:CHISELED_STONE_BRICKS:MOSSY_COBBLESTONE",
                        "MOSSY_COBBLESTONE:MOSSY_COBBLESTONE:MOSSY_COBBLESTONE", true)) {
                    // BAMBOO
                    e.setCancelled(true);
                    l.getWorld().createExplosion(l, 2, false);
                    final Location bs = l.clone();
                    MCUtility.takeItem(p, 1);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> bossManager.spawnBoss(p, bs, "PapaPanda"),
                            (20));
                }
            } else if (p.getInventory().getItemInMainHand().getType().equals(Material.JACK_O_LANTERN)
                    && Objects.requireNonNull(l.getWorld()).getEnvironment().equals(World.Environment.NORMAL) && (l.getY() <= -40)) {
                if (checkBlockRecipe(l, "COPPER_BLOCK:COPPER_BLOCK:COPPER_BLOCK",
                        "COPPER_BLOCK:IRON_BLOCK:COPPER_BLOCK", "COPPER_BLOCK:COPPER_BLOCK:COPPER_BLOCK", true)) {
                    // COPPER GOLEM
                    e.setCancelled(true);
                    // l.getWorld().createExplosion(l, 3, false);
                    l.getWorld().playSound(l, Sound.ENTITY_IRON_GOLEM_REPAIR, 1, 1);
                    final Location bs = l.clone();
                    MCUtility.takeItem(p, 1);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> bossManager.spawnBoss(p, bs, "Anger"),
                            (20));
                }
            } else if (p.getInventory().getItemInMainHand().getType().equals(Material.BRAIN_CORAL)
                    && Objects.requireNonNull(l.getWorld()).getEnvironment().equals(World.Environment.NORMAL) && (biome.contains("PLAINS"))) {
                if (checkBlockRecipe(l, "SOUL_SAND:SOUL_SAND:SOUL_SAND", "SOUL_SAND:EMERALD_BLOCK:SOUL_SAND",
                        "SOUL_SAND:SOUL_SAND:SOUL_SAND", true)) {
                    e.setCancelled(true);
                    l.getWorld().createExplosion(l, 2, false);
                    final Location bs = l.clone();
                    MCUtility.takeItem(p, 1);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,() -> bossManager.spawnBoss(p, bs, "ZombieKing"), (20));
                }
            } else if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getDisplayName().equals("§5§lBook of Spells")
                    && Objects.requireNonNull(l.getWorld()).getEnvironment().equals(World.Environment.NORMAL) && (biome.contains("SNOWY") || biome.contains("FROZEN") || biome.contains("JAGGED") || biome.contains("GROVE") || biome.contains("ICE"))) {
                if (checkBlockRecipe(l, "REDSTONE_TORCH:REDSTONE_WIRE:REDSTONE_TORCH",
                        "REDSTONE_WIRE:CAMPFIRE:REDSTONE_WIRE", "REDSTONE_TORCH:REDSTONE_WIRE:REDSTONE_TORCH", true)) {
                    e.setCancelled(true);
                    MCUtility.lightningShow(plugin,l, 2);
                    MCUtility.takeItem(p, 1);
                    final Location bs = l.clone();
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,() -> bossManager.spawnBoss(p, bs, "EvilWizard"), (20));
                }
            } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§6§lBell of Doom")
                    && Objects.requireNonNull(l.getWorld()).getEnvironment().equals(World.Environment.NORMAL) && (biome.contains("SAVANNA"))) {
                if (checkBlockRecipe(l, "REDSTONE_TORCH:REDSTONE_WIRE:REDSTONE_TORCH",
                        "REDSTONE_WIRE:CAMPFIRE:REDSTONE_WIRE", "REDSTONE_TORCH:REDSTONE_WIRE:REDSTONE_TORCH", true)) {
                    e.setCancelled(true);
                    MCUtility.lightningShow(plugin,l, 3);
                    MCUtility.takeItem(p, 1);
                    final Location bs = l.clone();
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> bossManager.spawnBoss(p, bs, "IllagerKing"), (20));
                }
            } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName()
                    .equals("§2§lPotion of Giant Growth") && Objects.requireNonNull(l.getWorld()).getEnvironment().equals(World.Environment.NORMAL)
                    && (l.getWorld().getBiome((int) l.getX(), (int) l.getY(), (int) l.getZ()).toString()
                    .contains("PLAINS"))) {
                if (checkBlockRecipe(l, "REDSTONE_TORCH:REDSTONE_WIRE:REDSTONE_TORCH",
                        "REDSTONE_WIRE:CAMPFIRE:REDSTONE_WIRE", "REDSTONE_TORCH:REDSTONE_WIRE:REDSTONE_TORCH", true)) {
                    e.setCancelled(true);
                    // boom(l,5,false);
                    MCUtility.lightningShow(plugin,l, 5);
                    MCUtility.takeItem(p, 1);
                    final Location bs = l.clone();
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> bossManager.spawnBoss(p, bs, "Giant"),
                            (20));
                }
            } else if (p.getInventory().getItemInMainHand().getType().equals(Material.NETHER_STAR)) {
                if (checkBlockRecipe(l, "REDSTONE_TORCH:REDSTONE_WIRE:REDSTONE_TORCH",
                        "REDSTONE_WIRE:CAMPFIRE:REDSTONE_WIRE", "REDSTONE_TORCH:REDSTONE_WIRE:REDSTONE_TORCH", true)) {
                    e.setCancelled(true);
                    if (config.godsDead()) {
                        MCUtility.takeItem(p, 1);
                        p.getWorld().createExplosion(e.getClickedBlock().getLocation(), 4);
                        final Location bs = l.clone();
                        p.sendMessage("§c§lFool, who dares summon me!");
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> bossManager.spawnBoss(p, bs, "Demon"),
                                (10));
                    } else
                        p.sendMessage(config.getLang("noPower"));
                }
            } else if (p.getInventory().getItemInMainHand().getType().equals(Material.GOLD_INGOT)
                    && p.getInventory().getItemInMainHand().getAmount() >= 16) {
                // Ghast Spawn
                // l.setY(l.getY()+1);
                if (Objects.requireNonNull(l.getWorld()).getEnvironment().equals(World.Environment.NETHER) && checkBlockRecipe(l,
                        "REDSTONE_WIRE:REDSTONE_WIRE:REDSTONE_WIRE", "REDSTONE_WIRE:MAGMA_BLOCK:REDSTONE_WIRE",
                        "REDSTONE_WIRE:REDSTONE_WIRE:REDSTONE_WIRE", false)) {
                    if (!l.getBlock().getType().equals(Material.BEDROCK))
                        l.getBlock().setType(Material.AIR);
                    MCUtility.boom(plugin, l, 5, true);
                    e.setCancelled(true);
                    MCUtility.takeItem(p, 16);
                    final Location bs = l.clone();
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> bossManager.spawnBoss(p, bs, "GhastLord"),
                            (40));
                }
            } else if (p.getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_CARROT)
                    && p.getInventory().getItemInMainHand().getAmount() >= 16) {
                // this.getLogger().log(Level.WARNING, "Gold Consumed!");
                // l.setY(l.getY()+1);
                if (Objects.requireNonNull(l.getWorld()).getBiome((int) l.getX(), (int) l.getY(), (int) l.getZ()).toString().contains("DESERT"))
                    if (l.getWorld().getEnvironment().equals(World.Environment.NORMAL)
                            && checkBlockRecipe(l, "CARROTS:CARROTS:CARROTS", "CARROTS:MAGMA_BLOCK:CARROTS",
                            "CARROTS:CARROTS:CARROTS", false)) {
                        // this.getLogger().log(Level.WARNING, "Correct condition found for Killer
                        // Bunny!");
                        e.setCancelled(true);
                        MCUtility.takeItem(p, 16);
                        // boom(l,2,false);
                        // Location la = l.clone();
                        // la.setY(la.getY()-1);
                        if (!l.getBlock().getType().equals(Material.BEDROCK))
                            l.getBlock().setType(Material.AIR);
                        l.getWorld().strikeLightning(l);
                        final Location bs = l.clone();
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin,() -> bossManager.spawnBoss(p, bs, "KillerBunny"), (40));
                    }
            }
            // System.out.println("Is Shard: " +
            // isShard(p.getInventory().getItemInMainHand()));
        } catch (Exception x) {
        }
    }

    private boolean checkBlockRecipe(Location l, String line1, String line2, String line3, boolean remove) {
        // String[] s1 = line1.split(":");
        // String[] s2 = line2.split(":");
        // String[] s3 = line3.split(":");
        // Row One
        Location l1 = l.clone();
        l1.setX(l1.getX() + 1);
        l1.setZ(l1.getZ() + 1);
        Location l2 = l.clone();
        l2.setX(l2.getX() + 1);
        Location l3 = l.clone();
        l3.setX(l3.getX() + 1);
        l3.setZ(l3.getZ() - 1);
        // System.out.println(l1.getBlock().getType() + ":" + l2.getBlock().getType() +
        // ":" + l3.getBlock().getType());
        // l1.getBlock().setType(Material.DIRT); l2.getBlock().setType(Material.DIRT);
        // l3.getBlock().setType(Material.DIRT);
        // if((l1.getBlock().getType().toString().equals(s1[0])) &&
        // (l2.getBlock().getType().toString().equals(s1[1])) &&
        // (l3.getBlock().getType().toString().equals(s1[2]))) {
        if (line1.equals(l1.getBlock().getType() + ":" + l2.getBlock().getType() + ":" + l3.getBlock().getType())) {
            // Row Two
            Location l4 = l.clone();
            l4.setZ(l4.getZ() + 1);
            Location l5 = l.clone();
            Location l6 = l.clone();
            l6.setZ(l6.getZ() - 1);
            // System.out.println(l4.getBlock().getType() + ":" + l5.getBlock().getType() +
            // ":" + l6.getBlock().getType());
            // if((l4.getBlock().getType().toString().equals(s2[0])) &&
            // (l5.getBlock().getType().toString().equals(s2[1])) &&
            // (l6.getBlock().getType().toString().equals(s2[2]))) {
            if (line2.equals(l4.getBlock().getType() + ":" + l5.getBlock().getType() + ":" + l6.getBlock().getType())) {
                // Row Three
                Location l7 = l.clone();
                l7.setX(l7.getX() - 1);
                l7.setZ(l7.getZ() + 1);
                Location l8 = l.clone();
                l8.setX(l8.getX() - 1);
                Location l9 = l.clone();
                l9.setX(l9.getX() - 1);
                l9.setZ(l9.getZ() - 1);
                // System.out.println(l7.getBlock().getType() + ":" + l8.getBlock().getType() +
                // ":" + l9.getBlock().getType());
                // if((l7.getBlock().getType().toString().equals(s3[0])) &&
                // (l8.getBlock().getType().toString().equals(s3[1])) &&
                // (l9.getBlock().getType().toString().equals(s3[2]))) {
                if (line3.equals(
                        l7.getBlock().getType() + ":" + l8.getBlock().getType() + ":" + l9.getBlock().getType())) {
                    // Recipe Correct
                    // System.out.println("Recipe Correct");
                    if (remove)
                        for (Location bl : Arrays.asList(l1, l2, l3, l4, l5, l6, l7, l8, l9))
                            if (!bl.getBlock().getType().equals(Material.BEDROCK))
                                bl.getBlock().setType(Material.AIR);
                    return true;
                }
            }
        }
        return false;
    }
}
