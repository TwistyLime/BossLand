package com.twistylime.bossLand.utility;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class MCUtility {

    public static void takeItem(Player p, int amount) {
        if (p.getInventory().getItemInMainHand().getAmount() > amount) {
            p.getInventory().getItemInMainHand().setAmount(p.getInventory().getItemInMainHand().getAmount() - amount);
        } else
            p.getInventory().setItemInMainHand(null);
    }

    public static List<Entity> getNearbyEntities(Location l, float range, List<EntityType> entityTypes) {
        List<Entity> entities = new ArrayList<>();
        for (Entity e : getEntitiesInNearbyChunks(l, (int) range, entityTypes)) {
            if (e.getLocation().getWorld().getName().equals(l.getWorld().getName()))
                if (e.getLocation().distance(l) <= range) {
                    entities.add(e);
                }
        }
        return entities;
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

    public static void equipMob(Entity mob, String type) {
        ItemStack helm = new ItemStack(Material.valueOf(type + "_HELMET"), 1);
        ItemStack chest = new ItemStack(Material.valueOf(type + "_CHESTPLATE"), 1);
        ItemStack pants = new ItemStack(Material.valueOf(type + "_LEGGINGS"), 1);
        ItemStack boots = new ItemStack(Material.valueOf(type + "_BOOTS"), 1);
        ItemStack sword = new ItemStack(Material.valueOf(type + "_SWORD"), 1);
        sword.addUnsafeEnchantment(CompatibilityResolver.resolveEnchantment("DAMAGE_ALL","SHARPNESS"), 4);
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

    public static void levitate(Plugin plugin, final LivingEntity e, boolean up, HashMap<Entity, Entity> targetMap) {
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
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                levitate(plugin, e, nup, targetMap);
            }
        }, (20 * 2));
    }

    public static void target(Plugin plugin,final Entity e, final double speed, HashMap<Entity, Entity> targetMap) {
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
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                try {
                    target(plugin,e, speed,targetMap);
                } catch (Exception localException) {
                }
            }
        }, 1L);
    }

    public static List<Block> getArea(Location loc1, Location loc2, boolean removeBottom) {
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

    public static ArrayList<Location> getArea(Location l, double r, double t) {
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

    public static void doDragonBalls(Plugin plugin, final LivingEntity ent, final LivingEntity dmgr, final String bossType) {
        Location l = ent.getEyeLocation();
        l.setY(l.getY() + 1);
        WitherSkull f = ent.launchProjectile(WitherSkull.class);
        moveToward(plugin, f, dmgr, 0.6);
        boomTimer(plugin, f, 5);
        makeTrail(plugin, f, plugin.getConfig().getString("bosses." + bossType + ".attackParticle"));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void doFireBalls(Plugin plugin, final LivingEntity ent, final LivingEntity dmgr, final String bossType) {
        int balls = 1;
        // EntityType bt = EntityType.FIREBALL;
        Class c = Fireball.class;
        double maxHealth = Objects.requireNonNull(ent.getAttribute(CompatibilityResolver.resolveAttribute("MAX_HEALTH", "GENERIC_MAX_HEALTH"))).getBaseValue();
        if (ent.getHealth() <= ((maxHealth / 4) * 3)) {
            // Phase 2
            balls = plugin.getConfig().getInt("bosses." + bossType + ".amountSpecial2");
            // bt = EntityType.SMALL_FIREBALL;
        } else if (ent.getHealth() <= ((maxHealth / 4) * 2)) {
            // Phase 3
            balls = plugin.getConfig().getInt("bosses." + bossType + ".amountSpecial3");
            // bt = EntityType.FIREBALL;
            c = LargeFireball.class;
        } else if ((ent.getHealth() <= (maxHealth / 4))) {
            // Phase 4
            balls = plugin.getConfig().getInt("bosses." + bossType + ".amountSpecial4");
            // bt = EntityType.DRAGON_FIREBALL;
            c = DragonFireball.class;
        }
        if (balls <= 0)
            balls = 1;
        // final EntityType fbt = bt;
        final Class fc = c;
        for (int i = 0; i < balls; i++)
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                Location l = ent.getEyeLocation();
                l.setY(l.getY() + 1);
                // Projectile f = (Projectile) ent.getWorld().spawnEntity(l, fbt);
                Projectile f = ent.launchProjectile(fc);
                moveToward(plugin, f, dmgr, 0.6);
                boomTimer(plugin, f, 5);
                makeTrail(plugin, f, plugin.getConfig().getString("bosses." + bossType + ".attackParticle"));
            }, 7L * i);
    }

    public static void moveToward(Plugin plugin, final Entity e, final Entity to, final double speed) {
        if (e.isDead()) {
            return;
        } else if (e.getLocation().distance(to.getLocation()) < .5)
            return;
        Location loc = to.getLocation();
        if (to instanceof LivingEntity)
            loc = ((LivingEntity) to).getEyeLocation();
        Vector direction = loc.toVector().subtract(e.getLocation().toVector()).normalize();
        e.setVelocity(direction.multiply(speed));
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            try {
                moveToward(plugin, e, to, speed);
            } catch (Exception localException) {
            }
        }, 1L);
    }

    public static void moveToward(Plugin plugin, final Entity e, final Location to, final double speed) {
        if (e.isDead()) {
            return;
        } else if (e.getLocation().distance(to) < .5)
            return;
        Vector direction = to.toVector().subtract(e.getLocation().toVector()).normalize();
        e.setVelocity(direction.multiply(speed));
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            try {
                moveToward(plugin,e, to, speed);
            } catch (Exception localException) {
            }
        }, 1L);
    }

    public static void boomTimer(Plugin plugin, final Entity p, int t) {
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            try {
                if (!p.isDead()) {
                    p.remove();
                    p.getWorld().createExplosion(p.getLocation(), 2, false);
                }
            } catch (Exception localException) {
            }
        }, t * 20L);
    }

    public static void displayParticle(String effect, Location loc) {
        // effect: particle:speed:amount:range
        String[] split = effect.split(":");
        int speed = Integer.parseInt(split[1]);
        int amount = Integer.parseInt(split[2]);
        double r = Double.parseDouble(split[3]);
        displayParticle(split[0], loc.getWorld(), loc.getX(), loc.getY(), loc.getZ(), r, speed, amount);
    }

    public static void displayParticle(String effect, Location l, double radius, int speed, int amount) {
        displayParticle(effect, l.getWorld(), l.getX(), l.getY(), l.getZ(), radius, speed, amount);
    }

    public static void displayParticle(String effect, World w, double x, double y, double z, double radius, int speed, int amount) {
        amount = (amount <= 0) ? 1 : amount;
        Location l = new Location(w, x, y, z);
        try {
            if (radius <= 0) {
                // Effect, Location, Count, X, Y, Z, Speed
                w.spawnParticle(Particle.valueOf(effect), l, amount, 0, 0, 0, speed);
                // w.spawnParticle(Particle.valueOf(effect), l, 0, 0, 0, speed, amount);
            } else {
                List<Location> ll = MCUtility.getArea(l, radius, 0.2);
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

    public static void makeTrail(Plugin plugin, final Entity e, final String effect) {
        if (e.isDead()) {
            return;
        }
        Location loc = e.getLocation();
        displayParticle(effect, loc);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            public void run() {
                makeTrail(plugin, e, effect);
            }
        }, 1L);
    }

    public static void lightningShow(Plugin plugin, final Location l, final int size) {
        // Boom 1
        l.getWorld().strikeLightningEffect(l);
        // Boom 2
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
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
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
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

    public static void boom(Plugin plugin, final Location l, final int size, final boolean fire) {
        // Boom 1
        l.getWorld().createExplosion(l, size, fire);
        // Boom 2
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
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
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
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
}
