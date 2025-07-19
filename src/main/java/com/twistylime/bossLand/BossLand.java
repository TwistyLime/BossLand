package com.twistylime.bossLand;

import java.io.File;
import java.io.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Banner;

import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.banner.Pattern;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.IronGolem;
import org.bukkit.entity.LargeFireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Panda.Gene;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Rabbit.Type;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.Slime;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.entity.Trident;
import org.bukkit.entity.WitherSkull;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.SlimeSplitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

public class BossLand extends JavaPlugin implements Listener {

    String version = Version.getServerVersion();

    File saveYML = new File(getDataFolder(), "save.yml");
    YamlConfiguration saveFile = YamlConfiguration.loadConfiguration(saveYML);

    File langYML = new File(getDataFolder(), "lang.yml");
    YamlConfiguration langFile = YamlConfiguration.loadConfiguration(langYML);

    File bookYML = new File(getDataFolder(), "book.yml");

    HashMap<Entity, BossBar> bossMap = new HashMap<>();
    HashMap<Entity, Entity> targetMap = new HashMap<>();
    HashMap<Entity, UUID> controlMap = new HashMap<>();
    HashMap<Entity, Player> itemDropMap = new HashMap<>();
    // HashMap<String, BossBar> playerBars = new HashMap<String, BossBar>();
    ArrayList<UUID> coolList = new ArrayList<>();
    ArrayList<UUID> diedList = new ArrayList<>();
    ArrayList<UUID> noFireList = new ArrayList<>();
    ArrayList<Projectile> lightningList = new ArrayList<>();
    ArrayList<UUID> noList = new ArrayList<>();
    ArrayList<UUID> deathList = new ArrayList<>();
    ArrayList<UUID> canEnterDeath = new ArrayList<>();
    ArrayList<UUID> hadDeathNote = new ArrayList<>();
    ArrayList<FallingBlock> removeBLockList = new ArrayList<>();

    @Override
    public void onEnable() {
        this.getLogger().log(Level.INFO, "The Server version is: "+version);
        getServer().getPluginManager().registerEvents(this, this);
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        // Register Saves
        if (!saveYML.exists()) {
            try {
                if(saveYML.createNewFile()){
                    this.getLogger().log(Level.INFO, "New save.yml generated.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Register Lang
        if (!langYML.exists()) {
            this.getLogger().log(Level.INFO, "No lang.yml found, generating...");
            // Generate Lang
            this.saveResource("lang.yml", false);
            // new File(this.getDataFolder(), "lang.yml").renameTo(new
            // File(this.getDataFolder(), "lang.yml"));
            this.getLogger().log(Level.INFO, Bukkit.getVersion() + " Lang successfully generated!");
            reloadLang();
        }
        // Register Guide book
        if (!bookYML.exists()) {
            this.getLogger().log(Level.INFO, "No book.yml found, generating...");
            // Generate Book
            this.saveResource("book.yml", false);
            this.getLogger().log(Level.INFO, Bukkit.getVersion() + " Book successfully generated!");
        }
        // Metrics
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            // Failed to submit the stats :-(
        }
        addRecipes();
        timer();
        Objects.requireNonNull(this.getCommand("bossland")).setTabCompleter(new BossLandTabCompleter());
    }

    private void reloadLang() {
        if (this.langYML == null) {
            this.langYML = new File(getDataFolder(), "lang.yml");
        }
        this.langFile = YamlConfiguration.loadConfiguration(this.langYML);

        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(langYML);
        this.langFile.setDefaults(defConfig);
    }

    private String getBossItemName(String b, int l) {
        return Objects.requireNonNull(getConfig().getString("bosses." + b + ".loot." + l + ".name")).replace("&", "§");
    }

    private String getLang(String s) {
        if (langFile.getString(s) == null) {
            this.getLogger().log(Level.SEVERE, "Error with Lang file!");
            System.out.print("Looking for path: " + s);
            System.out.print("Found: " + langFile.getString(s));
            langFile.set(s, "Missing!");
            try {
                langFile.save(langYML);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Objects.requireNonNull(langFile.getString(s)).replace("&", "§");
    }

    @SuppressWarnings("unchecked")
    private ArrayList<String> getLangList(String s) {
        ArrayList<String> list = (ArrayList<String>) langFile.getList(s);
        if (list == null || list.isEmpty()) {
            this.getLogger().log(Level.SEVERE, "Error with Lang file!");
            System.out.print("Looking for list path: " + s);
            System.out.print("Found: " + langFile.getString(s));
        }
        ArrayList<String> list2 = new ArrayList<>();
        assert list != null;
        for (String l : list)
            list2.add(l.replace("&", "§"));
        return list2;
    }

    @SuppressWarnings({ "unchecked" })
    public void timer() {
        try {
            HashMap<Entity, BossBar> tmp = (HashMap<Entity, BossBar>) bossMap.clone();
            for (Map.Entry<Entity, BossBar> hm : tmp.entrySet()) {
                Entity e = hm.getKey();
                // Dead Check
                if (((Damageable) e).getHealth() <= 0) {
                    bossMap.remove(e);
                    saveFile.set("bosses." + e.getUniqueId(), null);
                    save();
                }
                // Fire Check
                if ((e instanceof LivingEntity))
                    e.setFireTicks(0);
            }
            for (Player p : Bukkit.getServer().getOnlinePlayers())
                if (!p.isDead()) {
                    // Potion Effects
                    try {
                        if (Objects.requireNonNull(Objects.requireNonNull(p.getInventory().getLeggings()).getItemMeta())
                                .getDisplayName().equals(getBossItemName("PapaPanda", 1))) {
                            p.removePotionEffect(PotionEffectType.SPEED);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, 3));
                            repairItem(p.getInventory().getLeggings());
                        } else if (p.getInventory().getLeggings().getItemMeta().getDisplayName()
                                .equals(getBossItemName("KillerBunny", 2))) {
                            p.removePotionEffect(PotionEffectType.JUMP_BOOST);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 20 * 5, 3));
                            repairItem(p.getInventory().getLeggings());
                        } else if (p.getInventory().getLeggings().getItemMeta().getDisplayName()
                                .equals(getBossItemName("Devil", 0))) {
                            p.removePotionEffect(PotionEffectType.FIRE_RESISTANCE);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 20 * 5, 3));
                            // p.removePotionEffect(PotionEffectType.ABSORPTION);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 20 * 10, 1));
                            repairItem(p.getInventory().getLeggings());
                        }
                    } catch (Exception x2) {
                    }
                    try {
                        if (Objects.requireNonNull(Objects.requireNonNull(p.getInventory().getChestplate()).getItemMeta()).getDisplayName()
                                .equals(getBossItemName("AetherGod", 0))) {
                            p.removePotionEffect(PotionEffectType.SLOW_FALLING);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 20 * 5, 3));
                            // p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                            // p.addPotionEffect(new
                            // PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,20*5,2));
                            p.removePotionEffect(PotionEffectType.STRENGTH);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 20 * 5, 1));
                            repairItem(p.getInventory().getChestplate());
                        }
                    } catch (Exception x2) {
                    }
                    try {
                        if (Objects.requireNonNull(Objects.requireNonNull(p.getInventory().getBoots()).getItemMeta()).getDisplayName()
                                .equals(getBossItemName("PharaohGod", 2))) {
                            p.removePotionEffect(PotionEffectType.HASTE);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 20 * 5, 2));
                            p.removePotionEffect(PotionEffectType.JUMP_BOOST);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 20 * 5, 2));
                            p.removePotionEffect(PotionEffectType.SPEED);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 20 * 5, 2));
                            repairItem(p.getInventory().getBoots());
                        }
                    } catch (Exception x2) {
                    }
                    try {
                        if (Objects.requireNonNull(Objects.requireNonNull(p.getInventory().getHelmet()).getItemMeta()).getDisplayName()
                                .equals(getBossItemName("DrownedGod", 2))) {
                            p.removePotionEffect(PotionEffectType.CONDUIT_POWER);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.CONDUIT_POWER, 20 * 5, 0));
                            p.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 20 * 5, 0));
                            p.removePotionEffect(PotionEffectType.NIGHT_VISION);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 20 * 15, 0));
                            repairItem(p.getInventory().getHelmet());
                        }
                    } catch (Exception x2) {
                    }
                    // Bar Add
                    double dis = getConfig().getInt("bossRange");
                    Entity b = null;
                    for (Map.Entry<Entity, BossBar> hm : bossMap.entrySet()) {
                        Entity boss = hm.getKey();
                        if (p.getWorld().equals(boss.getWorld()))
                            if (p.getLocation().distance(boss.getLocation()) < dis) {
                                dis = p.getLocation().distance(boss.getLocation());
                                b = boss;
                            }
                    }
                    // System.out.println("B: " + b);
                    if (b != null) {
                        showBossBar(p, b);
                    } else
                        removeBar(p);
                }
        } catch (Exception x) {
            x.printStackTrace();
        }
        // Tick
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, this::timer, (20));
    }

    private void repairItem(ItemStack s) {
        ((org.bukkit.inventory.meta.Damageable) s).setDamage(0);
    }

    private void save() {
        try {
            this.saveFile.save(this.saveYML);
        } catch (IOException localIOException) {
        }
    }

    // @EventHandler(priority=EventPriority.HIGH)
    // public void onPlayerMove(PlayerMoveEvent e) {
    // Player p = e.getPlayer();
    // try {
    // if(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§lStaff
    // of Control")) {
    //
    // }
    // }catch(Exception x) {}
    // }

    @EventHandler(priority = EventPriority.HIGH)
    public void onProjectileHit(ProjectileHitEvent e) {
        if (lightningList.contains(e.getEntity())) {
            e.getEntity().getWorld().strikeLightning(e.getEntity().getLocation());
            lightningList.remove(e.getEntity());
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        // Check for God Trident
        try {
            final String bossType = saveFile.getString("bosses." + e.getEntity().getUniqueId());
            if (e.getEntity().getShooter() != null) {
                if (e.getEntity().getShooter() instanceof Player p) {
                    // Check For Trident
                    if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getDisplayName()
                            .equals(getBossItemName("DrownedGod", 0))) {
                        lightningList.add(e.getEntity());
                    } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName()
                            .equals(getBossItemName("AetherGod", 2))) {
                        Arrow a = (Arrow) e.getEntity();
                        a.setGlowing(true);
                        a.setBasePotionType(PotionType.SLOWNESS);
                        makeTrail(a, "CLOUD:0:1:0");
                        Entity t = getTarget(p);
                        if (t != null)
                            moveToward(a, t, 1.1);
                        // lightningList.add(e.getEntity());
                    }
                } else if (bossType != null && bossType.equals("DrownedGod")) {
                    lightningList.add(e.getEntity());
                }
            }
        } catch (Exception x) {
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        try {
            List<Material> list = Arrays.asList(Material.DIRT, Material.GRASS_BLOCK, Material.DIRT_PATH,
                    Material.GRAVEL, Material.SAND, Material.RED_SAND, Material.SOUL_SAND, Material.FARMLAND,
                    Material.CLAY, Material.MYCELIUM, Material.PODZOL, Material.COARSE_DIRT, Material.SNOW_BLOCK,
                    Material.SNOW);
            if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getDisplayName()
                    .equals(getBossItemName("Giant", 1))) {
                dig(p, e.getBlock().getLocation(), list, false);
            } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName()
                    .equals(getBossItemName("Giant", 2))) {
                dig(p, e.getBlock().getLocation(), list, true);
            }
        } catch (Exception x) {
        }
    }

    private void dig(Player p, Location l, List<Material> cb, boolean canBreak) {
        for (double x = l.getX() - (double) 2 / 2; x <= l.getX() + (double) 2 / 2; x += 1.0D) {
            for (double y = l.getY() - (double) 2 / 2; y <= l.getY() + (double) 2 / 2; y += 1.0D) {
                for (double z = l.getZ() - (double) 2 / 2; z <= l.getZ() + (double) 2 / 2; z += 1.0D) {
                    // Break Block
                    Block b = new Location(l.getWorld(), x, y, z).getBlock();
                    if (!b.getType().equals(Material.BEDROCK))
                        if (cb == null || (canBreak && cb.contains(b.getType()))
                                || (!canBreak && !cb.contains(b.getType())))
                            b.breakNaturally();
                }
            }
        }
        p.updateInventory();
    }

    private void enterDeath(final Player p) {
        this.getLogger().log(Level.INFO, "Enter Death");
        if (p.getWorld().getEnvironment().equals(Environment.NORMAL)) {
            World end = getServer().getWorld(p.getWorld().getName() + "_the_end");
            assert end != null;
            p.teleport(end.getSpawnLocation());
            p.sendMessage("§5§lPrepare to die!");
            final Location l = new Location(end, 0, end.getHighestBlockYAt(0, 0) + 3, 0);
            p.getWorld().createExplosion(l, 4);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> spawnBoss(p, l, "Death"), (10));
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerRespawn(PlayerRespawnEvent e) {
        // System.out.println("onPlayerRespawn");
        final Player p = e.getPlayer();
        // System.out.println("canEnterDeath = " +
        // canEnterDeath.contains(p.getUniqueId()));
        // System.out.println("hasDeathNote = " + hasDeathNote(p));
        if (canEnterDeath.contains(p.getUniqueId()) && hasDeathNote(p)) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> enterDeath(p), 5);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamaged(EntityDamageEvent e) {
        if (e.getEntity() instanceof Player p) {
            // Slime Boots
            try {
                if (e.getCause().equals(DamageCause.FALL) && Objects.requireNonNull(Objects.requireNonNull(p.getInventory().getBoots()).getItemMeta()).getDisplayName()
                        .contains(getBossItemName("KingSlime", 0))) {
                    e.setCancelled(true);
                    Vector v = p.getVelocity();
                    // System.out.println("DMG: " + e.getDamage());
                    v.setY(p.getFallDistance() / 14.0);
                    // System.out.println("FD: " + p.getFallDistance());
                    final Vector vel = v.clone();
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                        // p.setFallDistance(0);
                        p.setVelocity(vel);
                        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_SLIME_BLOCK_HIT, 1, 1);
                    }, 2);
                }
            } catch (Exception x) {
            }
            try {
                if ((!e.isCancelled()) && (!diedList.contains(p.getUniqueId())) && Objects.requireNonNull(p.getInventory().getItemInOffHand()
                        .getItemMeta()).getDisplayName().equals(getBossItemName("PharaohGod", 0))) {
                    // System.out.println("D2: " + p.getHealth() + " : " + e.getFinalDamage());
                    if (checkDeath(p, e.getFinalDamage())) {
                        e.setCancelled(true);
                        // Died
                        // Check for Death Item
                        this.getLogger().log(Level.INFO, p.getName() + " should have died.");
                    }
                }
            } catch (Exception x) {
            }
            if ((!e.isCancelled()) && (p.getInventory().getItemInOffHand().getType().equals(Material.TOTEM_OF_UNDYING)
                    || p.getInventory().getItemInMainHand().getType().equals(Material.TOTEM_OF_UNDYING))) {
                if (p.getHealth() - e.getFinalDamage() <= 0) {
                    // Died
                    this.getLogger().log(Level.INFO, p.getName() + " should have died.");
                    // System.out.println("canEnterDeath = " +
                    // canEnterDeath.contains(p.getUniqueId()));
                    // System.out.println("hasDeathNote = " + hasDeathNote(p));
                    if (canEnterDeath.contains(p.getUniqueId()) && hasDeathNote(p)) {
                        enterDeath(p);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityTarget(EntityTargetEvent e) {
        try {
            if (e.getTarget() != null) {
                String bossType = saveFile.getString("bosses." + e.getTarget().getUniqueId());
                if (bossType != null) {
                    e.setCancelled(true);
                } else if (e.getEntity() instanceof PigZombie) {
                    if ((!(e.getTarget() instanceof Player)) && Objects.requireNonNull(Objects.requireNonNull(((PigZombie) e.getEntity()).getEquipment()).getHelmet())
                            .getType().equals(Material.PLAYER_HEAD))
                        e.setCancelled(true);
                }
            }
        } catch (Exception x) {
        }
    }

    @SuppressWarnings({ "unchecked" })
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent e) {
        // Check if is Boss
        final String bossType = saveFile.getString("bosses." + e.getEntity().getUniqueId());
        if (bossType != null && (e.getEntity() instanceof LivingEntity ent)) {
            // Make Sure Real Boss
            if (!bossMap.containsKey(ent))
                makeBoss(ent, bossType);
            updateHP(ent);
            // System.out.println("Boss Damaged");
            // Stop Self Damage
            if (e.getDamager() instanceof Projectile)
                try {
                    if (Objects.equals(((Projectile) e.getDamager()).getShooter(), ent)) {
                        e.setCancelled(true);
                        return;
                    }
                } catch (Exception x) {
                }
            // Potions
            int pc = getConfig().getInt("bosses." + bossType + ".potionChance");
            final LivingEntity dmgr = getDamager(e.getDamager());
            if (dmgr != null && rand(1, 100) <= pc) {
                // System.out.println("Pot");
                for (String pn : (ArrayList<String>) Objects
                        .requireNonNull(getConfig().getList("bosses." + bossType + ".hurtPotions"))) {
                    String[] split = pn.split(":");
                    if (PotionEffectType.getByName(split[0]) != null) {
                        if (split[0].equalsIgnoreCase("FIRE")) {
                            dmgr.setFireTicks(Integer.parseInt(split[2]) * 20);
                        } else
                            dmgr.addPotionEffect(new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(split[0])),
                                    Integer.parseInt(split[2]) * 20, Integer.parseInt(split[1]) - 1));
                    }
                }
            }
            // Check Player
            if (!(dmgr instanceof Player p))
                return;
            // Do Boss Stuff
            int c = getConfig().getInt("bosses." + bossType + ".specialChance");
            int mc = getConfig().getInt("bosses." + bossType + ".minionChance");
            double maxHealth = Objects.requireNonNull(ent.getAttribute(Attribute.MAX_HEALTH)).getBaseValue();
            switch (bossType) {
                case "GhastLord" -> {
                    // Stop Instant Death
                    if (e.getDamager().getType().equals(EntityType.FIREBALL)) {
                        // System.out.println("FB Rebound");
                        e.getDamager().remove();
                        e.setCancelled(true);
                        // ent.damage(50);
                    }
                    // Special Attack
                    if (rand(1, 100) <= c) {
                        // System.out.println("Phase 1 power");
                        // Phase 1
                        spawnMinions(ent, bossType, 5, false);
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 2) {
                        // System.out.println("Phase 2 power");
                        // Phase 2
                        int balls = getConfig().getInt("bosses." + bossType + ".amountSpecial2");
                        for (int i = 0; i < balls; i++)
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                                Fireball f = ent.launchProjectile(Fireball.class);
                                // Location l = ent.getLocation();
                                // l.setY(l.getY()-2);
                                // Fireball f = (Fireball) l.getWorld().spawnEntity(l, EntityType.FIREBALL);
                                // f.setShooter(ent);
                                moveToward(f, dmgr.getLocation(), 0.6);
                                boomTimer(f, 5);
                                makeTrail(f, getConfig().getString("bosses." + bossType + ".attackParticle"));
                            }, 5L * i);
                    }
                }
                case "KingSlime" -> {
                    // Special Attack
                    if (rand(1, 100) <= c) {
                        // Phase 1
                        spawnMinions(ent, bossType, 2, false);
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 2) {
                        // Phase 2
                        dmgr.setVelocity(new Vector(rand(-1, 1), rand(1, 2), rand(-1, 1)));
                    }
                }
                case "KillerBunny" -> {
                    // Special Attack
                    if (rand(1, 100) <= c) {
                        // Phase 1
                        spawnMinions(ent, bossType, 0, false);
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 2) {
                        // Phase 2
                        int s = rand(1, 4);
                        try {
                            if (s == 1 && p.getInventory().getHelmet() != null) {
                                ent.getWorld().dropItem(ent.getLocation(), p.getInventory().getHelmet());
                                p.getInventory().setHelmet(null);
                            } else if (s == 2 && p.getInventory().getChestplate() != null) {
                                ent.getWorld().dropItem(ent.getLocation(), p.getInventory().getChestplate());
                                p.getInventory().setChestplate(null);
                            } else if (s == 3 && p.getInventory().getLeggings() != null) {
                                ent.getWorld().dropItem(ent.getLocation(), p.getInventory().getLeggings());
                                p.getInventory().setLeggings(null);
                            } else if (s == 4 && p.getInventory().getBoots() != null) {
                                ent.getWorld().dropItem(ent.getLocation(), p.getInventory().getBoots());
                                p.getInventory().setBoots(null);
                            }
                        } catch (Exception x) {
                        }
                    }
                }
                case "PapaPanda" -> {
                    // Special Attack
                    if (rand(1, 100) <= c) {
                        // Phase 1
                        spawnMinions(ent, bossType, 1, false);
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 2) {
                        // Phase 2
                        Location l = dmgr.getLocation();
                        // Land Check
                        Location g = l.clone();
                        g.setY(g.getY() - 1);
                        if (g.getBlock().getType().equals(Material.AIR)
                                || g.getBlock().getType().equals(Material.WATER))
                            g.getBlock().setType(Material.DIRT);
                        // DMG
                        ent.getWorld().playSound(l, Sound.BLOCK_BAMBOO_PLACE, 1, 1);
                        dmgr.damage(getConfig().getInt("bosses." + bossType + ".specal2DMG"), ent);
                        // Block Change
                        for (int i = 0; i <= rand(7, 14); i++) {
                            Location tmp = l.clone();
                            tmp.setY(tmp.getY() + i);
                            tmp.getBlock().setType(Material.BAMBOO);
                        }
                    }
                }
                case "WitherSkeletonKing" -> {
                    // Special Attack
                    if (rand(1, 100) <= c) {
                        // Phase 1
                        spawnMinions(ent, bossType, 1, false);
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 2) {
                        // Phase 2
                        // if(getMount(ent) == null) {
                        // SkeletonHorse mount = (SkeletonHorse)
                        // ent.getWorld().spawnEntity(ent.getLocation(), EntityType.SKELETON_HORSE);
                        // mount.setInvulnerable(true);
                        // mount.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                        // mount.addPassenger(ent);
                        // }
                        // Other Effects
                        int balls = getConfig().getInt("bosses." + bossType + ".amountSpecial2");
                        for (int i = 0; i < balls; i++)
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                                WitherSkull f = ent.launchProjectile(WitherSkull.class);
                                moveToward(f, dmgr.getLocation(), 0.6);
                                boomTimer(f, 5);
                                makeTrail(f, getConfig().getString("bosses." + bossType + ".attackParticle"));
                            }, 5L * i);
                    }
                }
                case "ZombieKing" -> {
                    // Stop Boom Death
                    if (e.getCause().equals(DamageCause.ENTITY_EXPLOSION)
                            || e.getCause().equals(DamageCause.BLOCK_EXPLOSION)) {
                        e.setCancelled(true);
                    }
                    // Special Attack
                    if (rand(1, 100) <= c) {
                        // Phase 1
                        spawnMinions(ent, bossType, 1, ent.getHealth() <= (maxHealth / 2));
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 2) {
                        // Phase 2
                        // if(getMount(ent) == null) {
                        // ZombieHorse mount = (ZombieHorse)
                        // ent.getWorld().spawnEntity(ent.getLocation(), EntityType.ZOMBIE_HORSE);
                        // mount.setInvulnerable(true);
                        // mount.getInventory().setSaddle(new ItemStack(Material.SADDLE));
                        // mount.addPassenger(ent);
                        // }
                        // Other Effects
                        TNTPrimed tnt = (TNTPrimed) ent.getWorld().spawnEntity(ent.getEyeLocation(), EntityType.TNT);
                        moveToward(tnt, dmgr.getLocation(), 0.5);
                    }
                }
                case "EvilWizard" -> {
                    // Special Attack
                    if (rand(1, 100) <= c) {
                        // Phase 1
                        spawnMinions(ent, bossType, 1, false);
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 3 * 2) {
                        // Phase 2
                        ItemStack item = new ItemStack(Material.SPLASH_POTION);
                        PotionMeta meta = (PotionMeta) item.getItemMeta();
                        assert meta != null;
                        meta.setColor(Color.BLUE);
                        ArrayList<String> pots = (ArrayList<String>) getConfig()
                                .getList("bosses." + bossType + ".attackPotions");
                        assert pots != null;
                        String[] s = pots.get(rand(1, pots.size()) - 1).split(":");
                        meta.addCustomEffect(
                                new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(s[0])), 10 * 20, Integer.parseInt(s[1])),
                                true);
                        item.setItemMeta(meta);
                        ThrownPotion thrownPotion = ent.launchProjectile(ThrownPotion.class);
                        thrownPotion.setItem(item);
                        moveToward(thrownPotion, dmgr.getLocation(), 0.7);
                        boomTimer(thrownPotion, 4);
                        makeTrail(thrownPotion, getConfig().getString("bosses." + bossType + ".attackParticle"));
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 3) {
                        // Phase 3
                        dmgr.getWorld().strikeLightning(dmgr.getLocation());
                    }
                }
                case "IllagerKing" -> {
                    // Special Attack
                    if (rand(1, 100) <= c) {
                        // Phase 1
                        spawnMinions(ent, bossType, 1, false);
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 3 * 2) {
                        // Phase 2
                        ItemStack item = new ItemStack(Material.LINGERING_POTION);
                        PotionMeta meta = (PotionMeta) item.getItemMeta();
                        assert meta != null;
                        meta.setColor(Color.YELLOW);
                        ArrayList<String> pots = (ArrayList<String>) getConfig()
                                .getList("bosses." + bossType + ".attackPotions");
                        assert pots != null;
                        String[] s = pots.get(rand(1, pots.size()) - 1).split(":");
                        meta.addCustomEffect(
                                new PotionEffect(Objects.requireNonNull(PotionEffectType.getByName(s[0])), 10 * 20, Integer.parseInt(s[1])),
                                true);
                        item.setItemMeta(meta);
                        ThrownPotion thrownPotion = (ThrownPotion) ent.getWorld().spawnEntity(dmgr.getLocation(),
                                EntityType.POTION);
                        thrownPotion.setItem(item);
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 3) {
                        // Phase 3
                        Location l1 = dmgr.getLocation().clone();
                        Location l2 = dmgr.getLocation().clone();
                        Location l3 = dmgr.getLocation().clone();
                        Location l4 = dmgr.getLocation().clone();
                        Location l5 = dmgr.getLocation().clone();
                        Location l6 = dmgr.getLocation().clone();
                        Location l7 = dmgr.getLocation().clone();
                        Location l8 = dmgr.getLocation().clone();
                        Location l9 = dmgr.getLocation().clone();
                        Location l10 = dmgr.getLocation().clone();
                        l1.setY(l1.getY() + 20);
                        l2.setY(l2.getY() + 22);
                        l3.setY(l3.getY() + 20);
                        l3.setX(l3.getX() + 1);
                        l3.setZ(l3.getZ() + 1);
                        l4.setY(l4.getY() + 20);
                        l4.setX(l4.getX() + 1);
                        l5.setY(l5.getY() + 20);
                        l5.setX(l5.getX() + 1);
                        l5.setZ(l5.getZ() - 1);
                        l6.setY(l6.getY() + 20);
                        l6.setZ(l6.getZ() + 1);
                        l7.setY(l7.getY() + 20);
                        l7.setZ(l7.getZ() - 1);

                        l8.setY(l8.getY() + 20);
                        l8.setX(l8.getX() - 1);
                        l8.setZ(l8.getZ() + 1);
                        l9.setY(l9.getY() + 20);
                        l9.setX(l9.getX() - 1);
                        l10.setY(l10.getY() + 20);
                        l10.setX(l10.getX() - 1);
                        l10.setZ(l10.getZ() - 1);
                        for (Location bl : Arrays.asList(l1, l2, l3, l4, l5, l6, l7, l8, l9, l10))
                            ent.getWorld().spawnFallingBlock(bl, Bukkit.createBlockData(Material.COBBLESTONE));
                    }
                }
                case "Giant" -> {
                    // Special Attack
                    if (rand(1, 100) <= c) {
                        // Phase 1
                        spawnMinions(ent, bossType, 1, false);
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 3 * 2) {
                        // Phase 2
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this,
                                () -> dmgr.damage(getConfig().getInt("bosses." + bossType + ".special2Damage"), ent),
                                25);
                        // Vector direction =
                        // dmgr.getLocation().toVector().subtract(ent.getLocation().toVector()).normalize();
                        // Shoot Beam
                        Location point1 = ent.getLocation();
                        Location point2 = dmgr.getLocation();
                        int space = 1;
                        World world = point1.getWorld();
                        if (Objects.equals(point2.getWorld(), world)) {
                            double distance = point1.distance(point2);
                            Vector p1 = point1.toVector();
                            Vector p2 = point2.toVector();
                            Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
                            double length = 0;
                            int t = 0;
                            for (; length < distance; p1.add(vector)) {
                                // world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, p1.getX(), p1.getY(),
                                // p1.getZ(), 1);
                                final Location loc = new Location(world, p1.getX(), p1.getY(), p1.getZ());
                                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                                    for (int i = 0; i < 2; i++) {
                                        Location tmp = loc.clone();
                                        tmp.setY(tmp.getY() - i);
                                        if (!tmp.getBlock().getType().equals(Material.BEDROCK))
                                            tmp.getBlock().setType(Material.AIR);
                                    }
                                    Objects.requireNonNull(loc.getWorld()).playSound(loc, Sound.BLOCK_STONE_BREAK, 1, 1);
                                    displayParticle(Particle.LARGE_SMOKE.toString(), loc, 0.3, 0, 3);
                                }, t);
                                t = t + 1;
                                length += space;
                            }
                        } else
                            this.getLogger().log(Level.SEVERE, "Lines cannot be in different worlds!");
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 3) {
                        // Phase 3
                        dmgr.getWorld().playSound(dmgr.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 1, 1);
                        displayParticle(Particle.CLOUD.toString(), dmgr.getEyeLocation(), 0, 1, 10);
                        final Location bl = dmgr.getLocation();
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                            Location l1 = bl.clone();
                            l1.setX(l1.getX() + 1);
                            Location l2 = bl.clone();
                            l2.setX(l2.getX() - 1);
                            Location l3 = bl.clone();
                            l3.setZ(l3.getZ() + 1);
                            Location l4 = bl.clone();
                            l4.setZ(l4.getZ() - 1);
                            for (Location i : Arrays.asList(bl, l1, l2, l3, l4)) {
                                for (int j = 0; j < 12; j++) {
                                    Location tmp = i.clone();
                                    tmp.setY(tmp.getY() - j);
                                    if (!tmp.getBlock().getType().equals(Material.BEDROCK))
                                        if (j < 11) {
                                            tmp.getBlock().setType(Material.AIR);
                                        } else
                                            tmp.getBlock().setType(Material.LAVA);
                                }
                            }
                        }, 2 * 20);
                    }
                }
                case "DrownedGod" -> {
                    // Special Attack
                    if (rand(1, 100) <= c) {
                        // Phase 1
                        spawnGodMinions(ent, ent.getLocation(), bossType, rand(1, 6));
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 4 * 3) {
                        // Phase 2
                        Projectile pj = ent.launchProjectile(Trident.class);
                        lightningList.add(pj);
                        moveToward(p, dmgr.getLocation(), 0.7);
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 4 * 2) {
                        // Phase 3
                        moveToward(ent, dmgr.getLocation(), 0.5);
                        // Other Effects
                        int balls = getConfig().getInt("bosses." + bossType + ".amountSpecial3");
                        for (int i = 0; i < balls; i++)
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                                Projectile f = ent.launchProjectile(Trident.class);
                                lightningList.add(f);
                                moveToward(f, dmgr, 0.7);
                                boomTimer(f, 5);
                                makeTrail(f, getConfig().getString("bosses." + bossType + ".attackParticle"));
                            }, 5L * i);
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 4) {
                        // Phase 4
                        spawnGodMinions(ent, dmgr.getLocation(), bossType, rand(1, 6));
                    }
                }
                case "PharaohGod" -> {
                    // Special Attack
                    if (rand(1, 100) <= mc) {
                        // Phase 1
                        spawnGodMinions(ent, ent.getLocation(), bossType, rand(1, 6));
                    }
                    if (rand(1, 100) <= c) {
                        doFireBalls(ent, dmgr, bossType);
                    }
                    if (rand(1, 100) <= getConfig().getInt("bosses." + bossType + ".special3Chance")
                            && ent.getHealth() <= maxHealth / 4 * 2) {
                        // Phase 3
                        spawnTornado(ent.getEyeLocation(), dmgr,
                                getConfig().getInt("bosses." + bossType + ".specialDamage3"), "dark");
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 4) {
                        // Phase 4
                        Location l1 = dmgr.getLocation();
                        l1.setY(l1.getY() - 1);
                        Location l2 = dmgr.getLocation();
                        l2.setY(l2.getY() - 2);
                        Location l3 = dmgr.getLocation();
                        l3.setY(l3.getY() - 3);
                        for (Location l : Arrays.asList(l1, l2, l3))
                            if (!l.getBlock().getType().equals(Material.BEDROCK))
                                l.getBlock().setType(Material.AIR);
                        Location l4 = dmgr.getLocation();
                        l4.setY(l4.getY() + 1);
                        Location l5 = dmgr.getLocation();
                        l5.setY(l5.getY() + 2);
                        Location l6 = dmgr.getLocation();
                        l6.setY(l6.getY() + 3);
                        for (Location l : Arrays.asList(l1, l2, l3))
                            Objects.requireNonNull(l.getWorld()).spawnFallingBlock(l, Bukkit.createBlockData(Material.SAND));
                    }
                }
                case "AetherGod" -> {
                    // Special Attack
                    if (rand(1, 100) <= mc) {
                        // Phase 1
                        spawnGodMinions(ent, ent.getLocation(), bossType, rand(3, 10));
                    }
                    if (rand(1, 100) <= c) {
                        // Phase 1
                        int balls = getConfig().getInt("bosses." + bossType + ".amountSpecial");
                        for (int i = 0; i < balls; i++)
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                                Location l = ent.getEyeLocation();
                                l.setY(l.getY() + 1);
                                // Projectile f = (Projectile) ent.getWorld().spawnEntity(l, fbt);
                                Projectile f = ent.launchProjectile(ShulkerBullet.class);
                                moveToward(f, dmgr, 0.4);
                                boomTimer(f, 8);
                                makeTrail(f, getConfig().getString("bosses." + bossType + ".attackParticle"));
                            }, 7L * i);
                    }
                    if (ent.getHealth() <= maxHealth / 4 * 3) {
                        // Phase 2
                        if (rand(1, 100) <= c) {
                            dmgr.setVelocity(new Vector(rand(-0.5, 0.5), rand(0.2, 0.5), rand(-0.5, 0.5)));
                        }
                        if (rand(1, 100) <= c) {
                            Arrow a = ent.launchProjectile(Arrow.class);
                            a.setKnockbackStrength(5);
                            moveToward(a, dmgr, 0.7);
                            makeTrail(a, getConfig().getString("bosses." + bossType + ".attackParticle2"));
                        }
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 4 * 2) {
                        // Phase 3
                        spawnTornado(ent.getEyeLocation(), dmgr,
                                getConfig().getInt("bosses." + bossType + ".specialDamage3"), "wind");
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 4) {
                        // Phase 4
                        int strikes = getConfig().getInt("bosses." + bossType + ".amountSpecial4");
                        for (int i = 0; i < strikes; i++)
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this,
                                    () -> ent.getWorld().strikeLightning(dmgr.getLocation()), 10L * i);
                    }
                }
                case "Demon" -> {
                    // Special Attack
                    if (rand(1, 100) <= mc) {
                        // Phase 1
                        spawnGodMinions(ent, ent.getLocation(), bossType, rand(2, 5));
                    }
                    if (rand(1, 100) <= c) {
                        // Phase 1
                        int balls = getConfig().getInt("bosses." + bossType + ".amountSpecial");
                        for (int i = 0; i < balls; i++)
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                                Location l = ent.getEyeLocation();
                                l.setY(l.getY() + 1);
                                // Projectile f = (Projectile) ent.getWorld().spawnEntity(l, fbt);
                                Projectile f = ent.launchProjectile(Fireball.class);
                                moveToward(f, dmgr, 0.7);
                                boomTimer(f, 4);
                                makeTrail(f, getConfig().getString("bosses." + bossType + ".attackParticle"));
                            }, 10L * i);
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 4 * 3) {
                        // Phase 2
                        int s = 2;
                        for (Block b : getArea(
                                new Location(ent.getWorld(), ent.getLocation().getX() - s, ent.getLocation().getY() - s,
                                        ent.getLocation().getZ() - s),
                                new Location(ent.getWorld(), ent.getLocation().getX() + s, ent.getLocation().getY() + s,
                                        ent.getLocation().getZ() + s),
                                false))
                            if (Arrays.asList(Material.AIR, Material.GRASS_BLOCK, Material.TALL_GRASS, Material.WATER)
                                    .contains(b.getType()))
                                b.setType(Material.FIRE);
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 4 * 2) {
                        // Phase 3
                        ent.getWorld().createExplosion(dmgr.getLocation(), 2);
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 4) {
                        // Phase 4
                        displayParticle(Objects.requireNonNull(getConfig().getString("bosses." + bossType + ".tpParticle")), ent.getLocation());
                        ent.teleport(dmgr);
                        displayParticle(Objects.requireNonNull(getConfig().getString("bosses." + bossType + ".tpParticle")), ent.getLocation());
                        dmgr.damage(getConfig().getInt("bosses." + bossType + ".specialDamage"), ent);
                    }
                }
                case "Devil" -> {
                    // Fire
                    dmgr.getLocation().getBlock().setType(Material.FIRE);
                    // Minnions Attack
                    if (rand(1, 100) <= mc) {
                        spawnGodMinions(ent, ent.getLocation(), bossType, rand(4, 8));
                    }
                    // Phase Attacks
                    if (rand(1, 100) <= c) {
                        // Phase 1
                        LivingEntity b = (LivingEntity) ent.getWorld().spawnEntity(ent.getLocation(), EntityType.BAT);
                        makeTrail(b, getConfig().getString("bosses." + bossType + ".attackParticle"));
                        moveToward(b, dmgr, 0.3);
                        suicideTimer(b, 3);
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 5 * 4) {
                        // Phase 2
                        // Shoot Beam
                        Location point1 = ent.getLocation();
                        Location point2 = dmgr.getLocation();
                        int space = 1;
                        World world = point1.getWorld();
                        if (Objects.equals(point2.getWorld(), world)) {
                            double distance = point1.distance(point2);
                            Vector p1 = point1.toVector();
                            Vector p2 = point2.toVector();
                            Vector vector = p2.clone().subtract(p1).normalize().multiply(space);
                            double length = 0;
                            for (; length < distance; p1.add(vector)) {
                                // world.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, p1.getX(), p1.getY(),
                                // p1.getZ(), 1);
                                final Location loc = new Location(world, p1.getX(), p1.getY(), p1.getZ());
                                displayParticle(Particle.FLAME.toString(), loc, 0.5, 0, 4);
                                length += space;
                            }
                            // Damage
                            dmgr.damage(getConfig().getInt("bosses." + bossType + ".specialDamage2"), ent);
                        } else
                            this.getLogger().log(Level.SEVERE, "Lines cannot be in different worlds!");
                    }
                    if ((rand(1, 100) <= c) && ent.getHealth() <= ((maxHealth / 5) * 3)) {
                        // Phase 3
                        spawnTornado(ent.getEyeLocation(), dmgr,
                                getConfig().getInt("bosses." + bossType + ".specialDamage3"), "fire");
                    }
                    if ((rand(1, 100) <= c) && ent.getHealth() <= ((maxHealth / 5) * 2)) {
                        // Phase 4
                        Location l1 = dmgr.getLocation();
                        l1.setX(l1.getX() + 1);
                        l1.setY(l1.getY() + 1);
                        l1.setZ(l1.getZ() + 1);
                        Location l2 = dmgr.getLocation();
                        l2.setX(l2.getX() - 1);
                        l2.setY(l2.getY() - 25);
                        l2.setZ(l2.getZ() - 1);
                        for (Block b : getArea(l1, l2, false))
                            if ((!b.getType().equals(Material.LAVA)) && (!b.getType().equals(Material.BEDROCK))) {
                                b.setType(Material.AIR);
                            }
                    }
                    if ((rand(1, 100) <= c) && ent.getHealth() <= ((maxHealth / 5))) {
                        // Phase 5
                        moveTowardTemp(dmgr, ent, 0.4, 20 * 3);
                    }
                }
                case "Death" -> {
                    // Minnions Attack
                    if (rand(1, 100) <= mc) {
                        Location l = ent.getLocation();
                        if (rand(1, 2) == 1)
                            l = dmgr.getLocation();
                        spawnGodMinions(ent, l, bossType, rand(4, 8));
                    }
                    // Phase Attacks
                    if (rand(1, 100) <= c) {
                        // Phase 1 attack 1
                        int balls = getConfig().getInt("bosses." + bossType + ".amountSpecial");
                        for (int i = 0; i < balls; i++)
                            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                                DragonFireball f = ent.launchProjectile(DragonFireball.class);
                                moveToward(f, dmgr.getLocation(), 0.7);
                                boomTimer(f, 6);
                                makeTrail(f, getConfig().getString("bosses." + bossType + ".attackParticle"));
                            }, 15L * i);
                    }
                    if (rand(1, 100) <= c) {
                        // Phase 1 attack 2
                        Location l = dmgr.getLocation().clone();
                        l.setY(l.getY() - 1);
                        l.getBlock().setType(Material.END_STONE);
                        dmgr.getLocation().getBlock().setType(Material.WITHER_ROSE);
                    }
                    if (rand(1, 100) <= c && ent.getHealth() <= maxHealth / 5 * 4) {
                        // Phase 2
                        Location l1 = dmgr.getLocation().clone();
                        Location l2 = ent.getLocation().clone();
                        dmgr.teleport(l2);
                        ent.teleport(l1);
                        ent.getWorld().playSound(l1, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                        ent.getWorld().playSound(l2, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                        double h = dmgr.getHealth() - getConfig().getDouble("bosses." + bossType + ".specialDamage2");
                        boolean kill = false;
                        if (h <= 0) {
                            h = 1;
                            kill = true;
                        }
                        dmgr.setHealth(h);
                        if (kill)
                            dmgr.damage(999, ent);
                    }
                    if ((rand(1, 100) <= c) && ent.getHealth() <= ((maxHealth / 5) * 3)) {
                        // Phase 3
                        spawnTornado(ent.getEyeLocation(), dmgr,
                                getConfig().getInt("bosses." + bossType + ".specialDamage3"), "magic");
                    }
                    if ((rand(1, 100) <= c) && ent.getHealth() <= ((maxHealth / 5) * 2)) {
                        // Phase 4
                        HashMap<Block, Material> oldBlocks = new HashMap<>();
                        int radious = 2;
                        int depth = 50;
                        Location l1 = dmgr.getLocation();
                        l1.setX(l1.getX() + radious);
                        l1.setY(l1.getY() + radious);
                        l1.setZ(l1.getZ() + radious);
                        Location l2 = dmgr.getLocation();
                        l2.setX(l2.getX() - radious);
                        l2.setY(l2.getY() - depth);
                        l2.setZ(l2.getZ() - radious);
                        for (Block b : getArea(l1, l2, false))
                            if (!b.getType().equals(Material.BEDROCK)) {
                                oldBlocks.put(b, b.getType());
                                b.setType(Material.AIR);
                            }
                        // Restore Blocks
                        final HashMap<Block, Material> fOldBlocks = (HashMap<Block, Material>) oldBlocks.clone();
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                            for (Map.Entry<Block, Material> i : fOldBlocks.entrySet()) {
                                i.getKey().setType(i.getValue());
                            }
                        }, 30 * 20);
                    }
                    if ((rand(1, 100) <= c) && ent.getHealth() <= ((maxHealth / 5))) {
                        // Phase 5
                        int dis = rand(5, 10);
                        int mult = -1;
                        if (rand(1, 2) == 1)
                            mult = 1;
                        final Location l = ent.getLocation().clone();
                        ent.getWorld().playSound(l, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                        if (rand(1, 2) == 1) {
                            Location l2 = l.clone();
                            l2.setX((l2.getX() + dis) * mult);
                            ent.teleport(l2);
                        } else if (rand(1, 2) == 1) {
                            Location l2 = l.clone();
                            l2.setZ((l2.getZ() + dis) * mult);
                            ent.teleport(l2);
                        }
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                            ent.getWorld().playSound(ent.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
                            ent.teleport(l);
                        }, 15 * 20);
                    }
                }
                case "Anger" -> {
                    // Agro
                    ((IronGolem) ent).setTarget(dmgr);
                    // Special Attack
                    if (rand(1, 100) <= c) {
                        // Phase 1
                        // spawnMinions(ent,bossType,1,false);
                        spawnGodMinions(ent, ent.getLocation(), bossType, 1);
                    }
                    if (rand(1, 100) <= c / 2 && ent.getHealth() <= maxHealth / 3 * 2) {
                        // Phase 2
                        Location l = dmgr.getLocation();
                        for (int i = 0; i < 10; i++) {
                            l.setY(l.getY() + 1);
                            if ((!l.getBlock().getType().equals(Material.AIR))
                                    && (!l.getBlock().getType().equals(Material.BEDROCK)))
                                l.getBlock().breakNaturally();
                        }
                        // Entity anvil = l.getBlock().setType(Material.ANVIL);
                        FallingBlock anvil = Objects.requireNonNull(l.getWorld()).spawnFallingBlock(l, Material.ANVIL.createBlockData());
                        removeBLockList.add(anvil);
                        // BlockData b = l.getBlock().getData();
                        // l.getBlock().getState().setBlockData(null);;
                    }
                    if (rand(1, 100) <= c / 2 && ent.getHealth() <= maxHealth / 3) {
                        // Phase 3
                        Location l = dmgr.getLocation().clone();
                        l.setX(l.getX() + (rand(1, 3) - 2));
                        l.setZ(l.getZ() + (rand(1, 3) - 2));
                        if (l.getBlock().getType().equals(Material.AIR)) {
                            l.getBlock().setType(Material.STONE_PRESSURE_PLATE);
                            l.setY(l.getY() - 1);
                            l.getBlock().breakNaturally();
                            l.getBlock().setType(Material.TNT);
                        }
                    }
                }
            }
        } else if (!e.getEntity().getPassengers().isEmpty()) {
            // Mount Damage Stop
            final String bossType2 = saveFile
                    .getString("bosses." + e.getEntity().getPassengers().getFirst().getUniqueId());
            if (bossType2 != null) {
                LivingEntity boss = (LivingEntity) e.getEntity().getPassengers().getFirst();
                if (!boss.isDead()) {
                    boss.damage(e.getDamage(), e.getDamager());
                    e.setDamage(0);
                }
            }
        }
        // Item
        if ((e.getDamager() instanceof Player p) && (!(e.getEntity() instanceof Player))) {
            try {
                if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getDisplayName().equals("§5§lScythe of Death")
                        && (bossType == null || (!bossType.equals("Death")))) {
                    e.setDamage(999 * 999);
                }
            } catch (Exception x) {
            }
        }
        // Death Stop
        if (e.getEntity() instanceof Player p) {
            try {
                // System.out.println("D1");
                if ((!diedList.contains(p.getUniqueId())) && Objects.requireNonNull(p.getInventory().getItemInOffHand().getItemMeta())
                        .getDisplayName().equals("§e§lThe Cursed Skull")) {
                    // System.out.println("D2: " + p.getHealth() + " : " + e.getFinalDamage());
                    if (checkDeath(p, e.getFinalDamage())) {
                        this.getLogger().log(Level.INFO, p.getName() + " should have died.");
                        e.setCancelled(true);
                    }
                }
            } catch (Exception x) {
            }
        }
    }

    public List<Block> getArea(Location loc1, Location loc2, boolean removeBottom) {
        int topBlockX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int bottomBlockX = Math.min(loc1.getBlockX(), loc2.getBlockX());

        int topBlockY = Math.max(loc1.getBlockY(), loc2.getBlockY());
        int bottomBlockY = Math.min(loc1.getBlockY(), loc2.getBlockY());
        if (removeBottom) {
            bottomBlockY++;
        }
        int topBlockZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        int bottomBlockZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        List<Block> blocks = new ArrayList<>();
        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    blocks.add(Objects.requireNonNull(loc1.getWorld()).getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }

    private boolean checkDeath(final Player p, double fd) {
        if ((p.getHealth() - fd) <= 0) {
            // System.out.println("D3");
            p.sendMessage(getLang("curse"));
            double maxHealth = Objects.requireNonNull(p.getAttribute(Attribute.MAX_HEALTH)).getBaseValue();
            p.setHealth(maxHealth);
            p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 10 * 40, 1));
            final GameMode gm = p.getGameMode();
            p.setGameMode(GameMode.SPECTATOR);
            diedList.add(p.getUniqueId());
            // Check Death Note
            if (canEnterDeath.contains(p.getUniqueId()) && hasDeathNote(p)) {
                enterDeath(p);
            }
            // Put to Normal Mode
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                try {
                    p.setGameMode(gm);
                } catch (Exception localException) {
                }
            }, 30 * 20);
            // Reset Death Time
            final UUID id = p.getUniqueId();
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                try {
                    diedList.remove(id);
                } catch (Exception localException) {
                }
            }, 900 * 20);
            return true;
        }
        return false;
    }

    private void autoBalls(final LivingEntity ent, final String bossType) {
        if (ent.isDead())
            return;
        for (Entity x : ent.getNearbyEntities(35, 35, 35))
            if (x instanceof Player) {
                if (bossType.equals("Death")) {
                    doDragonBalls(ent, (LivingEntity) x, bossType);
                } else
                    doFireBalls(ent, (LivingEntity) x, bossType);
            }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            try {
                autoBalls(ent, bossType);
            } catch (Exception localException) {
            }
        }, 10 * 20);
    }

    private void doDragonBalls(final LivingEntity ent, final LivingEntity dmgr, final String bossType) {
        Location l = ent.getEyeLocation();
        l.setY(l.getY() + 1);
        WitherSkull f = ent.launchProjectile(WitherSkull.class);
        moveToward(f, dmgr, 0.6);
        boomTimer(f, 5);
        makeTrail(f, getConfig().getString("bosses." + bossType + ".attackParticle"));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void doFireBalls(final LivingEntity ent, final LivingEntity dmgr, final String bossType) {
        int balls = 1;
        // EntityType bt = EntityType.FIREBALL;
        Class c = Fireball.class;
        double maxHealth = Objects.requireNonNull(ent.getAttribute(Attribute.MAX_HEALTH)).getBaseValue();
        if (ent.getHealth() <= ((maxHealth / 4) * 3)) {
            // Phase 2
            balls = getConfig().getInt("bosses." + bossType + ".amountSpecial2");
            // bt = EntityType.SMALL_FIREBALL;
        } else if (ent.getHealth() <= ((maxHealth / 4) * 2)) {
            // Phase 3
            balls = getConfig().getInt("bosses." + bossType + ".amountSpecial3");
            // bt = EntityType.FIREBALL;
            c = LargeFireball.class;
        } else if ((ent.getHealth() <= (maxHealth / 4))) {
            // Phase 4
            balls = getConfig().getInt("bosses." + bossType + ".amountSpecial4");
            // bt = EntityType.DRAGON_FIREBALL;
            c = DragonFireball.class;
        }
        if (balls <= 0)
            balls = 1;
        // final EntityType fbt = bt;
        final Class fc = c;
        for (int i = 0; i < balls; i++)
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                Location l = ent.getEyeLocation();
                l.setY(l.getY() + 1);
                // Projectile f = (Projectile) ent.getWorld().spawnEntity(l, fbt);
                Projectile f = ent.launchProjectile(fc);
                moveToward(f, dmgr, 0.6);
                boomTimer(f, 5);
                makeTrail(f, getConfig().getString("bosses." + bossType + ".attackParticle"));
            }, 7L * i);
    }

    private void spawnTornado(Location l, LivingEntity target, int dmg, String type) {
        LivingEntity bat = (LivingEntity) Objects.requireNonNull(l.getWorld()).spawnEntity(l, EntityType.BAT);
        bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999 * 999, 1));
        Objects.requireNonNull(bat.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(2000);
        bat.setHealth(2000);
        moveTowardConstant(bat, target, 0.2);
        boomTimer(bat, 30);
        tornadoEffect(bat, target, dmg, type);
    }

    // private void spawnBlackTornado(Location l, LivingEntity target, int dmg) {
    // LivingEntity bat = (LivingEntity) l.getWorld().spawnEntity(l,
    // EntityType.BAT);
    // bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999*999,
    // 1));
    // bat.Attribute.GENERIC_MAX_HEALTH).setBaseValue(2000); bat.setHealth(2000);
    // moveTowardConstant(bat,target,0.2);
    // boomTimer(bat,30);
    // tornadoEffect(bat,target,dmg, "dark");
    // }
    //
    // private void spawnWhiteTornado(Location l, LivingEntity target, int dmg) {
    // LivingEntity bat = (LivingEntity) l.getWorld().spawnEntity(l,
    // EntityType.BAT);
    // bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999*999,
    // 1));
    // bat.Attribute.GENERIC_MAX_HEALTH).setBaseValue(2000); bat.setHealth(2000);
    // moveTowardConstant(bat,target,0.2);
    // boomTimer(bat,30);
    // tornadoEffect(bat,target,dmg, "wind");
    // }
    //
    // private void spawnRedTornado(Location l, LivingEntity target, int dmg) {
    // LivingEntity bat = (LivingEntity) l.getWorld().spawnEntity(l,
    // EntityType.BAT);
    // bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999*999,
    // 1));
    // bat.Attribute.GENERIC_MAX_HEALTH).setBaseValue(2000); bat.setHealth(2000);
    // moveTowardConstant(bat,target,0.2);
    // boomTimer(bat,30);
    // tornadoEffect(bat,target,dmg, "fire");
    // }

    public void tornadoEffect(final Entity e, LivingEntity to, final double dmg, final String type) {
        // Check Target
        LivingEntity to2 = to;
        if (e.isDead()) {
            return;
        } else if (to.isDead() || (!to.getWorld().equals(e.getWorld()))) {
            for (Entity x : e.getNearbyEntities(30, 30, 30))
                if (x instanceof Player) {
                    to2 = (LivingEntity) x;
                    break;
                }
        }
        // Do Effects
        for (Entity x : e.getNearbyEntities(2, 2, 2))
            if (x instanceof Player) {
                ((LivingEntity) x).damage(dmg);
                switch (type) {
                    case "wind" -> {
                        ((LivingEntity) x).addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 10 * 20, 3));
                        ((LivingEntity) x).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 20 * 20, 3));
                    }
                    case "dark" -> {
                        ((LivingEntity) x).addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 30 * 20, 2));
                        ((LivingEntity) x).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 20, 1));
                    }
                    case "fire" -> {
                        ((LivingEntity) x).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 10 * 20, 5));
                        x.setFireTicks(60 * 20);
                    }
                    case "magic" -> {
                        ((LivingEntity) x).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 10 * 20, 9));
                        ((LivingEntity) x).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 20, 0));
                        double h = ((LivingEntity) x).getHealth() - dmg;
                        if (h <= 0)
                            h = 1;
                        ((LivingEntity) x).setHealth(h);
                    }
                }
            }
        // Do Particles
        String part = switch (type) {
            case "wind" -> "CLOUD:0:5:0.6";
            case "dark" -> "SMOKE_LARGE:0:5:0.7";
            case "fire" -> "FLAME:0:5:0.7";
            default -> "DRAGON_BREATH:0:5:0.6";
        };
        Location l1 = e.getLocation();
        for (int i = 0; i <= 5; i++) {
            l1.setY(l1.getY() + 1);
            displayParticle(part, l1);
        }
        Location l2 = e.getLocation();
        for (int i = 1; i <= 4; i++) {
            l2.setY(l2.getY() - i);
            if (l2.getBlock().getType().equals(Material.AIR))
                displayParticle(part, l2);
        }
        // Finish Loop
        final LivingEntity to3 = to2;
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            try {
                tornadoEffect(e, to3, dmg, type);
            } catch (Exception localException) {
            }
        }, 5L);
    }

    private Entity getMount(Entity boss) {
        for (Entity m : boss.getNearbyEntities(4, 4, 4))
            if (!m.getPassengers().isEmpty() && m.getPassengers().getFirst().equals(boss))
                return m;
        return null;
    }

    public void suicideTimer(final Entity e, final int boomSize) {
        if (e.isDead()) {
            return;
        }
        // Check
        for (Entity ent : e.getNearbyEntities(3, 3, 3))
            if (ent instanceof Player) {
                e.getWorld().createExplosion(e.getLocation(), boomSize, true);
                e.remove();
                ent.setFireTicks(60 * 60);
                return;
            }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            try {
                suicideTimer(e, boomSize);
            } catch (Exception localException) {
            }
        }, 5);
    }

    public void moveTowardTemp(final Entity e, final Entity to, final double speed, int time) {
        if (e.isDead() || to.isDead()) {
            return;
        } else if (e.getLocation().distance(to.getLocation()) < .5)
            return;
        Location loc = to.getLocation();
        if (to instanceof LivingEntity)
            loc = ((LivingEntity) to).getEyeLocation();
        Vector direction = loc.toVector().subtract(e.getLocation().toVector()).normalize();
        e.setVelocity(direction.multiply(speed));
        // Time
        final int nt = time - 1;
        if (nt <= 0)
            return;
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            try {
                moveTowardTemp(e, to, speed, nt);
            } catch (Exception localException) {
            }
        }, 1L);
    }

    public void moveToward(final Entity e, final Entity to, final double speed) {
        if (e.isDead()) {
            return;
        } else if (e.getLocation().distance(to.getLocation()) < .5)
            return;
        Location loc = to.getLocation();
        if (to instanceof LivingEntity)
            loc = ((LivingEntity) to).getEyeLocation();
        Vector direction = loc.toVector().subtract(e.getLocation().toVector()).normalize();
        e.setVelocity(direction.multiply(speed));
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            try {
                moveToward(e, to, speed);
            } catch (Exception localException) {
            }
        }, 1L);
    }

    public void moveTowardConstant(final Entity e, final Entity to, final double speed) {
        if (e.isDead()) {
            return;
        }
        Vector direction = to.getLocation().toVector().subtract(e.getLocation().toVector()).normalize();
        e.setVelocity(direction.multiply(speed));
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            try {
                moveToward(e, to, speed);
            } catch (Exception localException) {
            }
        }, 1L);
    }

    public void moveToward(final Entity e, final Location to, final double speed) {
        if (e.isDead()) {
            return;
        } else if (e.getLocation().distance(to) < .5)
            return;
        Vector direction = to.toVector().subtract(e.getLocation().toVector()).normalize();
        e.setVelocity(direction.multiply(speed));
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            try {
                moveToward(e, to, speed);
            } catch (Exception localException) {
            }
        }, 1L);
    }

    private void boomTimer(final Entity p, int t) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            try {
                if (!p.isDead()) {
                    p.remove();
                    p.getWorld().createExplosion(p.getLocation(), 2, false);
                }
            } catch (Exception localException) {
            }
        }, t * 20L);
    }

    private void spawnGodMinions(LivingEntity ent, Location sl, String bossType, int distance) {
        // System.out.println("1: " +
        // getConfig().getString("bosses."+bossType+".minion"));
        // System.out.println("2: " +
        // EntityType.valueOf(getConfig().getString("bosses."+bossType+".minion")));
        EntityType minType = EntityType.valueOf(getConfig().getString("bosses." + bossType + ".minion"));
        EntityType mountType = null;
        int phase = 1;
        double maxHealth = Objects.requireNonNull(ent.getAttribute(Attribute.MAX_HEALTH)).getBaseValue();
        if (ent.getHealth() <= (maxHealth / 4)) {
            // Phase 4
            if (getConfig().getString("bosses." + bossType + ".minion4") != null)
                minType = EntityType.valueOf(getConfig().getString("bosses." + bossType + ".minion4"));
            if (getConfig().getString("bosses." + bossType + ".minion4Mount") != null)
                mountType = EntityType.valueOf(getConfig().getString("bosses." + bossType + ".minion4Mount"));
            phase = 4;
        } else if (ent.getHealth() <= ((maxHealth / 4) * 2)) {
            // Phase 3
            if (getConfig().getString("bosses." + bossType + ".minion3") != null)
                minType = EntityType.valueOf(getConfig().getString("bosses." + bossType + ".minion3"));
            if (getConfig().getString("bosses." + bossType + ".minion3Mount") != null)
                mountType = EntityType.valueOf(getConfig().getString("bosses." + bossType + ".minion3Mount"));
            phase = 3;
        } else if (ent.getHealth() <= ((maxHealth / 4) * 3)) {
            // Phase 2
            if (getConfig().getString("bosses." + bossType + ".minion2") != null)
                minType = EntityType.valueOf(getConfig().getString("bosses." + bossType + ".minion2"));
            if (getConfig().getString("bosses." + bossType + ".minion2Mount") != null)
                mountType = EntityType.valueOf(getConfig().getString("bosses." + bossType + ".minion2Mount"));
            phase = 2;
        } else {
            // Phase 1
            if (getConfig().getString("bosses." + bossType + ".minionMount") != null)
                mountType = EntityType.valueOf(getConfig().getString("bosses." + bossType + ".minionMount"));
        }
        // Get Amount
        int count = 0;
        for (Entity mob : ent.getNearbyEntities(35, 35, 35))
            if (mob.getType().equals(minType)) {
                count = count + 1;
            }
        if (count < getConfig().getInt("bosses." + bossType + ".maxSpecial")) {
            Location l1 = sl.clone();
            l1.setX(l1.getX() + distance);
            l1.setZ(l1.getZ() + distance);
            Location l2 = sl.clone();
            l2.setX(l2.getX() - distance);
            l2.setZ(l2.getZ() + distance);
            Location l3 = sl.clone();
            l3.setX(l3.getX() - distance);
            l3.setZ(l3.getZ() - distance);
            Location l4 = sl.clone();
            l4.setX(l4.getX() + distance);
            l4.setZ(l4.getZ() - distance);
            this.getLogger().log(Level.INFO, "Spawn Minions: " + minType);
            for (Location l : Arrays.asList(l1, l2, l3, l4)) {
                LivingEntity minion = (LivingEntity) ent.getWorld().spawnEntity(l, minType);
                switch (bossType) {
                    case "DrownedGod" -> {
                        // Armour
                        ItemStack hand = new ItemStack(Material.TRIDENT);
                        ItemStack chest = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
                        ItemStack pants = new ItemStack(Material.CHAINMAIL_LEGGINGS, 1);
                        ItemStack boots = new ItemStack(Material.CHAINMAIL_BOOTS, 1);
                        EntityEquipment ee = minion.getEquipment();
                        if (phase == 2) {
                            // ItemStack head = getHead("d5f9d66b-8c46-48d6-aaba-8f67072c9668","Fish Man
                            // Head");//"Vaporeon"
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/d88ba8bb50b79e441e47b7e452764d5fff6693779d2dadd9f7f52f98d7ea0");
                            assert ee != null;
                            ee.setHelmet(head);
                            ee.setChestplate(chest);
                            ee.setLeggings(pants);
                            ee.setBoots(boots);
                        } else if (phase == 3) {
                            // ItemStack head = getHead("b7191c02-8966-4e56-bacd-36990ad7bb27","Squid Man
                            // Head");//"MinerByTrade"
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/85ef46255c156b465dbf83c41ca145e9f57b0e87a4e6a2a143abab7f854b98");
                            assert ee != null;
                            ee.setHelmet(head);
                            chest.addEnchantment(Enchantment.PROTECTION, 2);
                            ee.setChestplate(chest);
                            pants.addEnchantment(Enchantment.PROTECTION, 2);
                            ee.setLeggings(pants);
                            boots.addEnchantment(Enchantment.PROTECTION, 2);
                            ee.setBoots(boots);
                            ee.setItemInMainHand(hand);
                        } else if (phase == 4) {
                            // ItemStack head = getHead("ac819439-ecf8-4040-aa32-48b3eb251243","Cthulhu Man
                            // Head");//"ELF_PUNSHER"
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/296343dcc59df35552f46d3ffc50ea2c4269dac139da2a581228cb3601bfe");
                            assert ee != null;
                            ee.setHelmet(head);
                            chest.addEnchantment(Enchantment.PROTECTION, 4);
                            ee.setChestplate(chest);
                            pants.addEnchantment(Enchantment.PROTECTION, 4);
                            ee.setLeggings(pants);
                            boots.addEnchantment(Enchantment.PROTECTION, 4);
                            ee.setBoots(boots);
                            ee.setItemInMainHand(hand);
                        }
                    }
                    case "PharaohGod" -> {
                        EntityEquipment ee = minion.getEquipment();
                        assert ee != null;
                        ee.setHelmetDropChance(0.0F);

                        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                        ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS, 1);
                        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
                        for (ItemStack s : Arrays.asList(chest, pants, boots))
                            dye(s, Color.BLACK);
                        if (phase == 2) {
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999 * 999, 2));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 999 * 999, 2));
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/d48935509b5dee1e0daa28f51a3cc741b2b2e18b4efa1aab5883a5378623");
                            ee.setHelmet(head);
                        } else if (phase == 3) {
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/badfc62ff19950a9afd6c23291d0b25e19c74eb922c184e5edd3255f3fad9565");
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 999 * 999, 5));
                            ee.setHelmet(head);
                            chest.addUnsafeEnchantment(Enchantment.PROTECTION, 5);
                            ee.setChestplate(chest);
                            pants.addUnsafeEnchantment(Enchantment.PROTECTION, 5);
                            ee.setLeggings(pants);
                            boots.addUnsafeEnchantment(Enchantment.PROTECTION, 5);
                            ee.setBoots(boots);
                            ItemStack hand = new ItemStack(Material.STONE_SWORD);
                            ee.setItemInMainHand(hand);
                        } else if (phase == 4) {
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/463a23a813b3d57d8964a859a7fb97ed5818279b708572d178e98252bd2b7f3d");
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 999 * 999, 6));
                            ee.setHelmet(head);
                            chest.addUnsafeEnchantment(Enchantment.PROTECTION, 7);
                            ee.setChestplate(chest);
                            pants.addUnsafeEnchantment(Enchantment.PROTECTION, 7);
                            ee.setLeggings(pants);
                            boots.addUnsafeEnchantment(Enchantment.PROTECTION, 7);
                            ee.setBoots(boots);
                            ItemStack hand = new ItemStack(Material.GOLDEN_SWORD);
                            ee.setItemInMainHand(hand);
                        } else {
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/3e91e95822fe98cc5a5658e824b1b8cf14d4de92f0e1af24815372435c9eab6");
                            ee.setHelmet(head);
                        }
                    }
                    case "AetherGod" -> {
                        EntityEquipment ee = minion.getEquipment();
                        assert ee != null;
                        ee.setHelmetDropChance(0.0F);

                        ItemStack chest = new ItemStack(Material.IRON_CHESTPLATE, 1);
                        ItemStack pants = new ItemStack(Material.IRON_LEGGINGS, 1);
                        ItemStack boots = new ItemStack(Material.IRON_BOOTS, 1);
                        ItemStack hand = new ItemStack(Material.BOW);
                        if (phase == 2) {
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999 * 999, 1));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 999 * 999, 2));
                        } else if (phase == 3) {
                            // ItemStack head = getHead("b182e3c7-1560-4573-abda-4e4b91b806e5","Sky Knight
                            // Head");//"Ryse93_YT"
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/1ab2d069a0027cda3341b5e8549ab6b214ecbc6080e1f779e7434a8e6fa253c1");
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 999 * 999, 4));
                            ee.setHelmet(head);
                            chest.addUnsafeEnchantment(Enchantment.PROTECTION, 5);
                            ee.setChestplate(chest);
                            pants.addUnsafeEnchantment(Enchantment.PROTECTION, 5);
                            ee.setLeggings(pants);
                            boots.addUnsafeEnchantment(Enchantment.PROTECTION, 5);
                            ee.setBoots(boots);
                            hand.addUnsafeEnchantment(Enchantment.POWER, 5);
                            ee.setItemInMainHand(hand);
                            makeTrail(minion, getConfig().getString("bosses." + bossType + ".minionAuraParticle"));
                            levitate(minion, true);
                            target(minion, 0.2);
                        } else if (phase == 4) {
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/a3c38235da73e12c5339ead444db8122dd63f9e8ea6a6329419cf160d3");
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 999 * 999, 5));
                            ee.setHelmet(head);
                            chest.addUnsafeEnchantment(Enchantment.PROTECTION, 7);
                            ee.setChestplate(chest);
                            pants.addUnsafeEnchantment(Enchantment.PROTECTION, 7);
                            ee.setLeggings(pants);
                            boots.addUnsafeEnchantment(Enchantment.PROTECTION, 7);
                            ee.setBoots(boots);
                            hand.addUnsafeEnchantment(Enchantment.POWER, 7);
                            ee.setItemInMainHand(hand);
                            makeTrail(minion, getConfig().getString("bosses." + bossType + ".minionAuraParticle"));
                        } else {
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 999 * 999, 4));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 999 * 999, 4));
                            noFireList.add(minion.getUniqueId());
                        }
                    }
                    case "Demon" -> {
                        EntityEquipment ee = minion.getEquipment();
                        assert ee != null;
                        ee.setHelmetDropChance(0.0F);

                        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                        ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS, 1);
                        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
                        for (ItemStack s : Arrays.asList(chest, pants, boots))
                            dye(s, Color.BLACK);
                        if (phase == 2) {
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999 * 999, 1));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 999 * 999, 2));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 999 * 999, 2));
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/7e4359dca2542729753661b44b79bcd25775d8432c72745547cf4c5af58e3a");
                            ee.setHelmet(head);
                            ItemStack hand = new ItemStack(Material.IRON_AXE);
                            hand.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 5);
                            hand.addUnsafeEnchantment(Enchantment.SHARPNESS, 5);
                            ee.setItemInMainHand(hand);
                        } else if (phase == 3) {
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/74fe948a6f7f81d9b7df6c7a7dcf66da6133f184b64f5c7068d0189a212a8b61");
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 999 * 999, 3));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 999 * 999, 3));
                            ee.setHelmet(head);
                            ItemStack hand = new ItemStack(Material.BOW);
                            hand.addUnsafeEnchantment(Enchantment.FLAME, 5);
                            hand.addUnsafeEnchantment(Enchantment.POWER, 5);
                            ee.setItemInMainHand(hand);
                            ee.setChestplate(chest);
                            ee.setLeggings(pants);
                            ee.setBoots(boots);
                        } else if (phase == 4) {
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/74fe948a6f7f81d9b7df6c7a7dcf66da6133f184b64f5c7068d0189a212a8b61");
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999 * 999, 2));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 999 * 999, 4));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 999 * 999, 4));
                            ee.setHelmet(head);
                            ItemStack hand = new ItemStack(Material.BOW);
                            hand.addUnsafeEnchantment(Enchantment.FLAME, 8);
                            hand.addUnsafeEnchantment(Enchantment.POWER, 8);
                            ee.setItemInMainHand(hand);
                            ee.setChestplate(chest);
                            ee.setLeggings(pants);
                            ee.setBoots(boots);
                        } else {
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/7e4359dca2542729753661b44b79bcd25775d8432c72745547cf4c5af58e3a");
                            ee.setHelmet(head);
                        }
                    }
                    case "Devil" -> {
                        EntityEquipment ee = minion.getEquipment();
                        assert ee != null;
                        ee.setItemInMainHandDropChance(0.0F);
                        ee.setHelmetDropChance(0.0F);
                        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                        ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS, 1);
                        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
                        if (phase == 2) {
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999 * 999, 1));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 999 * 999, 2));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 999 * 999, 3));
                            equipMob(minion, "IRON");
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/883fea591637eff42d7f62b30adb6f1fbce63641750de8b9dd933fbb26f5ae6");
                            ee.setHelmet(head);
                            ItemStack hand = new ItemStack(Material.IRON_SWORD);
                            hand.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 6);
                            hand.addUnsafeEnchantment(Enchantment.SHARPNESS, 6);
                            ee.setItemInMainHand(hand);
                        } else if (phase == 3) {
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/ccb94263f712d902dd136251fd4d8d005890c657ab5ee490ccc9bf6ec09b8f57");
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 999 * 999, 3));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 999 * 999, 3));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999 * 999, 3));
                            ee.setHelmet(head);
                            dye(chest, Color.MAROON);
                            ee.setChestplate(chest);
                            ee.setItemInMainHand(null);
                            autoBalls(minion, "Devil");
                        } else if (phase == 4) {
                            for (ItemStack s : Arrays.asList(chest, pants, boots))
                                dye(s, Color.MAROON);
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/e00cd37a4ebcbb28cb85d75bbde7b7aad5a0f42bf4842f8da77dffdea18c1356");
                            ItemStack hand = new ItemStack(Material.DIAMOND_AXE);
                            hand.addUnsafeEnchantment(Enchantment.SHARPNESS, 10);
                            hand.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
                            hand.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 10);
                            ee.setHelmet(head);
                            ee.setChestplate(chest);
                            ee.setLeggings(pants);
                            ee.setBoots(boots);
                            ee.setItemInMainHand(hand);
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999 * 999, 1));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 999 * 999, 5));
                            Objects.requireNonNull(minion.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(200);
                            minion.setHealth(200);
                            makeTrail(minion, getConfig().getString("bosses." + bossType + ".minionAuraParticle"));
                            autoBalls(minion, "Devil");
                        } else {
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/9d9d80b79442cf1a3afeaa237bd6adaaacab0c28830fb36b5704cf4d9f5937c4");
                            for (ItemStack s : Arrays.asList(chest, pants, boots))
                                dye(s, Color.RED);
                            ee.setHelmet(head);
                            ee.setChestplate(chest);
                            ee.setLeggings(pants);
                            ee.setBoots(boots);
                            ee.setItemInMainHand(null);
                        }
                    }
                    case "Death" -> {
                        EntityEquipment ee = minion.getEquipment();
                        assert ee != null;
                        ee.setHelmetDropChance(0.0F);
                        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                        if (phase == 2) {
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999 * 999, 2));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 999 * 999, 2));
                        } else if (phase == 3) {
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/4e7a68d1d52b71af013b88c8be5028ff273a209e622f505e472c6b3d9d0e9059");
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 999 * 999, 3));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999 * 999, 3));
                            ee.setHelmet(head);
                            dye(chest, Color.fromRGB(51, 0, 51));
                            ee.setChestplate(chest);
                            makeTrail(minion, getConfig().getString("bosses." + bossType + ".minionAuraParticle"));
                            levitate(minion, true);
                            target(minion, 0.4);
                            autoBalls(minion, "Death");
                        } else if (phase == 4) {
                            ItemStack head = getSkull(
                                    "http://textures.minecraft.net/texture/a459ee21aff7ed216b04bd6c486dd807d9edc99e0724ef4f4d2c4cee1a092296");
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 999 * 999, 5));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999 * 999, 3));
                            ee.setHelmet(head);
                            chest.addUnsafeEnchantment(Enchantment.PROTECTION, 10);
                            dye(chest, Color.fromRGB(127, 0, 255));
                            ee.setChestplate(chest);
                            makeTrail(minion, getConfig().getString("bosses." + bossType + ".minionAuraParticle"));
                            levitate(minion, true);
                            target(minion, 0.4);
                            autoBalls(minion, "Death");
                        } else {
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 999 * 999, 9));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 999 * 999, 3));
                        }
                    }
                    case "Anger" -> {
                        // Make Baby
                        if (minion instanceof Zombie)
                            ((Zombie) minion).setBaby();
                        // Equipment
                        EntityEquipment ee = minion.getEquipment();
                        assert ee != null;
                        ee.setHelmetDropChance(0.0F);
                        ee.setChestplateDropChance(0.0F);
                        ee.setLeggingsDropChance(0.0F);
                        ee.setBootsDropChance(0.0F);

                        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                        ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS, 1);
                        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
                        // ItemStack hand = new ItemStack(Material.BOW);
                        ItemStack head;
                        if (phase <= 2) {
                            // ItemStack head = getHead("b182e3c7-1560-4573-abda-4e4b91b806e5","Sky Knight
                            // Head");//"Ryse93_YT"
                            head = getSkull(
                                    "http://textures.minecraft.net/texture/ef4fcdff157a36d32061cb7dd0b69f7f7885fd3ddf99de471b67a84cc8677cb3");
                            // minion.addPotionEffect(new
                            // PotionEffect(PotionEffectType.INCREASE_DAMAGE,999*999,4));
                            // chest.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
                            // pants.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
                            // boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 5);
                            // hand.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 5);
                            // ee.setItemInMainHand(hand);
                            // makeTrail(minion,getConfig().getString("bosses."+bossType+".minionAuraParticle"));
                            // levitate(minion, true);
                            // target(minion, 0.2);
                        } else {
                            head = getSkull(
                                    "http://textures.minecraft.net/texture/d013ce27c241682a2361714a41bbe58fb671d00e5ea95cdc277bb53be9bef81c");
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 999 * 999, 3));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999 * 999, 1));
                            minion.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999 * 999, 1));
                            Color c = Color.fromRGB(255, 80, 0);
                            dye(chest, c);
                            dye(pants, c);
                            dye(boots, c);
                            // chest.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 7);
                            // pants.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 7);
                            // boots.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 7);
                            // hand.addUnsafeEnchantment(Enchantment.ARROW_DAMAGE, 7);
                            // ee.setItemInMainHand(hand);
                            // makeTrail(minion,getConfig().getString("bosses."+bossType+".minionAuraParticle"));
                        }
                        ee.setHelmet(head);
                        ee.setChestplate(chest);
                        ee.setLeggings(pants);
                        ee.setBoots(boots);
                    }
                }
                // Pigmen
                if (minion.getType().equals(EntityType.PIGLIN)) {
                    assert minion instanceof PigZombie;
                    ((PigZombie) minion).setAngry(true);
                    ((PigZombie) minion).setAnger(999 * 999);
                }
                // Mount
                if (mountType != null) {
                    LivingEntity m = (LivingEntity) ent.getWorld().spawnEntity(l, mountType);
                    m.addPassenger(minion);
                    if (m.getType().equals(EntityType.BAT)) {
                        m.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999 * 999, 1));
                        m.setInvulnerable(true);
                    } else if (m.getType().equals(EntityType.PHANTOM)) {
                        m.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999 * 999, 1));
                        noFireList.add(m.getUniqueId());
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEnttityTarget(EntityTargetEvent e) {
        // Attacker Boss
        Entity atkr = e.getEntity();
        {
            String bossType = saveFile.getString("bosses." + atkr.getUniqueId());
            if (bossType != null) {
                if (bossType.equals("Anger")) {
                    if (!(e.getTarget() instanceof Player)) {
                        e.setCancelled(true);
                        int r = 12;
                        for (Entity i : atkr.getNearbyEntities(r, r, r))
                            if (i instanceof Player)
                                ((IronGolem) atkr).setTarget(((Player) i));
                    }
                }
            }
        }
        // Target Boss
        Entity trgt = e.getTarget();
        if (trgt != null) {
            String bossType = saveFile.getString("bosses." + trgt.getUniqueId());
            if (bossType != null) {
                if (bossType.equals("Anger")) {
                    e.setCancelled(true);
                }
            }
        }
    }

    public ItemStack getSkull(String url) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (url.isEmpty())
            return head;

        SkullMeta headMeta = (SkullMeta) head.getItemMeta();

        try {
            // Create a player profile with random UUID
            PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID(), null);
            PlayerTextures textures = profile.getTextures();

            // Set the skin URL
            textures.setSkin(new URL(url));
            profile.setTextures(textures);

            // Apply the profile to the skull
            assert headMeta != null;
            headMeta.setOwnerProfile(profile);
            head.setItemMeta(headMeta);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return head;
    }

    private void spawnMinions(LivingEntity ent, String bossType, int distance, boolean mount) {
        // Get Type
        EntityType mt;
        EntityType mountType = null;
        double maxHealth = Objects.requireNonNull(ent.getAttribute(Attribute.MAX_HEALTH)).getBaseValue();
        if (getConfig().getString("bosses." + bossType + ".minion3") != null
                && (ent.getHealth() <= ((maxHealth / 3)))) {
            mt = EntityType.valueOf(getConfig().getString("bosses." + bossType + ".minion3"));
            if (getConfig().getString("bosses." + bossType + ".minion3Mount") != null)
                mountType = EntityType.valueOf(getConfig().getString("bosses." + bossType + ".minion3Mount"));
        } else if (getConfig().getString("bosses." + bossType + ".minion2") != null
                && (ent.getHealth() <= ((maxHealth / 3) * 2))) {
            mt = EntityType.valueOf(getConfig().getString("bosses." + bossType + ".minion2"));
            if (getConfig().getString("bosses." + bossType + ".minion2Mount") != null)
                mountType = EntityType.valueOf(getConfig().getString("bosses." + bossType + ".minion2Mount"));
        } else {
            mt = EntityType.valueOf(getConfig().getString("bosses." + bossType + ".minion"));
            if (getConfig().getString("bosses." + bossType + ".minionMount") != null)
                mountType = EntityType.valueOf(getConfig().getString("bosses." + bossType + ".minionMount"));
        }
        // Get Amount
        int count = 0;
        for (Entity mob : ent.getNearbyEntities(25, 25, 25))
            if (mob.getType().equals(mt)) {
                count = count + 1;
            }
        if (count < getConfig().getInt("bosses." + bossType + ".maxSpecial")) {
            Location l1 = ent.getLocation();
            l1.setX(l1.getX() + distance);
            l1.setZ(l1.getZ() + distance);
            Location l2 = ent.getLocation();
            l2.setX(l2.getX() - distance);
            l2.setZ(l2.getZ() + distance);
            Location l3 = ent.getLocation();
            l3.setZ(l3.getZ() - distance);
            for (Location l : Arrays.asList(l1, l2, l3)) {
                Entity minion = ent.getWorld().spawnEntity(l, mt);
                // System.out.println("Spawn Minnion: "+ minion.getType());
                // Type Edits
                if ((minion instanceof Slime) && bossType.equals("KingSlime")) {
                    ((Slime) minion).setSize(2);
                } else if (minion.getType().equals(EntityType.RABBIT) && bossType.equals("KillerBunny")) {
                    ((Rabbit) minion).setRabbitType(Type.THE_KILLER_BUNNY);
                } else if (minion.getType().equals(EntityType.ZOMBIE) && bossType.equals("ZombieKing")) {
                    ((Zombie) minion).setBaby();
                    ItemStack helm = new ItemStack(Material.CHAINMAIL_HELMET, 1);
                    ItemStack chest = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
                    ItemStack sword = new ItemStack(Material.IRON_SWORD, 1);
                    sword.addUnsafeEnchantment(Enchantment.SHARPNESS, 4);
                    EntityEquipment ee = ((LivingEntity) minion).getEquipment();
                    assert ee != null;
                    ee.setHelmetDropChance(0.0F);
                    ee.setChestplateDropChance(0.0F);
                    ee.setItemInMainHandDropChance(0.0F);
                    ee.setHelmet(helm);
                    ee.setChestplate(chest);
                    ee.setItemInMainHand(sword);
                } else if (bossType.equals("PapaPanda")) {
                    ((Panda) minion).setBaby();
                    ((Panda) minion).setMainGene(Gene.AGGRESSIVE);
                } else if (bossType.equals("Giant")) {
                    ItemStack helm = new ItemStack(Material.IRON_HELMET, 1);
                    ItemStack chest = new ItemStack(Material.IRON_CHESTPLATE, 1);
                    ItemStack leggings = new ItemStack(Material.IRON_LEGGINGS, 1);
                    ItemStack boots = new ItemStack(Material.IRON_BOOTS, 1);
                    ItemStack sword = new ItemStack(Material.IRON_SWORD, 1);
                    sword.addUnsafeEnchantment(Enchantment.SHARPNESS, 5);
                    EntityEquipment ee = ((LivingEntity) minion).getEquipment();
                    assert ee != null;
                    ee.setHelmetDropChance(0.0F);
                    ee.setChestplateDropChance(0.0F);
                    ee.setItemInMainHandDropChance(0.0F);
                    if (ent.getHealth() <= ((maxHealth / 3))) {
                        ee.setHelmet(helm);
                        ee.setChestplate(chest);
                        ee.setLeggings(leggings);
                        ee.setBoots(boots);
                    } else if (ent.getHealth() <= ((maxHealth / 3) * 2)) {
                        ee.setHelmet(helm);
                        ee.setChestplate(chest);
                        ee.setLeggings(leggings);
                        ee.setBoots(boots);
                        ee.setItemInMainHand(sword);
                    }
                }
                // Mount
                if (mount && mountType != null) {
                    Entity m = ent.getWorld().spawnEntity(l, mountType);
                    m.addPassenger(minion);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onEntityDeath(EntityDeathEvent e) {
        Entity ent = e.getEntity();
        // Boss Death
        final String bossType = saveFile.getString("bosses." + ent.getUniqueId());
        if (bossType != null) {
            // ArrayList<ItemStack> loot = new ArrayList<ItemStack>();
            // Boss Death
            for (Player p : bossMap.get(ent).getPlayers())
                bossMap.get(ent).removePlayer(p);
            bossMap.remove(ent);
            // Mount Removal
            LivingEntity m = (LivingEntity) getMount(ent);
            if (m != null) {
                // System.out.println("Found Mount");
                // m.removePassenger(ent);
                // if(m.isInvulnerable()) {
                // System.out.println("Remove");
                m.remove();
                // }else {System.out.println("Damage");
                // m.damage(9999*9999);
                // }
            }
            // Drops
            // System.out.println("DXP: " + e.getDroppedExp());
            e.setDroppedExp(getConfig().getInt("bosses." + bossType + ".dropedXP"));
            // Set Chest
            Location loc = ent.getLocation();
            if (loc.getY() > Objects.requireNonNull(loc.getWorld()).getMaxHeight())
                loc.setY(loc.getWorld().getMaxHeight() - 1);
            loc.getBlock().setType(Material.CHEST);
            Chest c = (Chest) loc.getBlock().getState();
            // Get Loot
            ItemStack s = getLoot(e.getEntity().getKiller(), bossType);
            c.getInventory().addItem(s);

            this.getLogger().log(Level.INFO, "Boss " + bossType + " has died!");
            // ent.getWorld().dropItemNaturally(ent.getLocation(),s);
            // ParticleEffects_1_9.sendToLocation(ParticleEffects_1_9.CLOUD,
            // ent.getLocation(), 0, 0, 0, 1, 50);
            displayParticle(Objects.requireNonNull(getConfig().getString("bosses." + bossType + ".deathParticle")), ent.getLocation());
            // God Deaths
            switch (bossType) {
                case "AetherGod" -> {
                    saveFile.set("aetherGodDeaths", saveFile.getInt("aetherGodDeaths") + 1);
                    save();
                }
                case "PharaohGod" -> {
                    saveFile.set("pharaohGodDeaths", saveFile.getInt("pharaohGodDeaths") + 1);
                    save();
                }
                case "DrownedGod" -> {
                    saveFile.set("drownedGodDeaths", saveFile.getInt("drownedGodDeaths") + 1);
                    save();
                }
            }
        }
        // Fire List Remove
        noFireList.remove(e.getEntity().getUniqueId());
        // Material.DIAMOND_BLOCK
        // Material.SLIME_BLOCK
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onBlockLand(EntityChangeBlockEvent e) {
        if (removeBLockList.contains(e.getEntity())) {
            removeBLockList.remove(e.getEntity());
            e.setCancelled(true);
            e.getEntity().remove();
            System.out.println("Block Cancel Land");
        }
    }

    private String getStaffMode(Player p) {
        for (ItemStack s : p.getInventory())
            if (s != null)
                try {
                    if (Objects.requireNonNull(s.getItemMeta()).getDisplayName().equals(getBossItemName("AetherGod", 1))) {
                        String[] sp = Objects.requireNonNull(s.getItemMeta().getLore()).getFirst().split(": ");
                        return sp[1].trim();
                    }
                } catch (Exception x) {
                }
        return null;
    }

    private boolean hasDeathNote(Player p) {
        try {
            if (hadDeathNote.contains(p.getUniqueId())) {
                hadDeathNote.remove(p.getUniqueId());
                return true;
            }
            for (ItemStack s : p.getInventory())
                if (s != null)
                    try {
                        // System.out.println(s.getItemMeta().getDisplayName() + " == " +
                        // getDeathItem().getItemMeta().getDisplayName());
                        if (Objects.requireNonNull(s.getItemMeta()).getDisplayName().equals(Objects.requireNonNull(getDeathItem().getItemMeta()).getDisplayName())) {
                            if (s.getAmount() > 1) {
                                s.setAmount(s.getAmount() - 1);
                            } else
                                p.getInventory().remove(s);
                            return true;
                        }
                    } catch (Exception x) {
                    }
        } catch (Exception x) {
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSlimeSplit(SlimeSplitEvent e) {
        Entity ent = e.getEntity();
        final String bossType = saveFile.getString("bosses." + ent.getUniqueId());
        if (bossType != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onBlockPlace(BlockPlaceEvent e) {
        // Player p = e.getPlayer();
        try {
            if (Objects.requireNonNull(e.getItemInHand().getItemMeta()).getDisplayName().equals(getBossItemName("PharaohGod", 0))) {
                e.setCancelled(true);
            } else if (e.getItemInHand().getItemMeta().getDisplayName().equals(getBossItemName("AetherGod", 1))) {
                e.setCancelled(true);
            } else if (e.getItemInHand().getItemMeta().getDisplayName().equals(getLang("items.deathnote"))) {
                e.setCancelled(true);
            }
            // if(p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§e§lThe
            // Cursed Skull") ||
            // p.getInventory().getItemInOffHand().getItemMeta().getDisplayName().equals("§e§lThe
            // Cursed Skull")) {
            // e.setCancelled(true);
            // }
        } catch (Exception x) {
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteract(PlayerInteractEvent e) {
        final Player p = e.getPlayer();
        try {
            assert e.getClickedBlock() != null;
            Location l = e.getClickedBlock().getLocation();
            // Boss Rituals
            if (p.getInventory().getItemInMainHand().getType().equals(Material.ENDER_EYE)
                    && Objects.requireNonNull(l.getWorld()).getEnvironment().equals(Environment.NORMAL) && l.getWorld()
                            .getBiome((int) l.getX(), (int) l.getY(), (int) l.getZ()).toString().contains("SWAMP")) {
                if (checkBlockRecipe(l, "SLIME_BLOCK:SLIME_BLOCK:SLIME_BLOCK", "SLIME_BLOCK:DIAMOND_BLOCK:SLIME_BLOCK",
                        "SLIME_BLOCK:SLIME_BLOCK:SLIME_BLOCK", true)) {
                    e.setCancelled(true);
                    l.getWorld().createExplosion(l, 3, false);
                    l.setY(l.getY() + 2);
                    final Location bs = l.clone();
                    takeItem(p, 1);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> spawnBoss(p, bs, "KingSlime"),
                            (40));
                }
            } else if (p.getInventory().getItemInMainHand().getType().equals(Material.MAGMA_CREAM)
                    && Objects.requireNonNull(l.getWorld()).getEnvironment().equals(Environment.NETHER)) {
                if (checkBlockRecipe(l, "FIRE:FIRE:FIRE", "FIRE:WITHER_SKELETON_SKULL:FIRE", "FIRE:FIRE:FIRE", true)) {
                    e.setCancelled(true);
                    l.getWorld().createExplosion(l, 2, true);
                    final Location bs = l.clone();
                    takeItem(p, 1);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this,
                            () -> spawnBoss(p, bs, "WitherSkeletonKing"), (20));
                }
            } else if (p.getInventory().getItemInMainHand().getType().equals(Material.CAKE)
                    && Objects.requireNonNull(l.getWorld()).getEnvironment().equals(Environment.NORMAL) && (l.getWorld()
                            .getBiome((int) l.getX(), (int) l.getY(), (int) l.getZ()).toString().contains("BAMBOO"))) {
                if (checkBlockRecipe(l, "MOSSY_COBBLESTONE:MOSSY_COBBLESTONE:MOSSY_COBBLESTONE",
                        "MOSSY_COBBLESTONE:CHISELED_STONE_BRICKS:MOSSY_COBBLESTONE",
                        "MOSSY_COBBLESTONE:MOSSY_COBBLESTONE:MOSSY_COBBLESTONE", true)) {
                    // BAMBOO
                    e.setCancelled(true);
                    l.getWorld().createExplosion(l, 2, false);
                    final Location bs = l.clone();
                    takeItem(p, 1);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> spawnBoss(p, bs, "PapaPanda"),
                            (20));
                }
            } else if (p.getInventory().getItemInMainHand().getType().equals(Material.JACK_O_LANTERN)
                    && Objects.requireNonNull(l.getWorld()).getEnvironment().equals(Environment.NORMAL) && (l.getY() <= -40)) {
                if (checkBlockRecipe(l, "COPPER_BLOCK:COPPER_BLOCK:COPPER_BLOCK",
                        "COPPER_BLOCK:COPPER_BLOCK:COPPER_BLOCK", "COPPER_BLOCK:COPPER_BLOCK:COPPER_BLOCK", true)) {
                    // COPPER GOLEM
                    e.setCancelled(true);
                    // l.getWorld().createExplosion(l, 3, false);
                    l.getWorld().playSound(l, Sound.ENTITY_IRON_GOLEM_REPAIR, 1, 1);
                    final Location bs = l.clone();
                    takeItem(p, 1);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> spawnBoss(p, bs, "Anger"),
                            (20));
                }
            } else if (p.getInventory().getItemInMainHand().getType().equals(Material.BRAIN_CORAL)
                    && Objects.requireNonNull(l.getWorld()).getEnvironment().equals(Environment.NORMAL) && (l.getWorld()
                            .getBiome((int) l.getX(), (int) l.getY(), (int) l.getZ()).toString().contains("PLAINS"))) {
                if (checkBlockRecipe(l, "SOUL_SAND:SOUL_SAND:SOUL_SAND", "SOUL_SAND:EMERALD_BLOCK:SOUL_SAND",
                        "SOUL_SAND:SOUL_SAND:SOUL_SAND", true)) {
                    e.setCancelled(true);
                    l.getWorld().createExplosion(l, 2, false);
                    final Location bs = l.clone();
                    takeItem(p, 1);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this,
                            () -> spawnBoss(p, bs, "ZombieKing"), (20));
                }
            } else if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getDisplayName().equals("§5§lBook of Spells")
                    && Objects.requireNonNull(l.getWorld()).getEnvironment().equals(Environment.NORMAL) && (l.getWorld()
                            .getBiome((int) l.getX(), (int) l.getY(), (int) l.getZ()).toString().contains("SNOWY"))) {
                if (checkBlockRecipe(l, "REDSTONE_TORCH:REDSTONE_WIRE:REDSTONE_TORCH",
                        "REDSTONE_WIRE:CAMPFIRE:REDSTONE_WIRE", "REDSTONE_TORCH:REDSTONE_WIRE:REDSTONE_TORCH", true)) {
                    e.setCancelled(true);
                    lightningShow(l, 2);
                    takeItem(p, 1);
                    final Location bs = l.clone();
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this,
                            () -> spawnBoss(p, bs, "EvilWizard"), (20));
                }
            } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName().equals("§6§lBell of Doom")
                    && Objects.requireNonNull(l.getWorld()).getEnvironment().equals(Environment.NORMAL) && (l.getWorld()
                            .getBiome((int) l.getX(), (int) l.getY(), (int) l.getZ()).toString().contains("SAVANNA"))) {
                if (checkBlockRecipe(l, "REDSTONE_TORCH:REDSTONE_WIRE:REDSTONE_TORCH",
                        "REDSTONE_WIRE:CAMPFIRE:REDSTONE_WIRE", "REDSTONE_TORCH:REDSTONE_WIRE:REDSTONE_TORCH", true)) {
                    e.setCancelled(true);
                    lightningShow(l, 3);
                    takeItem(p, 1);
                    final Location bs = l.clone();
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this,
                            () -> spawnBoss(p, bs, "IllagerKing"), (20));
                }
            } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName()
                    .equals("§2§lPotion of Giant Growth") && Objects.requireNonNull(l.getWorld()).getEnvironment().equals(Environment.NORMAL)
                    && (l.getWorld().getBiome((int) l.getX(), (int) l.getY(), (int) l.getZ()).toString()
                            .contains("PLAINS"))) {
                if (checkBlockRecipe(l, "REDSTONE_TORCH:REDSTONE_WIRE:REDSTONE_TORCH",
                        "REDSTONE_WIRE:CAMPFIRE:REDSTONE_WIRE", "REDSTONE_TORCH:REDSTONE_WIRE:REDSTONE_TORCH", true)) {
                    e.setCancelled(true);
                    // boom(l,5,false);
                    lightningShow(l, 5);
                    takeItem(p, 1);
                    final Location bs = l.clone();
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> spawnBoss(p, bs, "Giant"),
                            (20));
                }
            } else if (p.getInventory().getItemInMainHand().getType().equals(Material.NETHER_STAR)) {
                if (checkBlockRecipe(l, "REDSTONE_TORCH:REDSTONE_WIRE:REDSTONE_TORCH",
                        "REDSTONE_WIRE:CAMPFIRE:REDSTONE_WIRE", "REDSTONE_TORCH:REDSTONE_WIRE:REDSTONE_TORCH", true)) {
                    e.setCancelled(true);
                    if (godsDead()) {
                        takeItem(p, 1);
                        p.getWorld().createExplosion(e.getClickedBlock().getLocation(), 4);
                        final Location bs = l.clone();
                        p.sendMessage("§c§lFool, who dares summon me!");
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> spawnBoss(p, bs, "Demon"),
                                (10));
                    } else
                        p.sendMessage(getLang("noPower"));
                }
            } else if (p.getInventory().getItemInMainHand().getType().equals(Material.GOLD_INGOT)
                    && p.getInventory().getItemInMainHand().getAmount() >= 16) {
                // Ghast Spawn
                // l.setY(l.getY()+1);
                if (Objects.requireNonNull(l.getWorld()).getEnvironment().equals(Environment.NETHER) && checkBlockRecipe(l,
                        "REDSTONE_WIRE:REDSTONE_WIRE:REDSTONE_WIRE", "REDSTONE_WIRE:MAGMA_BLOCK:REDSTONE_WIRE",
                        "REDSTONE_WIRE:REDSTONE_WIRE:REDSTONE_WIRE", false)) {
                    if (!l.getBlock().getType().equals(Material.BEDROCK))
                        l.getBlock().setType(Material.AIR);
                    boom(l, 5, true);
                    e.setCancelled(true);
                    takeItem(p, 16);
                    final Location bs = l.clone();
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> spawnBoss(p, bs, "GhastLord"),
                            (40));
                }
            } else if (p.getInventory().getItemInMainHand().getType().equals(Material.GOLDEN_CARROT)
                    && p.getInventory().getItemInMainHand().getAmount() >= 16) {
                // this.getLogger().log(Level.WARNING, "Gold Consumed!");
                // l.setY(l.getY()+1);
                if (Objects.requireNonNull(l.getWorld()).getBiome((int) l.getX(), (int) l.getY(), (int) l.getZ()).toString().contains("DESERT"))
                    if (l.getWorld().getEnvironment().equals(Environment.NORMAL)
                            && checkBlockRecipe(l, "CARROTS:CARROTS:CARROTS", "CARROTS:MAGMA_BLOCK:CARROTS",
                                    "CARROTS:CARROTS:CARROTS", false)) {
                        // this.getLogger().log(Level.WARNING, "Correct condition found for Killer
                        // Bunny!");
                        e.setCancelled(true);
                        takeItem(p, 16);
                        // boom(l,2,false);
                        // Location la = l.clone();
                        // la.setY(la.getY()-1);
                        if (!l.getBlock().getType().equals(Material.BEDROCK))
                            l.getBlock().setType(Material.AIR);
                        l.getWorld().strikeLightning(l);
                        final Location bs = l.clone();
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this,
                                () -> spawnBoss(p, bs, "KillerBunny"), (40));
                    }
            }
            // System.out.println("Is Shard: " +
            // isShard(p.getInventory().getItemInMainHand()));
        } catch (Exception x) {
        }
        try {
            // Cool Check
            if (coolList.contains(p.getUniqueId())) {
                return;
            }
            // Boss Items
            if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getDisplayName()
                    .equals(getBossItemName("EvilWizard", 3))) {
                // Magic Book Use
                if (p.getBedSpawnLocation() != null) {
                    // Take Book
                    int took = 0;
                    int count = 5;
                    for (ItemStack s : p.getInventory())
                        if (s != null && s.getType().equals(Material.BOOK)) {
                            if (s.getAmount() > count) {
                                s.setAmount(s.getAmount() - count);
                                took = count;
                                break;
                            } else {
                                took = took + s.getAmount();
                                p.getInventory().remove(s);
                                if (took >= count)
                                    break;
                            }
                        }
                    if (took >= count) {
                        p.teleport(p.getBedSpawnLocation());
                        p.getWorld().playSound(p.getLocation(), Sound.ITEM_BOOK_PAGE_TURN, 1, 1);
                        p.sendMessage(getLang("bed"));
                        cool(p.getUniqueId());
                    } else {
                        p.sendMessage(getLang("books"));
                        if (took > 0)
                            p.getInventory().addItem(new ItemStack(Material.BOOK, took));
                    }
                } else
                    p.sendMessage(getLang("noBed"));
            } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName()
                    .equals(getBossItemName("DrownedGod", 1))) {
                if ((!p.getLocation().getBlock().getType().equals(Material.WATER)) && (!p.getWorld().hasStorm())) {
                    getServer().dispatchCommand(Bukkit.getConsoleSender(), "weather rain");
                    cool(p.getUniqueId());
                }
            } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName()
                    .equals(getBossItemName("PharaohGod", 1))) {
                Projectile f = p.launchProjectile(LargeFireball.class);
                makeTrail(f, "SMOKE_LARGE:0:4:0.2");
                cool(p.getUniqueId());
            } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName()
                    .equals(getBossItemName("AetherGod", 1))) {
                String mode = getStaffMode(p);
                if (p.isSneaking()) {
                    // Mode Change
                    // System.out.println("Mode: " + mode + " - " + mode.equals("Shard"));
                    String nMode = "Shard";
                    assert mode != null;
                    if (mode.equals("Shard")) {
                        nMode = "Loot";
                        // System.out.println("Loot");
                    } else if (mode.equals("Loot")) {
                        nMode = "Control";
                        // System.out.println("Control");
                    }
                    ItemStack s = p.getInventory().getItemInMainHand().clone();
                    ItemMeta m = s.getItemMeta();
                    assert m != null;
                    List<String> lore = m.getLore();
                    assert lore != null;
                    lore.set(0, "§8Mode: " + nMode);
                    m.setLore(lore);
                    s.setItemMeta(m);
                    p.getInventory().setItemInMainHand(s);
                    p.sendMessage(getLang("modeChange") + nMode);
                } else {
                    controlCheck(p);
                }
                cool(p.getUniqueId());
            } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName()
                    .equals(getLang("items.knowledgebook"))) {
                openKnowledgBook(p);
            } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName()
                    .equals(getLang("items.deathnote"))) {
                p.sendMessage(getLang("deathQ"));
                deathList.add(p.getUniqueId());
                cool(p.getUniqueId());
            }
        } catch (Exception x) {
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player p = e.getPlayer();
        if (deathList.contains(p.getUniqueId())) {
            deathList.remove(p.getUniqueId());
            e.setCancelled(true);
            final Player vic = getServer().getPlayer(e.getMessage());
            if (vic != null) {
                // Check
                if (p.getUniqueId().equals(vic.getUniqueId())) {
                    canEnterDeath.add(p.getUniqueId());
                }
                // Kill
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> vic.damage(999 * 999), 5L);
                // Consume Chance
                if (rand(1, 100) <= getConfig().getInt("deathNoteConsumeChance")) {
                    p.getInventory().setItemInMainHand(null);
                    p.sendMessage(getLang("noteConsume"));
                    hadDeathNote.add(p.getUniqueId());
                }
                p.sendMessage(getLang("deathPass"));
            } else
                p.sendMessage(getLang("deathFail"));
        }
    }

    private void openKnowledgBook(Player p) {
        Inventory inv = getServer().createInventory(p, 9, getLang("items.knowledgebook"));
        inv.setItem(2, getItem(Material.ENCHANTED_BOOK, "§eEnchant", 1, null));
        inv.setItem(6, getItem(Material.BOOK, "§cDisEnchant", 1, null));
        for (int i : Arrays.asList(0, 1, 3, 5, 7, 8))
            inv.setItem(i, getItem(Material.GRAY_STAINED_GLASS_PANE, "§l", 1, null));
        p.openInventory(inv);
    }

    private void openEnchantGUI(Player p, ItemStack s) {
        Inventory inv = getServer().createInventory(p, 54, "§0§lAdd Enchantments");
        for (int i : Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8))
            inv.setItem(i, getItem(Material.GRAY_STAINED_GLASS_PANE, "§l", 1, null));
        inv.setItem(4, s);
        for (Enchantment c : Enchantment.values()) {
            if (c != null) {
                c.getKey();
                if (!c.equals(Enchantment.MENDING)) {
                    ItemStack b = (getItem(Material.ENCHANTED_BOOK, "§eAdd Enchantment", 1, null));
                    b.addUnsafeEnchantment(c, 1);
                    inv.addItem(b);
                }
            }
        }
        inv.setItem(49, getItem(Material.EXPERIENCE_BOTTLE, "§eLevel: 1", 1, null));
        inv.setItem(53, getItem(Material.BARRIER, "§cDone", 1, null));
        p.openInventory(inv);
    }

    private void openDisEnchantGUI(Player p, ItemStack s) {
        Inventory inv = getServer().createInventory(p, 54, "§0§lRemove Enchantments");
        for (int i : Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8))
            inv.setItem(i, getItem(Material.GRAY_STAINED_GLASS_PANE, "§l", 1, null));
        inv.setItem(4, s);
        for (Map.Entry<Enchantment, Integer> hm : s.getEnchantments().entrySet()) {
            ItemStack b = (getItem(Material.ENCHANTED_BOOK, "§eRemove Enchantment", 1, null));
            b.addUnsafeEnchantment(hm.getKey(), hm.getValue());
            inv.addItem(b);
        }
        inv.setItem(53, getItem(Material.BARRIER, "§cDone", 1, null));
        p.openInventory(inv);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerClickInv(InventoryClickEvent e) {
        String name = e.getView().getTitle();
        Player p = (Player) e.getWhoClicked();
        try {
            String n = Objects.requireNonNull(Objects.requireNonNull(e.getCurrentItem()).getItemMeta()).getDisplayName();
            if (name.contains(getLang("items.knowledgebook"))) {
                switch (n) {
                    case "§eEnchant" -> {
                        e.setCancelled(true);
                        noList.add(p.getUniqueId());
                        p.closeInventory();
                        ItemStack s = e.getView().getItem(4);
                        noList.remove(p.getUniqueId());
                        if (s != null) {
                            openEnchantGUI(p, s);
                        } else
                            p.sendMessage(getLang("noItem"));
                    }
                    case "§cDisEnchant" -> {
                        e.setCancelled(true);
                        noList.add(p.getUniqueId());
                        p.closeInventory();
                        ItemStack s = e.getView().getItem(4);
                        noList.remove(p.getUniqueId());
                        if (s != null) {
                            openDisEnchantGUI(p, s);
                        } else
                            p.sendMessage(getLang("noItem"));
                    }
                    case "§l" -> e.setCancelled(true);
                }
            } else if (name.contains("§0§lAdd Enchantments") || name.contains("§0§lRemove Enchantments")) {
                if (n.equals("§eAdd Enchantment")) {
                    e.setCancelled(true);
                    int lvl = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(e.getView().getItem(49)).getItemMeta())).getDisplayName().split(": ")[1]);
                    int need = 3 + (lvl);
                    if (p.getLevel() >= need) {
                        p.setLevel(p.getLevel() - need);
                        p.getWorld().playSound(p.getLocation(), Sound.BLOCK_ENCHANTMENT_TABLE_USE, 1, 1);
                        ItemStack s = e.getView().getItem(4);
                        assert s != null;
                        EnchantmentStorageMeta sMeta = (EnchantmentStorageMeta) e.getCurrentItem().getItemMeta();
                        for (Map.Entry<Enchantment, Integer> hm : sMeta.getStoredEnchants().entrySet()) {
                            s.addUnsafeEnchantment(hm.getKey(), lvl);
                        }
                    } else {
                        p.sendMessage(getLang("needXP").replace("<xp>", need + ""));
                        p.closeInventory();
                    }
                } else if (n.equals("§eRemove Enchantment")) {
                    e.setCancelled(true);
                    // e.getCurrentItem()
                    p.getWorld().playSound(p.getLocation(), Sound.BLOCK_GRINDSTONE_USE, 1, 1);
                    ItemStack s = e.getView().getItem(4);
                    assert s != null;
                    EnchantmentStorageMeta sMeta = (EnchantmentStorageMeta) e.getCurrentItem().getItemMeta();
                    for (Map.Entry<Enchantment, Integer> hm : sMeta.getStoredEnchants().entrySet()) {
                        s.removeEnchantment(hm.getKey());
                    }
                    noList.add(p.getUniqueId());
                    p.closeInventory();
                    openDisEnchantGUI(p, s);
                    noList.remove(p.getUniqueId());
                } else if (n.contains("§eLevel: ")) {
                    e.setCancelled(true);
                    // ItemStack s = e.getView().getItem(49);
                    int i = Integer.parseInt(Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(e.getView().getItem(49))).getItemMeta()).getDisplayName().split(": ")[1]) + 1;
                    int maxLevel = getConfig().getInt("maxKBEnchantLevel", 10);
                    if (i > maxLevel)
                        i = 1;
                    e.getView().setItem(49, getItem(Material.EXPERIENCE_BOTTLE, "§eLevel: " + i, 1, null));
                } else if (n.equals("§cDone")) {
                    e.setCancelled(true);
                    p.closeInventory();
                } else if (n.equals("§l")) {
                    e.setCancelled(true);
                }
            }
        } catch (Exception x) {
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerCloseInv(InventoryCloseEvent e) {
        String name = e.getView().getTitle();
        Player p = (Player) e.getPlayer();
        try {
            if (name.contains(getLang("items.knowledgebook")) || name.contains("§0§lAdd Enchantments")
                    || name.contains("§0§lRemove Enchantments")) {
                if (!noList.contains(p.getUniqueId())) {
                    ItemStack s = e.getView().getItem(4);
                    p.getInventory().addItem(s);
                }
            }
        } catch (Exception x) {
        }
    }

    private boolean godsDead() {
        return saveFile.getInt("drownedGodDeaths") > 0 && saveFile.getInt("pharaohGodDeaths") > 0
                && saveFile.getInt("aetherGodDeaths") > 0;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerInteractEnt(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        try {
            // Cool Check
            if (coolList.contains(p.getUniqueId())) {
                return;
            }
            // Boss Item
            controlCheck(p);
        } catch (Exception x) {
        }
    }

    private void controlCheck(Player p) {
        if ((!p.isSneaking()) && Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getDisplayName()
                .equals(getBossItemName("AetherGod", 1))) {
            String mode = getStaffMode(p);
            assert mode != null;
            if (mode.equals("Control")) {
                // Control
                Entity ce = getControling(p);
                // System.out.println("CE: " + ce);
                if (ce != null) {
                    // System.out.println("CM Removed");
                    controlMap.remove(ce);
                } else {
                    Entity ne = getTarget(p);
                    if (ne != null) {
                        controlMap.put(ne, p.getUniqueId());
                        moveControl(ne, p, 0.7);
                    }
                }
                cool(p.getUniqueId());
            }
        }
    }

    public void moveControl(final Entity e, final Player owner, final double speed) {
        if (e == null || owner == null || e.isDead() || owner.isDead() || (!controlMap.containsKey(e))) {
            return;
        }
        // System.out.println(controlMap.containsKey(e));
        // Location to = owner.getTargetBlock(null, 5).getLocation();
        Location to = owner.getLocation().add(owner.getLocation().getDirection().normalize().multiply(5));
        Vector direction = to.toVector().subtract(e.getLocation().toVector()).normalize();
        e.setVelocity(direction.multiply(speed));
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            try {
                moveControl(e, owner, speed);
            } catch (Exception localException) {
            }
        }, 1L);
    }

    private Entity getControling(Player p) {
        for (Map.Entry<Entity, UUID> hm : controlMap.entrySet()) {
            if (hm.getValue().equals(p.getUniqueId()))
                return hm.getKey();
        }
        return null;
    }

    private Entity getTarget(final Player player) {

        BlockIterator iterator = new BlockIterator(player.getWorld(), player
                .getLocation().toVector(),
                player.getEyeLocation()
                        .getDirection(),
                0, 100);
        while (iterator.hasNext()) {
            Block item = iterator.next();
            for (Entity entity : player.getNearbyEntities(100, 100, 100))
                if ((entity instanceof LivingEntity) && (!entity.isDead())) {
                    int acc = 2;
                    for (int x = -acc; x < acc; x++)
                        for (int z = -acc; z < acc; z++)
                            for (int y = -acc; y < acc; y++)
                                if (entity.getLocation().getBlock().getRelative(x, y, z).equals(item)) {
                                    return entity;
                                }
                }
        }
        return null;
    }

    private void cool(final UUID id) {
        if (!coolList.contains(id)) {
            coolList.add(id);
            // System.out.println("Cool Add");
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> coolList.remove(id), (3 * 20));
        }
    }

    // private boolean isShard(ItemStack s) {
    // try {
    // if((s.getItemMeta().getDisplayName().contains("§l")) &&
    // (s.getItemMeta().getDisplayName().contains("Boss Shard")))
    // return true;
    // }catch(Exception x) {}
    // return false;
    // }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerEat(PlayerItemConsumeEvent e) {
        final Player p = e.getPlayer();
        try {
            if (Objects.requireNonNull(p.getInventory().getItemInMainHand().getItemMeta()).getDisplayName()
                    .equals(getLang("items.forbiddenfruit"))) {
                e.setCancelled(true);
                p.getWorld().strikeLightning(p.getLocation());
                // Biome
                final Location l = p.getLocation();
                if (Objects.requireNonNull(l.getWorld()).getBiome((int) l.getX(), (int) l.getY(), (int) l.getZ()).toString().contains("OCEAN")
                        && l.getBlockY() <= getConfig().getInt("waterLevel")) {
                    takeItem(p, 1);
                    boom(l, 3, false);
                    p.sendMessage(getLang("drownSpawn"));
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> spawnBoss(p, l, "DrownedGod"), (40));
                } else if (l.getWorld().getEnvironment().equals(Environment.NORMAL)
                        && l.getBlockY() >= getConfig().getInt("skyLevel")) {
                    takeItem(p, 1);
                    // boom(l,3,false);
                    lightningShow(l, 3);
                    l.setY(l.getY() + 5);
                    p.sendMessage(getLang("aetherSpawn"));
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> spawnBoss(p, l, "AetherGod"), (40));
                } else if (l.getWorld().getBiome((int) l.getX(), (int) l.getY(), (int) l.getZ()).toString()
                        .contains("DESERT")) {
                    takeItem(p, 1);
                    // boom(l,2,false);
                    lightningShow(l, 4);
                    p.sendMessage(getLang("pharaohSpawn"));
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> spawnBoss(p, l, "PharaohGod"), (40));
                } else
                    p.sendMessage(getLang("failSpawn"));
            } else if (p.getInventory().getItemInMainHand().getItemMeta().getDisplayName()
                    .equals(getLang("items.abhorrentfruit"))) {
                e.setCancelled(true);
                p.setFireTicks(60 * 20);
                // Biome
                final Location l = p.getLocation();
                if (l.getWorld().getEnvironment().equals(Environment.NETHER)) {
                    takeItem(p, 1);
                    boom(l, 8, false);
                    p.sendMessage(getLang("devilSpawn"));
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, () -> spawnBoss(p, l, "Devil"), (40));
                } else
                    p.sendMessage(getLang("hellSpawn"));
            }
        } catch (Exception x) {
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityCombust(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        itemDropMap.put(e.getItemDrop(), p);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityCombust(EntityCombustEvent e) {
        // //Item Death
        // Entity ent = e.getEntity();
        // if(ent.getType().equals(EntityType.DROPPED_ITEM)) {
        // //System.out.println("C: 2");
        // ItemStack s = ((Item)ent).getItemStack();
        // final Player p = itemDropMap.get(((Item)ent));
        // if(s.getType().equals(Material.GOLD_INGOT) && s.getAmount() >= 16) {
        // //Ghast Spawn
        // Location l = ent.getLocation();
        // l.setY(l.getY()+1);
        // if(l.getWorld().getEnvironment().equals(Environment.NETHER) &&
        // checkBlockRecipe(l,"REDSTONE_WIRE:REDSTONE_WIRE:REDSTONE_WIRE","REDSTONE_WIRE:AIR:REDSTONE_WIRE","REDSTONE_WIRE:REDSTONE_WIRE:REDSTONE_WIRE",false))
        // {
        // boom(l,5,true);
        // final Location bs = l.clone();
        // Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new
        // Runnable() {
        // public void run() {
        // spawnBoss(p,bs,"GhastLord");
        // }
        // }, (40));
        // }
        // }else if(s.getType().equals(Material.GOLDEN_CARROT) && s.getAmount() >= 16) {
        // this.getLogger().log(Level.WARNING, "Gold Carrors Consumed!");
        // Location l = ent.getLocation();
        // l.setY(l.getY()+1);
        // if(l.getWorld().getBiome((int)l.getX(), (int)l.getY(),
        // (int)l.getZ()).toString().contains("DESERT"))
        // if(l.getWorld().getEnvironment().equals(Environment.NORMAL) &&
        // checkBlockRecipe(l,"CARROTS:CARROTS:CARROTS","CARROTS:AIR:CARROTS","CARROTS:CARROTS:CARROTS",false))
        // {
        // this.getLogger().log(Level.WARNING, "Correct condition found for Killer
        // Bunny!");
        // //boom(l,2,false);
        // Location la = l.clone();
        // la.setY(la.getY()-1);
        // la.getBlock().setType(Material.OBSIDIAN);
        // la.getWorld().strikeLightning(la);
        // final Location bs = l.clone();
        // Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new
        // Runnable() {
        // public void run() {
        // spawnBoss(p,bs,"KillerBunny");
        // }
        // }, (40));
        // }
        // }
        // }
        // No Fire Check
        if (noFireList.contains(e.getEntity().getUniqueId()))
            e.setCancelled(true);
    }

    private void takeItem(Player p, int amount) {
        if (p.getInventory().getItemInMainHand().getAmount() > amount) {
            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - amount);
        } else
            p.getInventory().setItemInMainHand(null);
    }

    private void boom(final Location l, final int size, final boolean fire) {
        // Boom 1
        l.getWorld().createExplosion(l, size, fire);
        // Boom 2
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                Location l5 = l.clone();
                l5.setX(l5.getX() + 3);
                Location l6 = l.clone();
                l6.setX(l6.getX() - 3);
                Location l7 = l.clone();
                l7.setZ(l7.getZ() + 3);
                Location l8 = l.clone();
                l8.setZ(l8.getZ() - 3);
                Location l9 = l.clone();
                l9.setY(l9.getY() + 3);
                Location l10 = l.clone();
                l10.setY(l10.getY() - 3);
                l.getWorld().createExplosion(l5, size, fire);
                l.getWorld().createExplosion(l6, size, fire);
                l.getWorld().createExplosion(l7, size, fire);
                l.getWorld().createExplosion(l8, size, fire);
                l.getWorld().createExplosion(l9, size, fire);
                l.getWorld().createExplosion(l10, size, fire);
            }
        }, (10));
        // Boom 3
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                Location l1 = l.clone();
                l1.setX(l1.getX() + 6);
                l1.setZ(l1.getZ() + 6);
                Location l2 = l.clone();
                l2.setX(l2.getX() + 6);
                l2.setZ(l2.getZ() - 6);
                Location l3 = l.clone();
                l3.setX(l3.getX() - 6);
                l3.setZ(l3.getZ() - 6);
                Location l4 = l.clone();
                l4.setX(l4.getX() - 6);
                l4.setZ(l4.getZ() + 6);
                l.getWorld().createExplosion(l1, size, fire);
                l.getWorld().createExplosion(l2, size, fire);
                l.getWorld().createExplosion(l3, size, fire);
                l.getWorld().createExplosion(l4, size, fire);
            }
        }, (20));
    }

    private void lightningShow(final Location l, final int size) {
        // Boom 1
        l.getWorld().strikeLightningEffect(l);
        // Boom 2
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                Location l5 = l.clone();
                l5.setX(l5.getX() + 3);
                Location l6 = l.clone();
                l6.setX(l6.getX() - 3);
                Location l7 = l.clone();
                l7.setZ(l7.getZ() + 3);
                Location l8 = l.clone();
                l8.setZ(l8.getZ() - 3);
                Location l9 = l.clone();
                l9.setY(l9.getY() + 3);
                Location l10 = l.clone();
                l10.setY(l10.getY() - 3);
                l.getWorld().strikeLightningEffect(l5);
                l.getWorld().strikeLightningEffect(l6);
                l.getWorld().strikeLightningEffect(l7);
                l.getWorld().strikeLightningEffect(l8);
                l.getWorld().strikeLightningEffect(l9);
                l.getWorld().strikeLightningEffect(l10);
            }
        }, (10));
        // Boom 3
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                Location l1 = l.clone();
                l1.setX(l1.getX() + 6);
                l1.setZ(l1.getZ() + 6);
                Location l2 = l.clone();
                l2.setX(l2.getX() + 6);
                l2.setZ(l2.getZ() - 6);
                Location l3 = l.clone();
                l3.setX(l3.getX() - 6);
                l3.setZ(l3.getZ() - 6);
                Location l4 = l.clone();
                l4.setX(l4.getX() - 6);
                l4.setZ(l4.getZ() + 6);
                l.getWorld().strikeLightningEffect(l1);
                l.getWorld().strikeLightningEffect(l2);
                l.getWorld().strikeLightningEffect(l3);
                l.getWorld().strikeLightningEffect(l4);
            }
        }, (20));
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onChunkLoad(ChunkLoadEvent e) {
        for (Entity ent : e.getChunk().getEntities()) {
            final String bossType = saveFile.getString("bosses." + ent.getUniqueId().toString());
            if (bossType != null)
                if (!bossMap.containsKey(ent))
                    makeBoss(ent, bossType);
        }
    }

    private LivingEntity getDamager(Entity e) {
        try {
            if (e instanceof LivingEntity) {
                return (LivingEntity) e;
            } else if (e instanceof Projectile) {
                Entity ne = (Entity) ((Projectile) e).getShooter();
                if (ne instanceof LivingEntity)
                    return (LivingEntity) ne;
            }
        } catch (Exception x) {
        }
        return null;
    }

    private void makeTrail(final Entity e, final String effect) {
        if (e.isDead()) {
            return;
        }
        Location loc = e.getLocation();
        displayParticle(effect, loc);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                makeTrail(e, effect);
            }
        }, 1L);
    }

    private void removeBar(Player p) {
        for (Map.Entry<Entity, BossBar> hm : bossMap.entrySet())
            if (hm.getValue().getPlayers().contains(p)) {
                hm.getValue().removePlayer(p);
            }
        // if(playerBars.containsKey(p.getName())){
        // playerBars.get(p.getName()).removePlayer(p);
        // playerBars.remove(p.getName());
        // }
    }

    public void showBossBar(Player p, Entity e) {
        // Clear Old Bars
        for (Map.Entry<Entity, BossBar> hm : bossMap.entrySet())
            if (hm.getValue().getPlayers().contains(p)) {
                bossMap.get(hm.getKey()).removePlayer(p);
            }
        // Add New Bar
        BossBar bar = bossMap.get(e);
        if (!bar.getPlayers().contains(p))
            bar.addPlayer(p);
        updateHP(e);
    }

    private void updateHP(Entity e) {
        try {
            BossBar bar = bossMap.get(e);
            float health = (float) ((Damageable) e).getHealth();
            float maxHealth = (float) ((LivingEntity) e).getAttribute(Attribute.MAX_HEALTH).getBaseValue();
            float setHealth = (health * 100.0f) / maxHealth;
            bar.setProgress(setHealth / 100.0f);
        } catch (Exception x) {
        }
    }

    private void spawnBoss(Player p, Location l, String bossType) {
        try {
            // Check Disabled Worlds
            if (getConfig().getList("disabledWorlds").contains(l.getWorld().getName())) {
                for (Entity e : getNearbyEntities(l, 24, new ArrayList<EntityType>(Arrays.asList(EntityType.PLAYER))))
                    ((Player) e).sendMessage(getLang("failSpawnWorld"));
                return;
            }
            // Check World Guard
            // if(this.getServer().getPluginManager().getPlugin("WorldGuard") != null)
            // try {
            // if(!new WorldGuardMethods().queryBuild(p, l)) {
            // for(Entity e : getNearbyEntities(l, 24, new
            // ArrayList<EntityType>(Arrays.asList(EntityType.PLAYER))))
            // ((Player)e).sendMessage(getLang("failSpawnWG"));
            // return;
            // }
            // }catch(Exception x) {}
            // Check Boss Limit
            if (bossMap.size() >= getConfig().getInt("bossLimit")) {
                for (Entity e : getNearbyEntities(l, 24, new ArrayList<EntityType>(Arrays.asList(EntityType.PLAYER))))
                    ((Player) e).sendMessage(getLang("tooManyBosses"));
                return;
            }
            // Log Spawn
            this.getLogger().log(Level.INFO, "Spawn Boss: " + bossType);
            String entType = getConfig().getString("bosses." + bossType + ".entity");
            // //System.out.println("entType: " + entType);
            // //System.out.println("entType2: " + EntityType.valueOf(entType));
            // //System.out.println("entType3: " + EntityType.valueOf(entType));
            Entity boss = l.getWorld().spawnEntity(l, EntityType.valueOf(entType.toUpperCase()));
            // Slime
            if (boss instanceof Slime) {
                ((Slime) boss).setSize(10);
            } else if (boss instanceof Rabbit) {
                ((Rabbit) boss).setRabbitType(Type.THE_KILLER_BUNNY);
                ((Rabbit) boss).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999 * 999, 1));
                ((Rabbit) boss).addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 999 * 999, 1));
                ((Rabbit) boss).addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999 * 999, 1));
                // ((Rabbit)boss).setFireTicks(999*999);
                // ((Rabbit) boss).setCustomName(null);
            } else if (bossType.equals("WitherSkeletonKing") || bossType.equals("ZombieKing")) {
                equipMob(boss, "DIAMOND");
            } /**
               * else if(bossType.equals("IllagerKing")) {
               * 
               * }
               **/
            else if (bossType.equals("PapaPanda")) {
                ((Panda) boss).setMainGene(Gene.AGGRESSIVE);
            } else if (bossType.equals("DrownedGod")) {
                equipMob(boss, "DIAMOND");
                // ItemStack head = getHead("5cf625ba-8f8e-4069-bcfe-af5fbb35a3f4","§b§lDrowned
                // God's Head");//"LeftShark"
                ItemStack head = getSkull(
                        "http://textures.minecraft.net/texture/2d7a509789933b2640775f003a71dfb4f5d97aa23d804223029d295274deead1");
                ItemStack hand = new ItemStack(Material.TRIDENT);
                EntityEquipment ee = ((LivingEntity) boss).getEquipment();
                ee.setItemInMainHandDropChance(0.0F);
                ee.setHelmet(head);
                ee.setItemInMainHand(hand);
                // ((LivingEntity) boss).addPotionEffect(new
                // PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,999*999,2));
            } else if (bossType.equals("PharaohGod")) {
                equipMob(boss, "GOLDEN");
                // ItemStack head = getHead("73917135-da9d-4fd1-b032-158a7d1d03d1","§6§lPharaoh
                // God's Head");//"Sam1_6"
                ItemStack head = getSkull(
                        "http://textures.minecraft.net/texture/51182cf65d180ecf08fab2311abed0cfcee960e6df5a3ba528f7ea47cc41f0a2");
                ItemStack hand = new ItemStack(Material.BLAZE_ROD);
                hand.addUnsafeEnchantment(Enchantment.KNOCKBACK, 5);
                hand.addUnsafeEnchantment(Enchantment.SHARPNESS, 10);
                EntityEquipment ee = ((LivingEntity) boss).getEquipment();
                ee.setItemInMainHandDropChance(0.0F);
                ee.setHelmet(head);
                ee.setItemInMainHand(hand);
                ((LivingEntity) boss).addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 999 * 999, 1));
                ((LivingEntity) boss).addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 999 * 999, 2));
            } else if (bossType.equals("AetherGod")) {
                // System.out.println("Aether God 1");
                equipMob(boss, "DIAMOND");
                // ItemStack head = getHead("853c80ef-3c37-49fd-aa49-938b674adae6","§lAether
                // God's Head");//"jeb_"
                ItemStack head = getSkull(
                        "http://textures.minecraft.net/texture/6545210b810f3d2db27c87f443a5fb812bb85d14d1922d08f50a2ebb1b248788");
                ItemStack hand = new ItemStack(Material.BOW);
                hand.addUnsafeEnchantment(Enchantment.POWER, 10);
                hand.addUnsafeEnchantment(Enchantment.PUNCH, 3);
                hand.addUnsafeEnchantment(Enchantment.FLAME, 3);
                EntityEquipment ee = ((LivingEntity) boss).getEquipment();
                ee.setItemInMainHandDropChance(0.0F);
                ee.setHelmet(head);
                ee.setItemInMainHand(hand);
                ((LivingEntity) boss).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 999 * 999, 1));
                levitate((LivingEntity) boss, true);
                target(boss, 0.01);
            } else if (bossType.equals("Demon")) {
                ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS, 1);
                ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
                for (ItemStack s : Arrays.asList(chest, pants, boots))
                    dye(s, Color.MAROON);
                ItemStack head = getSkull(
                        "http://textures.minecraft.net/texture/e00cd37a4ebcbb28cb85d75bbde7b7aad5a0f42bf4842f8da77dffdea18c1356");
                ItemStack hand = new ItemStack(Material.IRON_HOE);
                hand.addUnsafeEnchantment(Enchantment.SHARPNESS, 10);
                hand.addUnsafeEnchantment(Enchantment.KNOCKBACK, 3);
                hand.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 10);
                EntityEquipment ee = ((LivingEntity) boss).getEquipment();
                ee.setItemInMainHandDropChance(0.0F);
                ee.setHelmetDropChance(0.0F);
                ee.setHelmet(head);
                ee.setChestplate(chest);
                ee.setLeggings(pants);
                ee.setBoots(boots);
                ee.setItemInMainHand(hand);
                ((LivingEntity) boss).addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999 * 999, 1));
                ((LivingEntity) boss).addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 999 * 999, 5));
            } else if (bossType.equals("Devil")) {
                ((PigZombie) boss).setAngry(true);
                ((PigZombie) boss).setAnger(999 * 999);
                equipMob(boss, "DIAMOND");
                ItemStack head = getSkull(
                        "http://textures.minecraft.net/texture/9da39269ef45f825ec61bb4f8aa09bd3cf07996fb6fac338a6e91d6699ae425");
                ItemStack hand = new ItemStack(Material.ENCHANTED_BOOK);
                hand.addUnsafeEnchantment(Enchantment.SHARPNESS, 999);
                EntityEquipment ee = ((LivingEntity) boss).getEquipment();
                ee.setItemInMainHandDropChance(0.0F);
                ee.setHelmet(head);
                ee.setItemInMainHand(hand);
                levitate((LivingEntity) boss, true);
                target(boss, 0.05);
            } else if (bossType.equals("Death")) {
                ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS, 1);
                ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
                for (ItemStack s : Arrays.asList(chest, pants, boots))
                    dye(s, Color.BLACK);
                ItemStack head = getSkull(
                        "http://textures.minecraft.net/texture/69e2f33eb180f0434916dc5d2bb326a6ea22fc9bbf988bc31a241fd4278023");
                ItemStack hand = new ItemStack(Material.IRON_HOE);
                hand.addUnsafeEnchantment(Enchantment.SHARPNESS, 999);
                EntityEquipment ee = ((LivingEntity) boss).getEquipment();
                ee.setItemInMainHandDropChance(0.0F);
                ee.setHelmet(head);
                ee.setItemInMainHand(hand);
                ee.setChestplate(chest);
                ee.setLeggings(pants);
                // ee.setBoots(boots);
                levitate((LivingEntity) boss, true);
                target(boss, 0.2);
            }
            // Mount
            if (getConfig().getString("bosses." + bossType + ".mount") != null) {
                LivingEntity mount = (LivingEntity) boss.getWorld().spawnEntity(boss.getLocation(),
                        EntityType.valueOf(getConfig().getString("bosses." + bossType + ".mount").toUpperCase()));
                mount.addPassenger(boss);
                int h = getConfig().getInt("bosses." + bossType + ".health");
                mount.getAttribute(Attribute.MAX_HEALTH).setBaseValue(h);
                mount.setHealth(h);
                mount.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 999 * 999, 10));
                if (mount.getType().equals(EntityType.BAT)) {
                    mount.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999 * 999, 1));
                    mount.setInvulnerable(true);
                    if (bossType.equals("AetherGod"))
                        mount.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 999 * 999, 2));
                }
                // mount.setPersistent(true);
            }
            // Stop Despawn
            // boss.setPersistent(true);
            // Save Boss
            saveFile.set("bosses." + boss.getUniqueId().toString(), bossType);
            save();
            makeBoss(boss, bossType);
        } catch (Exception x) {
            this.getLogger().log(Level.SEVERE, "Failed to spawn Boss: " + bossType);
            x.printStackTrace();
        }
    }

    private void levitate(final LivingEntity e, boolean up) {
        // Gone check
        if (e == null || e.isDead())
            return;
        // Leviate
        if (up) {
            e.removePotionEffect(PotionEffectType.LEVITATION);
            e.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 55, 1));
        } else {
            e.removePotionEffect(PotionEffectType.SLOW_FALLING);
            e.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 4 * 20, 5));
        }
        // Target
        Player p = null;
        double d = 999;
        for (Entity ent : e.getNearbyEntities(30, 30, 30))
            if (ent instanceof Player) {
                double dis = ent.getLocation().distance(e.getLocation());
                if (dis < d) {
                    p = (Player) ent;
                    d = dis;
                }
            }
        if (p != null)
            targetMap.put(e, p);
        // Loop
        final boolean nup = !up;
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                levitate(e, nup);
            }
        }, (20 * 2));
    }

    public void target(final Entity e, final double speed) {
        try {
            Location to = targetMap.get(e).getLocation();
            if (e.isDead()) {
                return;
            }
            if (to != null && (e.getLocation().distance(to) > 10)) {
                Vector direction = to.toVector().subtract(e.getLocation().toVector()).normalize();
                e.setVelocity(direction.multiply(speed));
            }
        } catch (Exception localException) {
        }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {
                try {
                    target(e, speed);
                } catch (Exception localException) {
                }
            }
        }, 1L);
    }

    private void equipMob(Entity mob, String type) {
        ItemStack helm = new ItemStack(Material.valueOf(type + "_HELMET"), 1);
        ItemStack chest = new ItemStack(Material.valueOf(type + "_CHESTPLATE"), 1);
        ItemStack pants = new ItemStack(Material.valueOf(type + "_LEGGINGS"), 1);
        ItemStack boots = new ItemStack(Material.valueOf(type + "_BOOTS"), 1);
        ItemStack sword = new ItemStack(Material.valueOf(type + "_SWORD"), 1);
        sword.addUnsafeEnchantment(Enchantment.SHARPNESS, 4);
        EntityEquipment ee = ((LivingEntity) mob).getEquipment();
        ee.setHelmetDropChance(0.0F);
        ee.setChestplateDropChance(0.0F);
        ee.setLeggingsDropChance(0.0F);
        ee.setBootsDropChance(0.0F);
        ee.setItemInMainHandDropChance(0.0F);
        ee.setHelmet(helm);
        ee.setChestplate(chest);
        ee.setLeggings(pants);
        ee.setBoots(boots);
        ee.setItemInMainHand(sword);
    }

    public void makeBoss(Entity ent, String bossType) {
        System.out.println("Make Boss");
        String title = ChatColor.translateAlternateColorCodes('&',
                getConfig().getString("bosses." + bossType + ".name"));
        BossBar bar = Bukkit.createBossBar(title,
                BarColor.valueOf(getConfig().getString("bosses." + bossType + ".barColor")),
                BarStyle.valueOf(getConfig().getString("bosses." + bossType + ".barStyle")), BarFlag.CREATE_FOG);
        bar.setVisible(true);
        bossMap.put(ent, bar);
        int maxHP = getConfig().getInt("bosses." + bossType + ".health");
        double maxHealth = ((LivingEntity) ent).getAttribute(Attribute.MAX_HEALTH).getBaseValue();
        if (maxHealth != maxHP) {
            ((LivingEntity) ent).getAttribute(Attribute.MAX_HEALTH).setBaseValue(maxHP);
            ((Damageable) ent).setHealth(maxHP);
        }
        // Name
        ent.setCustomName(getConfig().getString("bosses." + bossType + ".name").replace("&", "§"));
        ent.setCustomNameVisible(true);
        // Type Effects
        if (bossType.equals("KillerBunny")) {
            makeTrail(ent, getConfig().getString("bosses." + bossType + ".attackParticle"));
        }
        if (getConfig().getString("bosses." + bossType + ".auraParticle") != null) {
            // System.out.println("Aether God 2");
            makeTrail(ent, getConfig().getString("bosses." + bossType + ".auraParticle"));
        }
        if (bossType.equals("PharaohGod") || bossType.equals("Demon"))
            autoBalls((LivingEntity) ent, bossType);
        // No Despawn
        if (ent instanceof LivingEntity)
            ((LivingEntity) ent).setRemoveWhenFarAway(false);
    }

    public void displayParticle(String effect, Location loc) {
        // effect: particle:speed:amount:range
        String[] split = effect.split(":");
        int speed = Integer.parseInt(split[1]);
        int amount = Integer.parseInt(split[2]);
        double r = Double.parseDouble(split[3]);
        displayParticle(split[0], loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), r, speed, amount);
    }

    public void displayParticle(String effect, Location l, double radius, int speed, int amount) {
        displayParticle(effect, l.getWorld(), l.getX(), l.getY(), l.getZ(), radius, speed, amount);
    }

    private void displayParticle(String effect, World w, double x, double y, double z, double radius, int speed,
            int amount) {
        amount = (amount <= 0) ? 1 : amount;
        Location l = new Location(w, x, y, z);
        try {
            if (radius <= 0) {
                // Effect, Location, Count, X, Y, Z, Speed
                w.spawnParticle(Particle.valueOf(effect), l, amount, 0, 0, 0, speed);
                // w.spawnParticle(Particle.valueOf(effect), l, 0, 0, 0, speed, amount);
            } else {
                List<Location> ll = getArea(l, radius, 0.2);
                if (!ll.isEmpty()) {
                    for (int i = 0; i < amount; i++) {
                        int index = new Random().nextInt(ll.size());
                        // w.spawnParticle(Particle.valueOf(effect), ll.get(index), 1, 0, 0, speed, 1);
                        w.spawnParticle(Particle.valueOf(effect), ll.get(index), amount, 0, 0, 0, speed);
                        ll.remove(index);
                    }
                }
            }
        } catch (Exception ex) {
            // System.out.println("V: " + getServer().getVersion());
            ex.printStackTrace();
        }
    }

    private ArrayList<Location> getArea(Location l, double r, double t) {
        ArrayList<Location> ll = new ArrayList<Location>();
        for (double x = l.getX() - r; x < l.getX() + r; x += t) {
            for (double y = l.getY() - r; y < l.getY() + r; y += t) {
                for (double z = l.getZ() - r; z < l.getZ() + r; z += t) {
                    ll.add(new Location(l.getWorld(), x, y, z));
                }
            }
        }
        return ll;
    }

    public ItemStack getLoot(Entity p, String bossType) {
        List<String> list = new ArrayList<String>(
                getConfig().getConfigurationSection("bosses." + bossType + ".loot").getKeys(false));
        ItemStack s = null;
        int i = 256;
        do {
            // Get Item
            // System.out.println("List: " + list);
            s = getItem(bossType, list.get(rand(1, list.size()) - 1));
            // Do Loop
            i = i - 1;
            // Staff
            if (!bossType.contains("God") && p != null && (p instanceof Player)) {
                String mode = getStaffMode(((Player) p));
                if (mode != null) {
                    if (mode.equals("Shard")) {
                        if (s.getItemMeta() == null || s.getItemMeta().getDisplayName() == null
                                || (!s.getItemMeta().getDisplayName().contains(getLang("shardKeyWord")))) {
                            s = null;
                        }
                    } else if (mode.equals("Loot")) {
                        if (s.getItemMeta() != null && s.getItemMeta().getDisplayName() != null
                                && s.getItemMeta().getDisplayName().contains(getLang("shardKeyWord"))) {
                            s = null;
                        }
                    }
                }
            }
        } while (s == null && i > 0);
        return s;
    }

    public ItemStack getItem(String bossType, String loot) {
        ItemStack s = getConfig().getItemStack("bosses." + bossType + ".loot." + loot);
        if (s == null)
            s = getItemOld(bossType, loot);
        return s;
    }

    private void setItem(ItemStack s, String path, FileConfiguration fc) {
        fc.set(path, s);
        saveConfig();
    }

    @SuppressWarnings({ "unchecked" })
    public ItemStack getItemOld(String bossType, String loot) {
        // System.out.println("Get Loot: " + loot);
        try {
            String setItem = this.getConfig().getString("bosses." + bossType + ".loot." + loot + ".item");

            String setAmountString = this.getConfig().getString("bosses." + bossType + ".loot." + loot + ".amount");
            int setAmount;
            if (setAmountString != null) {
                setAmount = getIntFromString(setAmountString);
            } else
                setAmount = 1;
            assert setItem != null;
            ItemStack stack = new ItemStack(Material.valueOf(setItem.toUpperCase()), setAmount);
            // Texture
            if (stack.getType().equals(Material.PLAYER_HEAD)) {
                String tex = getConfig().getString("bosses." + bossType + ".loot." + loot + ".texture");
                if (tex != null) {
                    stack = getSkull(tex);
                }
            }
            // Get Name
            String name = null;
            if (getConfig().getList("bosses." + bossType + ".loot." + loot + ".name") != null) {
                // System.out.println("2");
                ArrayList<String> names = (ArrayList<String>) getConfig()
                        .getList("bosses." + bossType + ".loot." + loot + ".name");
                if (names != null) {
                    name = names.get(rand(1, names.size()) - 1);
                    name = prosessLootName(name, stack);
                }
            } else if (getConfig().getString("bosses." + bossType + ".loot." + loot + ".name") != null) {
                // System.out.println("3");
                name = getConfig().getString("bosses." + bossType + ".loot." + loot + ".name");
                name = prosessLootName(name, stack);
            }
            // Get Lore
            ArrayList<String> loreList = new ArrayList<String>();
            for (int i = 0; i <= 10; i++) {
                if (getConfig().getString("bosses." + bossType + ".loot." + loot + ".lore" + i) != null) {
                    String lore = (getConfig().getString("bosses." + bossType + ".loot." + loot + ".lore" + i));
                    lore = ChatColor.translateAlternateColorCodes('&', lore);
                    loreList.add(lore);
                    // System.out.println("5");
                }
            }
            // System.out.println("6");
            if (getConfig().getList("bosses." + bossType + ".loot." + loot + ".lore") != null) {
                // System.out.println("7");
                ArrayList<String> lb = (ArrayList<String>) getConfig()
                        .getList("bosses." + bossType + ".loot." + loot + ".lore");
                ArrayList<String> l = (ArrayList<String>) lb.clone();
                int min = l.size();
                if (getConfig().getString("bosses." + bossType + ".loot." + loot + ".minLore") != null)
                    min = getConfig().getInt("bosses." + bossType + ".loot." + loot + ".minLore");
                int max = l.size();
                if (getConfig().getString("bosses." + bossType + ".loot." + loot + ".maxLore") != null)
                    max = getConfig().getInt("bosses." + bossType + ".loot." + loot + ".maxLore");
                if (!l.isEmpty())
                    for (int i = 0; i < rand(min, max); i++) {
                        String lore = l.get(rand(1, l.size()) - 1);
                        l.remove(lore);
                        loreList.add(prosessLootName(lore, stack));
                    }
            }
            // System.out.println("8");
            ItemMeta meta = stack.getItemMeta();
            // Durability
            if (this.getConfig().getString("bosses." + bossType + ".loot." + loot + ".durability") != null) {
                String durabilityString = this.getConfig()
                        .getString("bosses." + bossType + ".loot." + loot + ".durability");
                int durability = getIntFromString(durabilityString);
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
            if (this.getConfig().getString("bosses." + bossType + ".loot." + loot + ".colour") != null
                    && stack.getType().toString().toLowerCase().contains("leather")) {
                String c = this.getConfig().getString("bosses." + bossType + ".loot." + loot + ".colour");
                String[] split = c.split(",");
                Color colour = Color.fromRGB(Integer.parseInt(split[0]), Integer.parseInt(split[1]),
                        Integer.parseInt(split[2]));
                dye(stack, colour);
            }
            // Book
            if ((stack.getType().equals(Material.WRITTEN_BOOK)) || (stack.getType().equals(Material.WRITABLE_BOOK))) {
                BookMeta bMeta = (BookMeta) stack.getItemMeta();
                if (this.getConfig().getString("bosses." + bossType + ".loot." + loot + ".author") != null) {
                    String author = this.getConfig().getString("bosses." + bossType + ".loot." + loot + ".author");
                    author = ChatColor.translateAlternateColorCodes('&', author);
                    bMeta.setAuthor(author);
                }
                if (this.getConfig().getString("bosses." + bossType + ".loot." + loot + ".title") != null) {
                    String title = this.getConfig().getString("bosses." + bossType + ".loot." + loot + ".title");
                    title = ChatColor.translateAlternateColorCodes('&', title);
                    bMeta.setTitle(title);
                }
                if (this.getConfig().getString("bosses." + bossType + ".loot." + loot + ".pages") != null) {
                    for (String i : this.getConfig()
                            .getConfigurationSection("bosses." + bossType + ".loot." + loot + ".pages")
                            .getKeys(false)) {
                        String page = this.getConfig()
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
                List<Pattern> patList = (List<Pattern>) getConfig()
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
                List<Pattern> patList = (List<Pattern>) getConfig()
                        .getList("bosses." + bossType + ".loot." + loot + ".patterns");
                b.setBaseColor(
                        DyeColor.valueOf(getConfig().getString("bosses." + bossType + ".loot." + loot + ".colour")));
                b.setPatterns(patList);
                b.update();
                bmeta.setBlockState(b);
                stack.setItemMeta(bmeta);
            }
            // Owner
            if (stack.getType().equals(Material.PLAYER_HEAD)) {
                String owner = getConfig().getString("bosses." + bossType + ".loot." + loot + ".owner");
                if (owner != null) {
                    SkullMeta sm = (SkullMeta) stack.getItemMeta();
                    sm.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(owner)));
                    stack.setItemMeta(sm);
                }
            }
            // Potions
            if (getConfig().getString("bosses." + bossType + ".loot." + loot + ".potion") != null)
                if (stack.getType().equals(Material.POTION) || stack.getType().equals(Material.SPLASH_POTION)
                        || stack.getType().equals(Material.LINGERING_POTION)) {
                    PotionMeta pMeta = (PotionMeta) stack.getItemMeta();
                    String pn = getConfig().getString("bosses." + bossType + ".loot." + loot + ".potion");
                    pMeta.setBasePotionType(PotionType.valueOf(pn));
                    stack.setItemMeta(pMeta);
                }
            int enchAmount = 0;
            for (int e = 0; e <= 10; e++) {
                if (this.getConfig().getString("bosses." + bossType + ".loot." + loot + ".enchantments." + e) != null) {
                    enchAmount++;
                }
            }
            if (enchAmount > 0) {
                int enMin = enchAmount / 2;
                if (enMin < 1) {
                    enMin = 1;
                }
                int enMax = enchAmount;
                if ((this.getConfig().getString("bosses." + bossType + ".loot." + loot + ".minEnchantments") != null)
                        && (this.getConfig()
                                .getString("bosses." + bossType + ".loot." + loot + ".maxEnchantments") != null)) {
                    enMin = this.getConfig().getInt("bosses." + bossType + ".loot." + loot + ".minEnchantments");
                    enMax = this.getConfig().getInt("bosses." + bossType + ".loot." + loot + ".maxEnchantments");
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
                    if (this.getConfig()
                            .getString("bosses." + bossType + ".loot." + loot + ".enchantments." + j) != null) {
                        int enChance = 1;
                        if (this.getConfig().getString(
                                "bosses." + bossType + ".loot." + loot + ".enchantments." + j + ".chance") != null) {
                            enChance = this.getConfig()
                                    .getInt("bosses." + bossType + ".loot." + loot + ".enchantments." + j + ".chance");
                        }
                        chance = new Random().nextInt(enChance - 1 + 1) + 1;
                        if (chance == 1) {
                            String enchantment = this.getConfig().getString(
                                    "bosses." + bossType + ".loot." + loot + ".enchantments." + j + ".enchantment");

                            String levelString = this.getConfig().getString(
                                    "bosses." + bossType + ".loot." + loot + ".enchantments." + j + ".level");
                            int level = getIntFromString(levelString);
                            NamespacedKey k = NamespacedKey.minecraft(enchantment.toLowerCase());
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
            this.getLogger().log(Level.SEVERE, e.getMessage(), true);
            e.printStackTrace();
        }
        return null;
    }

    /*
     @SuppressWarnings("rawtypes")
     private void setItem(ItemStack s, String path, FileConfiguration fc) {
     if (s != null) {
     fc.set(path + ".item", s.getType().toString());
     fc.set(path + ".amount", s.getAmount());
     fc.set(path + ".durability",
     ((org.bukkit.inventory.meta.Damageable)s.getItemMeta()).getDamage());
     if (s.getItemMeta() != null) {
     fc.set(path + ".name", s.getItemMeta().getDisplayName());
     if (s.getItemMeta().getLore() != null) {
     for (int l = 0; l < s.getItemMeta().getLore().size(); l++) {
     if (s.getItemMeta().getLore().get(l) != null) {
     fc.set(path + ".lore" + l, s.getItemMeta().getLore().get(l));
     }
     }
     }
     }
     Enchantment e;
     for (Map.Entry<Enchantment, Integer> hm : s.getEnchantments().entrySet()) {
     e = hm.getKey();
     int level = hm.getValue();
     for (int ei = 0; ei < 13; ei++) {
     if (fc.getString(path + ".enchantments." + ei) == null) {
     fc.set(path + ".enchantments." + ei + ".enchantment",
     e.getKey().getKey().toString());
     fc.set(path + ".enchantments." + ei + ".level", level);
     break;
     }
     }
     }
     if (s.getType().equals(Material.ENCHANTED_BOOK)) {
     EnchantmentStorageMeta em = (EnchantmentStorageMeta) s.getItemMeta();
     for (Object hm : em.getStoredEnchants().entrySet()) {
     e = (Enchantment) ((Map.Entry) hm).getKey();
     int level = (Integer) ((Map.Entry) hm).getValue();
     for (int ei = 0; ei < 13; ei++) {
     if (fc.getString(path + ".enchantments." + ei) == null) {
     fc.set(path + ".enchantments." + ei + ".enchantment",
     e.getKey().getKey().toString());
     fc.set(path + ".enchantments." + ei + ".level", level);
     break;
     }
     }
     }
     }
     if ((s.getType().equals(Material.WRITTEN_BOOK)) ||
     (s.getType().equals(Material.WRITABLE_BOOK))) {
     BookMeta meta = (BookMeta) s.getItemMeta();
     if (meta.getAuthor() != null) {
     fc.set(path + ".author", meta.getAuthor());
     }
     if (meta.getTitle() != null) {
     fc.set(path + ".title", meta.getTitle());
     }
     int i = 0;
     for (String p : meta.getPages()) {
     fc.set(path + ".pages." + i, p);
     i++;
     }
     }
     //Banner
     if (s.getType().toString().contains("BANNER")) {
     BannerMeta b = (BannerMeta) s.getItemMeta();
     if (b != null) {
     List patList = b.getPatterns();
     if (!patList.isEmpty())
     fc.set(path + ".patterns", patList);
     }
     }
     //Shield
     if (s.getType().equals(Material.SHIELD)) {
     ItemMeta im = s.getItemMeta();
     BlockStateMeta bmeta = (BlockStateMeta) im;
     Banner b = (Banner) bmeta.getBlockState();

     fc.set(path + ".colour", b.getBaseColor().toString());
     List patList = b.getPatterns();
     if (!patList.isEmpty())
     fc.set(path + ".patterns", patList);
     }
     //Potions
     if (s.getType().equals(Material.POTION) ||
     s.getType().equals(Material.SPLASH_POTION) ||
     s.getType().equals(Material.LINGERING_POTION)) {
     PotionMeta pMeta = (PotionMeta) s.getItemMeta();
     org.bukkit.potion.PotionData pd = pMeta.getBasePotionData();
     fc.set(path + ".potion", pd.getType().getEffectType().getName());
     }
     if ((s.getType().equals(Material.LEATHER_BOOTS)) ||
     (s.getType().equals(Material.LEATHER_CHESTPLATE)) ||
     (s.getType().equals(Material.LEATHER_HELMET)) ||
     (s.getType().equals(Material.LEATHER_LEGGINGS))) {
     LeatherArmorMeta l = (LeatherArmorMeta) s.getItemMeta();
     Color c = l.getColor();
     String color = c.getRed() + "," + c.getGreen() + "," + c.getBlue();
     fc.set(path + ".colour", color);
     }
     if (s.getType().equals(Material.PLAYER_HEAD)) {
     SkullMeta sm = (SkullMeta) s.getItemMeta();
     fc.set(path + ".owner", sm.getOwningPlayer().getUniqueId().toString());
     }
     ArrayList<String> flags = new ArrayList<>();
     for (ItemFlag f : s.getItemMeta().getItemFlags())
     if (f != null)
     flags.add(f.name());
     if (!flags.isEmpty())
     fc.set(path + ".flags", flags);
     } else {
     System.out.println("Item is null!");
     }
     saveConfig();
     }
    */

    public int rand(int min, int max) {
        int r = min + (int) (Math.random() * (1 + max - min));
        return r;
    }

    private String prosessLootName(String name, ItemStack stack) {
        name = ChatColor.translateAlternateColorCodes('&', name);
        String itemName = stack.getType().name();
        itemName = itemName.replace("_", " ");
        itemName = itemName.toLowerCase();
        name = name.replace("<itemName>", itemName);
        return name;
    }

    public void dye(ItemStack item, Color color) {
        try {
            LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
            meta.setColor(color);
            item.setItemMeta(meta);
        } catch (Exception e) {
        }
    }

    public int getIntFromString(String setAmountString) {
        int setAmount = 1;
        if (setAmountString.contains("-")) {
            String[] split = setAmountString.split("-");
            try {
                Integer minSetAmount = Integer.parseInt(split[0]);
                Integer maxSetAmount = Integer.parseInt(split[1]);
                setAmount = new Random().nextInt(maxSetAmount - minSetAmount + 1) + minSetAmount;
            } catch (Exception e) {
                System.out.println("getIntFromString: " + e);
            }
        } else {
            setAmount = Integer.parseInt(setAmountString);
        }
        return setAmount;
    }

    @EventHandler
    public void restrictCrafting(PrepareItemCraftEvent e) {
        CraftingInventory ci = e.getInventory();
        try {
            if (ci.getResult().getItemMeta().getDisplayName().contains(getLang("items.bell"))) {
                // Bell of Doom
                if (!ci.getItem(4).getItemMeta().getDisplayName().equals(getLang("items.whiteshard"))) {
                    ci.setResult(null);
                } else if (!ci.getItem(5).getItemMeta().getDisplayName().equals(getLang("items.greenshard"))) {
                    ci.setResult(null);
                } else if (!ci.getItem(6).getItemMeta().getDisplayName().equals(getLang("items.greyshard"))) {
                    ci.setResult(null);
                }
            } else if (ci.getResult().getItemMeta().getDisplayName().contains(getLang("items.spelbook"))) {
                // Wizard Book
                if (!ci.getItem(4).getItemMeta().getDisplayName().equals(getLang("items.blackshard")))
                    ci.setResult(null);
                if (!ci.getItem(6).getItemMeta().getDisplayName().equals(getLang("items.redshard")))
                    ci.setResult(null);
            } else if (ci.getResult().getItemMeta().getDisplayName().contains(getLang("items.giantpotion"))) {
                // Giant Potion
                if (!ci.getItem(4).getItemMeta().getDisplayName().equals(getLang("items.greenshard")))
                    ci.setResult(null);
                if (!ci.getItem(5).getItemMeta().getDisplayName().equals(getLang("items.redshard")))
                    ci.setResult(null);
                if (!ci.getItem(6).getItemMeta().getDisplayName().equals(getLang("items.brownshard")))
                    ci.setResult(null);
            } else if (ci.getResult().getItemMeta().getDisplayName().contains(getLang("items.elderegg"))) {
                // Dragon Egg
                if (!ci.getItem(5).getItemMeta().getDisplayName().equals(getLang("items.whiteshard")))
                    ci.setResult(null);
                if (!ci.getItem(8).getItemMeta().getDisplayName().equals(getLang("items.blackshard")))
                    ci.setResult(null);
            } else if (ci.getResult().getItemMeta().getDisplayName().contains(getLang("items.forbiddenfruit"))) {
                // Forbidden Fruit
                if (!ci.getItem(7).getItemMeta().getDisplayName().equals(getLang("items.emeraldshard")))
                    ci.setResult(null);
                if (!ci.getItem(8).getItemMeta().getDisplayName().equals(getLang("items.goldshard")))
                    ci.setResult(null);
                if (!ci.getItem(9).getItemMeta().getDisplayName().equals(getLang("items.blueshard")))
                    ci.setResult(null);
            } else if (ci.getResult().getItemMeta().getDisplayName().contains(getLang("items.abhorrentfruit"))) {
                // Abhorrent Fruit
                for (ItemStack s : Arrays.asList(ci.getItem(2), ci.getItem(4), ci.getItem(6), ci.getItem(8)))
                    if (!s.getItemMeta().getDisplayName().equals(getLang("items.demonicshard")))
                        ci.setResult(null);
                if (!ci.getItem(5).getItemMeta().getDisplayName().equals(getLang("items.forbiddenfruit")))
                    ci.setResult(null);
            } else if (ci.getResult().getItemMeta().getDisplayName().contains(getLang("items.deathnote"))) {
                // Death Note
                // System.out.println("1F");
                if (!ci.getItem(5).getItemMeta().getDisplayName().equals(getLang("items.knowledgebook"))) {
                    // System.out.println("2F");
                    ci.setResult(null);
                }
            }
        } catch (Exception localException) {
        }
    }

    // public ItemStack getHead(String owner, String name) {
    // //Test for UUID
    // if(UUID.fromString(owner) == null) {
    // this.getLogger().log(Level.SEVERE, "Invalid skin found: " + owner + " please
    // notify plugin author!");
    // owner = "3506994a-bc90-427d-bdda-be06e992aed9";
    // }
    // //Get Skin
    // ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
    // SkullMeta sm = (SkullMeta) stack.getItemMeta();
    // sm.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString(owner)));
    // sm.setDisplayName(prosessLootName(name, stack));
    // stack.setItemMeta(sm);
    // return stack;
    // }

    public ItemStack getIllagerItem() {
        ItemStack s = getItem(Material.BELL, getLang("items.bell"), 1, getLangList("items.belllore"));
        ItemMeta m = s.getItemMeta();
        m.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        s.setItemMeta(m);
        return s;
    }

    public ItemStack getWizardItem() {
        ItemStack s = getItem(Material.ENCHANTED_BOOK, getLang("items.spellbook"), 1,
                getLangList("items.spellbooklore"));
        ItemMeta m = s.getItemMeta();
        m.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        s.setItemMeta(m);
        return s;
    }

    public ItemStack getGiantIem() {
        ItemStack item = getItem(Material.POTION, getLang("items.giantpotion"), 1,
                getLangList("items.giantpotionlore"));
        PotionMeta meta = (PotionMeta) item.getItemMeta();
        meta.setColor(Color.GREEN);
        meta.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 3600 * 20, 10), true);
        meta.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        item.setItemMeta(meta);
        return item;
    }

    public ItemStack getDragonItem() {
        ItemStack s = getItem(Material.DRAGON_EGG, getLang("items.elderegg"), 1, getLangList("items.elderegglore"));
        ItemMeta m = s.getItemMeta();
        m.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        s.setItemMeta(m);
        return s;
    }

    public ItemStack getGodItem() {
        ItemStack s = getItem(Material.ENCHANTED_GOLDEN_APPLE, getLang("items.forbiddenfruit"), 1,
                getLangList("items.forbiddenfruitlore"));
        ItemMeta m = s.getItemMeta();
        // m.addEnchant(Enchantment.VANISHING_CURSE, 1, true);
        s.setItemMeta(m);
        return s;
    }

    public ItemStack getDevilItem() {
        ItemStack s = getItem(Material.APPLE, getLang("items.abhorrentfruit"), 1,
                getLangList("items.abhorrentfruitlore"));
        ItemMeta m = s.getItemMeta();
        m.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        s.setItemMeta(m);
        return s;
    }

    public ItemStack getDeathItem() {
        ItemStack s = getSkull(
                "http://textures.minecraft.net/texture/7eea345908d17dc44967d1dce428f22f2b19397370abeb77bdc12e2dd1cb6");
        ItemMeta m = s.getItemMeta();
        // m.addEnchant(Enchantment.BINDING_CURSE, 1, true);
        m.setDisplayName(getLang("items.deathnote"));
        m.setLore(getLangList("items.deathnotelore"));
        s.setItemMeta(m);
        return s;
    }

    public void addRecipes() {
        addIllagerBell();
        addWizardBook();
        addGiantPotion();
        addDragonEgg();
        addGodFruit();
        addDevilFruit();
        addDeathNote();
    }

    private void addDeathNote() {
        // God Fruit
        ItemStack item = getDeathItem();
        NamespacedKey key = new NamespacedKey(this, "death_note");
        ShapedRecipe sr = new ShapedRecipe(key, item);
        sr.shape("SSS", "SAS", "SSS");
        sr.setIngredient('S', Material.NETHER_STAR);
        sr.setIngredient('A', Material.ENCHANTED_BOOK);
        Bukkit.addRecipe(sr);
    }

    private void addDevilFruit() {
        // God Fruit
        ItemStack item = getDevilItem();
        NamespacedKey key = new NamespacedKey(this, "devil_fruit");
        ShapedRecipe sr = new ShapedRecipe(key, item);
        sr.shape("ASA", "SGS", "ASA");
        sr.setIngredient('S', Material.FIRE_CORAL);
        sr.setIngredient('G', Material.ENCHANTED_GOLDEN_APPLE);
        Bukkit.addRecipe(sr);
    }

    private void addGodFruit() {
        // God Fruit
        ItemStack item = getGodItem();
        NamespacedKey key = new NamespacedKey(this, "god_fruit");
        ShapedRecipe sr = new ShapedRecipe(key, item);
        sr.shape("GGG", "GFG", "ABC");
        sr.setIngredient('G', Material.GOLD_BLOCK);
        sr.setIngredient('F', Material.APPLE);
        sr.setIngredient('A', Material.EMERALD);
        sr.setIngredient('B', Material.YELLOW_DYE);
        sr.setIngredient('C', Material.LAPIS_LAZULI);
        Bukkit.addRecipe(sr);
    }

    private void addIllagerBell() {
        // Illager Bell
        ItemStack bell = getIllagerItem();
        NamespacedKey key = new NamespacedKey(this, "illager_bell");
        ShapedRecipe sr = new ShapedRecipe(key, bell);
        sr.shape("GGG", "ABC", "GGG");
        sr.setIngredient('G', Material.GOLD_INGOT);
        sr.setIngredient('A', Material.QUARTZ);
        sr.setIngredient('B', Material.GREEN_DYE);
        sr.setIngredient('C', Material.CLAY_BALL);
        Bukkit.addRecipe(sr);
    }

    private void addWizardBook() {
        // Illager Bell
        ItemStack item = getWizardItem();
        NamespacedKey key = new NamespacedKey(this, "wizard_book");
        ShapedRecipe sr = new ShapedRecipe(key, item);
        sr.shape("LLL", "APB", "PPP");
        sr.setIngredient('L', Material.LEATHER);
        sr.setIngredient('P', Material.PAPER);
        sr.setIngredient('A', Material.COAL);
        sr.setIngredient('B', Material.RED_DYE);
        Bukkit.addRecipe(sr);
    }

    private void addGiantPotion() {
        // Giant Potion
        ItemStack item = getGiantIem();
        NamespacedKey key = new NamespacedKey(this, "giant_potion");
        ShapedRecipe sr = new ShapedRecipe(key, item);
        sr.shape("GGG", "ABC", "GWG");
        sr.setIngredient('G', Material.GLASS);
        sr.setIngredient('W', Material.WATER_BUCKET);
        sr.setIngredient('A', Material.GREEN_DYE);
        sr.setIngredient('B', Material.RED_DYE);
        sr.setIngredient('C', Material.BROWN_DYE);
        Bukkit.addRecipe(sr);
    }

    private void addDragonEgg() {
        // Giant Potion
        ItemStack item = getGiantIem();
        NamespacedKey key = new NamespacedKey(this, "dragon_egg");
        ShapedRecipe sr = new ShapedRecipe(key, item);
        sr.shape("OOO", "OAO", "OBO");
        sr.setIngredient('O', Material.OBSIDIAN);
        sr.setIngredient('A', Material.QUARTZ);
        sr.setIngredient('B', Material.COAL);
        Bukkit.addRecipe(sr);
    }

    private static List<Chunk> getNearbyChunks(Location l, int range) {
        List<Chunk> chunkList = new ArrayList<Chunk>();
        World world = l.getWorld();
        int chunks = range / 16 + 1;
        for (int x = l.getChunk().getX() - chunks; x < l.getChunk().getX() + chunks; x++) {
            for (int z = l.getChunk().getZ() - chunks; z < l.getChunk().getZ() + chunks; z++) {
                Chunk chunk = world.getChunkAt(x, z);
                if ((chunk != null) && (chunk.isLoaded())) {
                    chunkList.add(chunk);
                }
            }
        }
        return chunkList;
    }

    private static List<Entity> getEntitiesInNearbyChunks(Location l, int range, List<EntityType> entityTypes) {
        List<Entity> entities = new ArrayList<Entity>();
        for (Chunk chunk : getNearbyChunks(l, range)) {
            if (entityTypes == null) {
                entities.addAll(Arrays.asList(chunk.getEntities()));
            } else {
                Entity[] arrayOfEntity;
                int j = (arrayOfEntity = chunk.getEntities()).length;
                for (int i = 0; i < j; i++) {
                    Entity e = arrayOfEntity[i];
                    if (entityTypes.contains(e.getType())) {
                        entities.add(e);
                    }
                }
            }
        }
        return entities;
    }

    private static List<Entity> getNearbyEntities(Location l, float range, List<EntityType> entityTypes) {
        List<Entity> entities = new ArrayList<Entity>();
        for (Entity e : getEntitiesInNearbyChunks(l, (int) range, entityTypes)) {
            if (e.getLocation().getWorld().getName().equals(l.getWorld().getName()))
                if (e.getLocation().distance(l) <= range) {
                    entities.add(e);
                }
        }
        return entities;
    }

    private ItemStack getItem(Material mat, String name, int amount, List<String> loreList) {
        ItemStack item = new ItemStack(mat, amount);
        ItemMeta m = item.getItemMeta();
        if (name != null)
            m.setDisplayName(name);
        if (loreList != null)
            m.setLore(loreList);
        item.setItemMeta(m);
        return item;
    }

    public double rand(double mind, double maxd) {
        int min = (int) (mind * 10.0D);
        int max = (int) (maxd * 10.0D);
        int r = rand(min, max);
        return r / 10.0D;
    }

    @SuppressWarnings("unchecked")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if ((cmd.getName().equals("bossland")) || (cmd.getName().equals("bl"))) {
            try {
                if (args[0].equals("guide")) {
                    if (sender instanceof Player p) {
                        YamlConfiguration config = YamlConfiguration.loadConfiguration(bookYML);
                        ItemStack guideBook = config.getItemStack("guidebook");
                        p.getInventory().addItem(guideBook);
                    }
                    return true;
                }else if (args[0].equals("reload")) {
                    reloadConfig();
                    reloadLang();
                    sender.sendMessage("§eBossLand: Reloaded config!");
                    return true;
                } else if (args[0].equals("spawn") && args.length == 2) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (getConfig().getString("bosses." + args[1]) != null) {
                            spawnBoss(p, p.getLocation(), args[1]);
                            sender.sendMessage("§eBossLand: Spawned a " + args[1] + " boss!");
                        } else
                            bossError(sender);
                    }
                    return true;
                } else if (args[0].equals("cspawn") && args.length == 6) {
                    if (getConfig().getString("bosses." + args[1]) != null) {
                        World w = getServer().getWorld(args[5]);
                        if (w == null) {
                            sender.sendMessage("§cWorld not found!");
                            return true;
                        }
                        Location l = new Location(w, Integer.parseInt(args[2]), Integer.parseInt(args[3]),
                                Integer.parseInt(args[4]));
                        spawnBoss(null, l, args[1]);
                        sender.sendMessage("§eBossLand: Spawned a " + args[1] + " boss at the coords!");
                    } else
                        bossError(sender);
                    return true;
                } else if (args[0].equals("loot") && args.length == 2) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (getConfig().getString("bosses." + args[1]) != null) {
                            // ExperienceOrb orb = (ExperienceOrb) p.getWorld().spawnEntity(p.getLocation(),
                            // EntityType.EXPERIENCE_ORB);
                            String bossType = args[1];
                            // orb.setExperience(getConfig().getInt("bosses."+bossType+".dropedXP"));
                            ItemStack s = getLoot(p, bossType);
                            if (s != null && (!s.getType().equals(Material.AIR))) {
                                p.getInventory().addItem(s);
                                sender.sendMessage("§eBossLand: Dropped " + bossType + " boss loot!");
                            } else
                                sender.sendMessage("§eBossLand: Loot Error!");
                        } else
                            bossError(sender);
                    }
                    return true;
                } else if (args[0].equals("sloot") && args.length == 3) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (getConfig().getString("bosses." + args[1]) != null) {
                            // ExperienceOrb orb = (ExperienceOrb) p.getWorld().spawnEntity(p.getLocation(),
                            // EntityType.EXPERIENCE_ORB);
                            String bossType = args[1];
                            // orb.setExperience(getConfig().getInt("bosses."+bossType+".dropedXP"));
                            // ItemStack s = getLoot(p, bossType);
                            try {
                                ItemStack s = getItem(bossType, args[2]);
                                if (s != null && (!s.getType().equals(Material.AIR))) {
                                    p.getInventory().addItem(s);
                                    sender.sendMessage("§eBossLand: Dropped " + bossType + " boss loot!");
                                } else
                                    sender.sendMessage("§eBossLand: Loot Error!");
                            } catch (Exception x) {
                                sender.sendMessage("§eBossLand: A drop does not exsist for that ID.");
                            }
                        } else
                            bossError(sender);
                        return true;
                    }
                } else if ((args[0].equalsIgnoreCase("setLoot") || args[0].equalsIgnoreCase("addLoot"))
                        && args.length >= 2) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (getConfig().getString("bosses." + args[1]) != null) {
                            String bossType = args[1];
                            int id;
                            if (args[0].equalsIgnoreCase("addLoot")) {
                                id = 0;
                                while (getConfig().getString("bosses." + bossType + ".loot." + id) != null) {
                                    id = id + 1;
                                    if (id > 999) {
                                        this.getLogger().log(Level.SEVERE,
                                                "Add loot count for " + bossType + " exceded max!");
                                        return true;
                                    }
                                }
                            } else
                                id = Integer.parseInt(args[2]);
                            ItemStack s = p.getInventory().getItemInMainHand();
                            if (!s.getType().equals(Material.AIR)) {
                                setItem(s, "bosses." + bossType + ".loot." + id, getConfig());
                                sender.sendMessage("§eBossLand: Loot at " + id + " for boss " + bossType + " set!");
                            } else
                                sender.sendMessage("§eBossLand: No item is in your hand!");
                        } else
                            bossError(sender);
                    }
                    return true;
                } else if (args[0].equals("killBosses") && args.length == 2) {
                    World w = getServer().getWorld(args[1]);
                    if (w == null) {
                        sender.sendMessage("§cWorld not found!");
                        return true;
                    }
                    HashMap<Entity, BossBar> pm = (HashMap<Entity, BossBar>) bossMap.clone();
                    for (Map.Entry<Entity, BossBar> i : pm.entrySet())
                        if (i.getKey().getLocation().getWorld().equals(w)) {
                            ((LivingEntity) i.getKey()).damage(999 * 999);
                        }
                    sender.sendMessage("§eBossLand: Removed all bosses from the world.");
                    return true;
                }
            } catch (Exception e) {
            }
            sender.sendMessage("§6--- Boss Land v"
                    + Bukkit.getServer().getPluginManager().getPlugin("BossLand").getDescription().getVersion()
                    + " ---");
            sender.sendMessage("§e/bl guide <- Provides Guide");
            sender.sendMessage("§e/bl spawn <boss> <- Spawns a Boss");
            sender.sendMessage("§e/bl cspawn <boss> <x> <y> <z> <world>");
            sender.sendMessage("§e/bl loot <boss>   <- Drops a random loot");
            sender.sendMessage("§e/bl sloot <boss> <id> <- Drops specific loot");
            sender.sendMessage("§e/bl setLoot <boss> <id> <- Set loot for boss");
            sender.sendMessage("§e/bl addLoot <boss>     <- Add loot for boss");
            sender.sendMessage("§e/bl killBosses <world>  <- Remove bosses");
            sender.sendMessage("§e/bl reload         <- Re-loads the config");
        }
        return true;
    }

    private void bossError(CommandSender sender) {
        sender.sendMessage("§cInvalid Boss Type!");
        sender.sendMessage("§eValid Bosses are: ");
        for (String b : getConfig().getConfigurationSection("bosses").getKeys(false))
            sender.sendMessage(b);
    }

    // class WorldGuardMethods{
    // WorldGuardMethods() {}
    //
    //// private WorldGuardPlugin getWorldGuard(){
    //// Plugin plugin =
    // BossLand.this.getServer().getPluginManager().getPlugin("WorldGuard");
    //// if ((plugin == null) || (!(plugin instanceof WorldGuardPlugin))) {
    //// return null;
    //// }
    //// return (WorldGuardPlugin)plugin;
    //// }
    //
    //// public boolean canSpawn(Player p, Location l) {
    //// boolean build = false;
    //// try{
    //// //RegionContainer container =
    // WorldGuard.getInstance().getPlatform().getRegionContainer();
    //// //RegionManager regionManager =
    // container.get(BukkitAdapter.adapt(l.getWorld()));
    //// ApplicableRegionSet set =
    // WorldGuard.getInstance().getPlatform().getRegionContainer().get(new
    // BukkitWorld(l.getWorld())).getApplicableRegions(BlockVector3.at(l.getX(),l.getY(),l.getZ()));
    //// if (!set.getRegions().isEmpty())
    //// for(ProtectedRegion i : set.getRegions()) {
    //// build = queryBuild(p,l);
    //// }
    //// }catch (Exception x) {x.printStackTrace();}
    //// return build;
    //// }
    //
    // private boolean queryBuild(Player player, Location loc) {
    // if(player == null)
    // return true;
    // BukkitPlayer wgPlayer = (BukkitPlayer) BukkitAdapter.adapt(player);
    // BukkitRegionContainer container =
    // WorldGuard.getInstance().getPlatform().getRegionContainer();
    // RegionQuery query = container.createQuery();
    //
    // // Can't build
    // return query.testState(BukkitAdapter.adapt(loc), wgPlayer, Flags.BUILD);
    // }
    //
    //// @SuppressWarnings("deprecation")
    //// public boolean canSpawn(Location l) {
    //// boolean build = false;
    //// try{
    //// WorldGuardPlugin wg = getWorldGuard();
    //// RegionManager regionManager = wg.getRegionManager(l.getWorld());
    //// ApplicableRegionSet set = regionManager.getApplicableRegions(l);
    ////
    //// ProtectedRegion r = regionManager.getRegion("__global__");
    //// State s = (State)r.getFlag(DefaultFlag.BUILD);
    //// if (s.toString().equals("DENY")) {
    //// build = false;
    //// }else{
    //// build = true;
    //// }
    //// if (!set.getRegions().isEmpty()) {
    //// if (set.allows(DefaultFlag.BLOCK_PLACE)) {
    //// build = true;
    //// }else{
    //// return false;
    //// }
    //// }
    //// }catch (Exception localException) {}
    //// return build;
    //// }
    //
    // }

    static class LevelledEnchantment {
        public Enchantment getEnchantment;
        public int getLevel;

        LevelledEnchantment(Enchantment enchantment, int level) {
            getEnchantment = enchantment;
            getLevel = level;
        }
    }
}