package com.twistylime.bossLand.core;

import com.twistylime.bossLand.config.BossLandConfiguration;
import com.twistylime.bossLand.utility.CompatibilityResolver;
import com.twistylime.bossLand.utility.SkullCreator;
import com.twistylime.bossLand.utility.UtilityCalc;
import org.bukkit.*;
import org.bukkit.block.Banner;
import org.bukkit.block.banner.Pattern;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;

public class BossLandItems {
    private final JavaPlugin plugin;
    private final BossLandConfiguration config;

    public BossLandItems(JavaPlugin plugin, BossLandConfiguration config) {
        this.plugin = plugin;
        this.config = config;
    }

    public ItemStack getItem(Material mat, String name, int amount, List<String> loreList) {
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta m = item.getItemMeta();
        if (name != null)
            m.setDisplayName(name);
        if (loreList != null)
            m.setLore(loreList);
        item.setItemMeta(m);
        return item;
    }

    public ItemStack getItem(String bossType, String loot) {
        ItemStack s = plugin.getConfig().getItemStack("bosses." + bossType + ".loot." + loot);
        if (s == null)
            s = getItemOld(bossType, loot);
        return s;
    }

    public ItemStack getItemOld(String bossType, String loot) {
        try {
            String setItem = plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".item");

            String setAmountString = plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".amount");
            int setAmount;
            if (setAmountString != null) {
                setAmount = UtilityCalc.getIntFromString(setAmountString);
            } else
                setAmount = 1;
            assert setItem != null;
            ItemStack stack = new ItemStack(Material.valueOf(setItem.toUpperCase()), setAmount);
            // Texture
            if (stack.getType().equals(Material.PLAYER_HEAD)) {
                String tex = plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".texture");
                if (tex != null) {
                    stack = SkullCreator.getSkull(tex);
                }
            }
            // Get Name
            String name = null;
            if (plugin.getConfig().getList("bosses." + bossType + ".loot." + loot + ".name") != null) {
                // System.out.println("2");
                ArrayList<String> names = (ArrayList<String>) plugin.getConfig().getList("bosses." + bossType + ".loot." + loot + ".name");
                if (names != null) {
                    name = names.get(UtilityCalc.rand(1, names.size()) - 1);
                    name = prosessLootName(name, stack);
                }
            } else if (plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".name") != null) {
                // System.out.println("3");
                name = plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".name");
                name = prosessLootName(name, stack);
            }
            // Get Lore
            ArrayList<String> loreList = new ArrayList<String>();
            for (int i = 0; i <= 10; i++) {
                if (plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".lore" + i) != null) {
                    String lore = (plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".lore" + i));
                    lore = ChatColor.translateAlternateColorCodes('&', lore);
                    loreList.add(lore);
                    // System.out.println("5");
                }
            }
            // System.out.println("6");
            if (plugin.getConfig().getList("bosses." + bossType + ".loot." + loot + ".lore") != null) {
                // System.out.println("7");
                ArrayList<String> lb = (ArrayList<String>) plugin.getConfig()
                        .getList("bosses." + bossType + ".loot." + loot + ".lore");
                ArrayList<String> l = (ArrayList<String>) lb.clone();
                int min = l.size();
                if (plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".minLore") != null)
                    min = plugin.getConfig().getInt("bosses." + bossType + ".loot." + loot + ".minLore");
                int max = l.size();
                if (plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".maxLore") != null)
                    max = plugin.getConfig().getInt("bosses." + bossType + ".loot." + loot + ".maxLore");
                if (!l.isEmpty())
                    for (int i = 0; i < UtilityCalc.rand(min, max); i++) {
                        String lore = l.get(UtilityCalc.rand(1, l.size()) - 1);
                        l.remove(lore);
                        loreList.add(prosessLootName(lore, stack));
                    }
            }
            // System.out.println("8");
            ItemMeta meta = stack.getItemMeta();
            // Durability
            if (plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".durability") != null) {
                String durabilityString = plugin.getConfig()
                        .getString("bosses." + bossType + ".loot." + loot + ".durability");
                int durability = UtilityCalc.getIntFromString(durabilityString);
                ((org.bukkit.inventory.meta.Damageable) meta).setDamage(durability);
                // stack.setDurability((short) durability);
            }
            // Name
            if (name != null) {
                meta.setDisplayName(name);
                // System.out.println("9");
            }
            // Lore
            if (!loreList.isEmpty()) {
                meta.setLore(loreList);
                // System.out.println("10");
            }
            stack.setItemMeta(meta);
            // Colour
            if (plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".colour") != null
                    && stack.getType().toString().toLowerCase().contains("leather")) {
                String c = plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".colour");
                String[] split = c.split(",");
                Color colour = Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]),
                        Integer.parseInt(split[2]));
                dye(stack, colour);
            }
            // Book
            if ((stack.getType().equals(Material.WRITTEN_BOOK)) || (stack.getType().equals(Material.WRITABLE_BOOK))) {
                BookMeta bMeta = (BookMeta) stack.getItemMeta();
                if (plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".author") != null) {
                    String author = plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".author");
                    author = ChatColor.translateAlternateColorCodes('&', author);
                    bMeta.setAuthor(author);
                }
                if (plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".title") != null) {
                    String title = plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".title");
                    title = ChatColor.translateAlternateColorCodes('&', title);
                    bMeta.setTitle(title);
                }
                if (plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".pages") != null) {
                    for (String i : plugin.getConfig()
                            .getConfigurationSection("bosses." + bossType + ".loot." + loot + ".pages")
                            .getKeys(false)) {
                        String page = plugin.getConfig()
                                .getString("bosses." + bossType + ".loot." + loot + ".pages." + i);
                        page = ChatColor.translateAlternateColorCodes('&', page);
                        bMeta.addPage(page);
                    }
                }
                stack.setItemMeta(bMeta);
            }
            // Banners
            if (stack.getType().toString().contains("BANNER")) {
                BannerMeta b = (BannerMeta) stack.getItemMeta();
                List<Pattern> patList = (List<Pattern>) plugin.getConfig()
                        .getList("bosses." + bossType + ".loot." + loot + ".patterns");
                if (patList != null && (!patList.isEmpty()))
                    b.setPatterns(patList);
                stack.setItemMeta(b);
            }
            // Shield
            if (stack.getType().equals(Material.SHIELD)) {
                ItemMeta im = stack.getItemMeta();
                BlockStateMeta bmeta = (BlockStateMeta) im;

                Banner b = (Banner) bmeta.getBlockState();
                List<Pattern> patList = (List<Pattern>) plugin.getConfig()
                        .getList("bosses." + bossType + ".loot." + loot + ".patterns");
                b.setBaseColor(
                        DyeColor.valueOf(plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".colour")));
                b.setPatterns(patList);
                b.update();
                bmeta.setBlockState(b);
                stack.setItemMeta(bmeta);
            }
            // Owner
            if (stack.getType().equals(Material.PLAYER_HEAD)) {
                String owner = plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".owner");
                if (owner != null) {
                    SkullMeta sm = (SkullMeta) stack.getItemMeta();
                    sm.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(owner)));
                    stack.setItemMeta(sm);
                }
            }
            // Potions
            if (plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".potion") != null)
                if (stack.getType().equals(Material.POTION) || stack.getType().equals(Material.SPLASH_POTION)
                        || stack.getType().equals(Material.LINGERING_POTION)) {
                    PotionMeta pMeta = (PotionMeta) stack.getItemMeta();
                    String pn = plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".potion");
                    CompatibilityResolver.setBasePotion(pMeta, PotionType.valueOf(pn));
//                    pMeta.setBasePotionType(PotionType.valueOf(pn));
                    stack.setItemMeta(pMeta);
                }
            int enchAmount = 0;
            for (int e = 0; e <= 10; e++) {
                if (plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".enchantments." + e) != null) {
                    enchAmount++;
                }
            }
            if (enchAmount > 0) {
                int enMin = enchAmount / 2;
                if (enMin < 1) {
                    enMin = 1;
                }
                int enMax = enchAmount;
                if ((plugin.getConfig().getString("bosses." + bossType + ".loot." + loot + ".minEnchantments") != null)
                        && (plugin.getConfig()
                        .getString("bosses." + bossType + ".loot." + loot + ".maxEnchantments") != null)) {
                    enMin = plugin.getConfig().getInt("bosses." + bossType + ".loot." + loot + ".minEnchantments");
                    enMax = plugin.getConfig().getInt("bosses." + bossType + ".loot." + loot + ".maxEnchantments");
                }
                int enchNeeded = new Random().nextInt(enMax + 1 - enMin) + enMin;
                if (enchNeeded > enMax) {
                    enchNeeded = enMax;
                }
                ArrayList<LevelledEnchantment> enchList = new ArrayList<>();
                int safety = 0;
                int j = 0;
                int chance;
                do {
                    if (plugin.getConfig()
                            .getString("bosses." + bossType + ".loot." + loot + ".enchantments." + j) != null) {
                        int enChance = 1;
                        if (plugin.getConfig().getString(
                                "bosses." + bossType + ".loot." + loot + ".enchantments." + j + ".chance") != null) {
                            enChance = plugin.getConfig()
                                    .getInt("bosses." + bossType + ".loot." + loot + ".enchantments." + j + ".chance");
                        }
                        chance = new Random().nextInt(enChance - 1 + 1) + 1;
                        if (chance == 1) {
                            String enchantment = plugin.getConfig().getString(
                                    "bosses." + bossType + ".loot." + loot + ".enchantments." + j + ".enchantment");

                            String levelString = plugin.getConfig().getString(
                                    "bosses." + bossType + ".loot." + loot + ".enchantments." + j + ".level");
                            int level = UtilityCalc.getIntFromString(levelString);
                            NamespacedKey k = NamespacedKey.minecraft(enchantment.toLowerCase());
                            if(Enchantment.getByKey(k) == null && enchantment.equals("sweeping")){
                                k = NamespacedKey.minecraft(("sweeping_edge"));
                            }
                            if (Enchantment.getByKey(k) != null) {
                                // if (Enchantment.getByName(enchantment) != null) {
                                if (level < 1) {
                                    level = 1;
                                }
                                LevelledEnchantment le = new LevelledEnchantment(
                                        Enchantment.getByKey(NamespacedKey.minecraft(enchantment.toLowerCase())),
                                        level);

                                boolean con = false;
                                for (LevelledEnchantment testE : enchList) {
                                    if (testE.getEnchantment.equals(le.getEnchantment)) {
                                        con = true;
                                        break;
                                    }
                                }
                                if (!con) {
                                    enchList.add(le);
                                }
                            } else {
                                System.out.println("Error: No valid drops found!");
                                System.out.println("Error: " + enchantment + " is not a valid enchantment!");
                                return null;
                            }
                        }
                    }
                    j++;
                    if (j > enchAmount) {
                        j = 0;
                        safety++;
                    }
                    if (safety >= enchAmount * 100) {
                        System.out.println("Error: No valid drops found!");
                        System.out.println("Error: Please increase chance for enchantments on item " + loot);
                        return null;
                    }
                } while (enchList.size() != enchNeeded);
                for (LevelledEnchantment le : enchList) {
                    if (stack.getType().equals(Material.ENCHANTED_BOOK)) {
                        EnchantmentStorageMeta enchantMeta = (EnchantmentStorageMeta) stack.getItemMeta();
                        enchantMeta.addStoredEnchant(le.getEnchantment, le.getLevel, true);
                        stack.setItemMeta(enchantMeta);
                    } else {
                        stack.addUnsafeEnchantment(le.getEnchantment, le.getLevel);
                    }
                }
            }
            return stack;
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, e.getMessage(), true);
            e.printStackTrace();
        }
        return null;
    }

    public void dye(ItemStack item, Color color) {
        try {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(color);
            item.setItemMeta(meta);
        } catch (Exception e) {
        }
    }

    private String prosessLootName(String name, ItemStack stack) {
        name = ChatColor.translateAlternateColorCodes('&', name);
        String itemName = stack.getType().name();
        itemName = itemName.replace("_", " ");
        itemName = itemName.toLowerCase();
        name = name.replace("<itemName>", itemName);
        return name;
    }

    public ItemStack getIllagerItem() {
        ItemStack s = getItem(Material.BELL, config.getLang("items.bell"), 1, config.getLangList("items.belllore"));
        ItemMeta m = s.getItemMeta();
        m.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        s.setItemMeta(m);
        return s;
    }

    public ItemStack getWizardItem() {
        ItemStack s = getItem(Material.ENCHANTED_BOOK, config.getLang("items.spellbook"), 1,
                config.getLangList("items.spellbooklore"));
        ItemMeta m = s.getItemMeta();
        m.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        s.setItemMeta(m);
        return s;
    }

    public ItemStack getGiantIem() {
        ItemStack item = getItem(Material.POTION, config.getLang("items.giantpotion"), 1,
                config.getLangList("items.giantpotionlore"));
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setColor(Color.GREEN);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 3600 * 20, 10), true);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getDragonItem() {
        ItemStack s = getItem(Material.DRAGON_EGG, config.getLang("items.elderegg"), 1, config.getLangList("items.elderegglore"));
        ItemMeta m = s.getItemMeta();
        m.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        s.setItemMeta(m);
        return s;
    }

    public ItemStack getGodItem() {
        ItemStack s = getItem(Material.ENCHANTED_GOLDEN_APPLE, config.getLang("items.forbiddenfruit"), 1,
                config.getLangList("items.forbiddenfruitlore"));
        ItemMeta m = s.getItemMeta();
        // m.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        s.setItemMeta(m);
        return s;
    }

    public ItemStack getDevilItem() {
        ItemStack s = getItem(Material.APPLE, config.getLang("items.abhorrentfruit"), 1,
                config.getLangList("items.abhorrentfruitlore"));
        ItemMeta m = s.getItemMeta();
        m.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        s.setItemMeta(m);
        return s;
    }

    public ItemStack getDeathItem() {
        ItemStack s = SkullCreator.getSkull(
                "http://textures.minecraft.net/texture/7eea345908d17dc44967d1dce428f22f2b19397370abeb77bdc12e2dd1cb6");
        ItemMeta m = s.getItemMeta();
        // m.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        m.setDisplayName(config.getLang("items.deathnote"));
        m.setLore(config.getLangList("items.deathnotelore"));
        s.setItemMeta(m);
        return s;
    }
}
