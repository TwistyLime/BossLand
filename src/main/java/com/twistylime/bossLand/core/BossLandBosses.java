package com.twistylime.bossLand.core;

import com.twistylime.bossLand.BossLand;
import com.twistylime.bossLand.config.BossLandConfiguration;
import com.twistylime.bossLand.utility.CompatibilityResolver;
import com.twistylime.bossLand.utility.MCUtility;
import com.twistylime.bossLand.utility.SkullCreator;
import com.twistylime.bossLand.worldguard.WorldGuardCompatibility;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.logging.Level;

public class BossLandBosses {
    private final JavaPlugin plugin;
    private final BossLandConfiguration config;
    private final BossLandItems itemManager;
    HashMap<Entity, BossBar> bossMap = new HashMap<>();
    HashMap<Entity, Entity> targetMap = new HashMap<>();

    public BossLandBosses(JavaPlugin plugin, BossLandConfiguration config, BossLandItems itemManager) {
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;
    }

    public HashMap<Entity, BossBar> getBossMapClone(){
        return (HashMap<Entity, BossBar>) bossMap.clone();
    }

    public HashMap<Entity, BossBar> getBossMapActual(){
        return (HashMap<Entity, BossBar>) bossMap;
    }

    public HashMap<Entity, Entity> getTargetMapActual(){
        return (HashMap<Entity, Entity>) targetMap;
    }

    public void spawnBoss(Player p, Location l, String bossType) {
        try {
            // Check Disabled Worlds
            if (plugin.getConfig().getList("disabledWorlds").contains(l.getWorld().getName())) {
                for (Entity e : MCUtility.getNearbyEntities(l, 24, new ArrayList<EntityType>(List.of(EntityType.PLAYER))))
                    ((Player) e).sendMessage(config.getLang("failSpawnWorld"));
                return;
            }

            // Check worldguard region Build Flag
            WorldGuardCompatibility worldGuardCheck = new WorldGuardCompatibility((BossLand) plugin);
            if(worldGuardCheck.isEnabled()){
                if(!worldGuardCheck.canBuild(p,l)){
                    for(Entity e : MCUtility.getNearbyEntities(l, 24, new ArrayList<>(List.of(EntityType.PLAYER))))
                        ((Player)e).sendMessage(config.getLang("failSpawnWG"));
                    return;
                }
            }

            // Check Boss Limit
            if (bossMap.size() >= plugin.getConfig().getInt("bossLimit")) {
                for (Entity e : MCUtility.getNearbyEntities(l, 24, new ArrayList<EntityType>(List.of(EntityType.PLAYER))))
                    ((Player) e).sendMessage(config.getLang("tooManyBosses"));
                return;
            }
            // Log Spawn
            plugin.getLogger().log(Level.INFO, "Spawn Boss: " + bossType);
            String entType = plugin.getConfig().getString("bosses." + bossType + ".entity");
            // //System.out.println("entType: " + entType);
            // //System.out.println("entType2: " + EntityType.valueOf(entType));
            // //System.out.println("entType3: " + EntityType.valueOf(entType));
            Entity boss = l.getWorld().spawnEntity(l, EntityType.valueOf(entType.toUpperCase()));
            // Slime
            if (boss instanceof Slime) {
                ((Slime) boss).setSize(10);
            } else if (boss instanceof Rabbit) {
                ((Rabbit) boss).setRabbitType(Rabbit.Type.THE_KILLER_BUNNY);
                ((Rabbit) boss).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 999 * 999, 1));
                ((Rabbit) boss).addPotionEffect(new PotionEffect(CompatibilityResolver.resolvePotionEffect("JUMP_BOOST","JUMP"), 999 * 999, 1));
                ((Rabbit) boss).addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 999 * 999, 1));
                // ((Rabbit)boss).setFireTicks(999*999);
                // ((Rabbit) boss).setCustomName(null);
            } else if (bossType.equals("WitherSkeletonKing") || bossType.equals("ZombieKing")) {
                MCUtility.equipMob(boss, "DIAMOND");
            } /**
             * else if(bossType.equals("IllagerKing")) {
             *
             * }
             **/
            else if (bossType.equals("PapaPanda")) {
                ((Panda) boss).setMainGene(Panda.Gene.AGGRESSIVE);
            } else if (bossType.equals("DrownedGod")) {
                MCUtility.equipMob(boss, "DIAMOND");
                // ItemStack head = getHead("5cf625ba-8f8e-4069-bcfe-af5fbb35a3f4","§b§lDrowned
                // God's Head");//"LeftShark"
                ItemStack head = SkullCreator.getSkull(
                        "http://textures.minecraft.net/texture/2d7a509789933b2640775f003a71dfb4f5d97aa23d804223029d295274deead1");
                ItemStack hand = new ItemStack(Material.TRIDENT);
                EntityEquipment ee = ((LivingEntity) boss).getEquipment();
                ee.setItemInMainHandDropChance(0.0F);
                ee.setHelmet(head);
                ee.setItemInMainHand(hand);
                // ((LivingEntity) boss).addPotionEffect(new
                // PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,999*999,2));
            } else if (bossType.equals("PharaohGod")) {
                MCUtility.equipMob(boss, "GOLDEN");
                // ItemStack head = getHead("73917135-da9d-4fd1-b032-158a7d1d03d1","§6§lPharaoh
                // God's Head");//"Sam1_6"
                ItemStack head = SkullCreator.getSkull(
                        "http://textures.minecraft.net/texture/51182cf65d180ecf08fab2311abed0cfcee960e6df5a3ba528f7ea47cc41f0a2");
                ItemStack hand = new ItemStack(Material.BLAZE_ROD);
                hand.addUnsafeEnchantment(Enchantment.KNOCKBACK, 5);
                hand.addUnsafeEnchantment(CompatibilityResolver.resolveEnchantment("DAMAGE_ALL","SHARPNESS"), 10);
                EntityEquipment ee = ((LivingEntity) boss).getEquipment();
                ee.setItemInMainHandDropChance(0.0F);
                ee.setHelmet(head);
                ee.setItemInMainHand(hand);
                ((LivingEntity) boss).addPotionEffect(new PotionEffect(CompatibilityResolver.resolvePotionEffect("SLOW","SLOWNESS"), 999 * 999, 1));
                ((LivingEntity) boss).addPotionEffect(new PotionEffect(CompatibilityResolver.resolvePotionEffect("DAMAGE_RESISTANCE","RESISTANCE"), 999 * 999, 2));
            } else if (bossType.equals("AetherGod")) {
                // System.out.println("Aether God 1");
                MCUtility.equipMob(boss, "DIAMOND");
                // ItemStack head = getHead("853c80ef-3c37-49fd-aa49-938b674adae6","§lAether
                // God's Head");//"jeb_"
                ItemStack head = SkullCreator.getSkull(
                        "http://textures.minecraft.net/texture/6545210b810f3d2db27c87f443a5fb812bb85d14d1922d08f50a2ebb1b248788");
                ItemStack hand = new ItemStack(Material.BOW);
                hand.addUnsafeEnchantment(CompatibilityResolver.resolveEnchantment("ARROW_DAMAGE","POWER"), 10);
                hand.addUnsafeEnchantment(CompatibilityResolver.resolveEnchantment("ARROW_KNOCKBACK","PUNCH"), 3);
                hand.addUnsafeEnchantment(CompatibilityResolver.resolveEnchantment("ARROW_FIRE","FLAME"), 3);
                EntityEquipment ee = ((LivingEntity) boss).getEquipment();
                ee.setItemInMainHandDropChance(0.0F);
                ee.setHelmet(head);
                ee.setItemInMainHand(hand);
                ((LivingEntity) boss).addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 999 * 999, 1));
                MCUtility.levitate(plugin,(LivingEntity) boss, true, targetMap);
                MCUtility.target(plugin,boss, 0.01,targetMap);
            } else if (bossType.equals("Demon")) {
                ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS, 1);
                ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
                for (ItemStack s : Arrays.asList(chest, pants, boots))
                    itemManager.dye(s, Color.MAROON);
                ItemStack head = SkullCreator.getSkull(
                        "http://textures.minecraft.net/texture/e00cd37a4ebcbb28cb85d75bbde7b7aad5a0f42bf4842f8da77dffdea18c1356");
                ItemStack hand = new ItemStack(Material.IRON_HOE);
                hand.addUnsafeEnchantment(CompatibilityResolver.resolveEnchantment("DAMAGE_ALL","SHARPNESS"), 10);
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
                ((LivingEntity) boss).addPotionEffect(new PotionEffect(CompatibilityResolver.resolvePotionEffect("INCREASE_DAMAGE","STRENGTH"), 999 * 999, 5));
            } else if (bossType.equals("Devil")) {
                ((PigZombie) boss).setAngry(true);
                ((PigZombie) boss).setAnger(999 * 999);
                MCUtility.equipMob(boss, "DIAMOND");
                ItemStack head = SkullCreator.getSkull(
                        "http://textures.minecraft.net/texture/9da39269ef45f825ec61bb4f8aa09bd3cf07996fb6fac338a6e91d6699ae425");
                ItemStack hand = new ItemStack(Material.ENCHANTED_BOOK);
                hand.addUnsafeEnchantment(CompatibilityResolver.resolveEnchantment("DAMAGE_ALL","SHARPNESS"), 999);
                EntityEquipment ee = ((LivingEntity) boss).getEquipment();
                ee.setItemInMainHandDropChance(0.0F);
                ee.setHelmet(head);
                ee.setItemInMainHand(hand);
                MCUtility.levitate(plugin,(LivingEntity) boss, true,targetMap);
                MCUtility.target(plugin,boss, 0.05,targetMap);
            } else if (bossType.equals("Death")) {
                ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
                ItemStack pants = new ItemStack(Material.LEATHER_LEGGINGS, 1);
                ItemStack boots = new ItemStack(Material.LEATHER_BOOTS, 1);
                for (ItemStack s : Arrays.asList(chest, pants, boots))
                    itemManager.dye(s, Color.BLACK);
                ItemStack head = SkullCreator.getSkull(
                        "http://textures.minecraft.net/texture/69e2f33eb180f0434916dc5d2bb326a6ea22fc9bbf988bc31a241fd4278023");
                ItemStack hand = new ItemStack(Material.IRON_HOE);
                hand.addUnsafeEnchantment(CompatibilityResolver.resolveEnchantment("DAMAGE_ALL","SHARPNESS"), 999);
                EntityEquipment ee = ((LivingEntity) boss).getEquipment();
                ee.setItemInMainHandDropChance(0.0F);
                ee.setHelmet(head);
                ee.setItemInMainHand(hand);
                ee.setChestplate(chest);
                ee.setLeggings(pants);
                // ee.setBoots(boots);
                MCUtility.levitate(plugin, (LivingEntity) boss, true, targetMap);
                MCUtility.target(plugin,boss, 0.2,targetMap);
            }
            // Mount
            if (plugin.getConfig().getString("bosses." + bossType + ".mount") != null) {
                LivingEntity mount = (LivingEntity) boss.getWorld().spawnEntity(boss.getLocation(),
                        EntityType.valueOf(plugin.getConfig().getString("bosses." + bossType + ".mount").toUpperCase()));
                mount.addPassenger(boss);
                int h = plugin.getConfig().getInt("bosses." + bossType + ".health");
                mount.getAttribute(CompatibilityResolver.resolveAttribute("MAX_HEALTH", "GENERIC_MAX_HEALTH")).setBaseValue(h);
                mount.setHealth(h);
                mount.addPotionEffect(new PotionEffect(CompatibilityResolver.resolvePotionEffect("DAMAGE_RESISTANCE","RESISTANCE"), 999 * 999, 10));
                if (mount.getType().equals(EntityType.BAT)) {
                    mount.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 999 * 999, 1));
                    mount.setInvulnerable(true);
                    if (bossType.equals("AetherGod"))
                        mount.addPotionEffect(new PotionEffect(CompatibilityResolver.resolvePotionEffect("SLOW","SLOWNESS"), 999 * 999, 2));
                }
                // mount.setPersistent(true);
            }
            // Stop Despawn
            // boss.setPersistent(true);
            // Save Boss
            config.setSaveData("bosses." + boss.getUniqueId().toString(), bossType);
            config.saveBossData();
            makeBoss(boss, bossType);
        } catch (Exception x) {
            plugin.getLogger().log(Level.SEVERE, "Failed to spawn Boss: " + bossType);
            x.printStackTrace();
        }
    }

    public void makeBoss(Entity ent, String bossType) {
        System.out.println("Make Boss");
        String title = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("bosses." + bossType + ".name"));
        BossBar bar = Bukkit.createBossBar(title,
                BarColor.valueOf(plugin.getConfig().getString("bosses." + bossType + ".barColor")),
                BarStyle.valueOf(plugin.getConfig().getString("bosses." + bossType + ".barStyle")), BarFlag.CREATE_FOG);
        bar.setVisible(true);
        bossMap.put(ent, bar);
        int maxHP = plugin.getConfig().getInt("bosses." + bossType + ".health");
        double maxHealth = ((LivingEntity) ent).getAttribute(CompatibilityResolver.resolveAttribute("MAX_HEALTH", "GENERIC_MAX_HEALTH")).getBaseValue();
        if (maxHealth != maxHP) {
            ((LivingEntity) ent).getAttribute(CompatibilityResolver.resolveAttribute("MAX_HEALTH", "GENERIC_MAX_HEALTH")).setBaseValue(maxHP);
            ((Damageable) ent).setHealth(maxHP);
        }
        // Name
        ent.setCustomName(plugin.getConfig().getString("bosses." + bossType + ".name").replace("&", "§"));
        ent.setCustomNameVisible(true);
        // Type Effects
        if (bossType.equals("KillerBunny")) {
            makeTrail(ent, plugin.getConfig().getString("bosses." + bossType + ".attackParticle"));
        }
        if (plugin.getConfig().getString("bosses." + bossType + ".auraParticle") != null) {
            // System.out.println("Aether God 2");
            makeTrail(ent, plugin.getConfig().getString("bosses." + bossType + ".auraParticle"));
        }
        if (bossType.equals("PharaohGod") || bossType.equals("Demon"))
            autoBalls((LivingEntity) ent, bossType);
        // No Despawn
        if (ent instanceof LivingEntity)
            ((LivingEntity) ent).setRemoveWhenFarAway(false);
    }

    private void makeTrail(final Entity e, final String effect) {
        if (e.isDead()) {
            return;
        }
        Location loc = e.getLocation();
        MCUtility.displayParticle(effect, loc);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                makeTrail(e, effect);
            }
        }, 1L);
    }

    public void autoBalls(final LivingEntity ent, final String bossType) {
        if (ent.isDead())
            return;
        for (Entity x : ent.getNearbyEntities(35, 35, 35))
            if (x instanceof Player) {
                if (bossType.equals("Death")) {
                    MCUtility.doDragonBalls(plugin,ent, (LivingEntity) x, bossType);
                } else
                    MCUtility.doFireBalls(plugin,ent, (LivingEntity) x, bossType);
            }
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            try {
                autoBalls(ent, bossType);
            } catch (Exception localException) {
            }
        }, 10 * 20);
    }

}
