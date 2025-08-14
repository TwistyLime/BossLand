package com.twistylime.bossLand.effects;

import com.twistylime.bossLand.BossLand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import com.twistylime.bossLand.utility.CompatibilityResolver;

public class ShardEffectListener implements Listener {
    private final BossLand plugin;
    private final Set<UUID> pluginEffectPlayers = new HashSet<>();

    public ShardEffectListener(BossLand plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        // Start repeating task to check all players
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    checkAndApplyHaste(player);
                }
            }
        }.runTaskTimer(plugin, 0L, 40L); // every 2 seconds
    }

    private static class ShardEffect {
        final String[] goodEffectNames;
        final String[] badEffectNames;
        final int durationTicks;
        final int amplifier;
        ShardEffect(String[] goodEffectNames, String[] badEffectNames, int durationTicks, int amplifier) {
            this.goodEffectNames = goodEffectNames;
            this.badEffectNames = badEffectNames;
            this.durationTicks = durationTicks;
            this.amplifier = amplifier;
        }
    }

    // Each entry: key = shard, value = (good effect, bad effect, duration, amplifier)
    private static final java.util.Map<String, ShardEffect> SHARD_EFFECTS = java.util.Map.ofEntries(
        // White Shard (Ghast Lord): Levitation (good), Weakness (bad)
        java.util.Map.entry("whiteshard", new ShardEffect(new String[]{"LEVITATION"}, new String[]{"WEAKNESS"}, 60, 1)),
        // Green Shard (King Slime): Jump Boost (good), Slowness (bad)
        java.util.Map.entry("greenshard", new ShardEffect(new String[]{"JUMP_BOOST", "JUMP"}, new String[]{"SLOW", "SLOWNESS"}, 60, 2)),
        // Grey Shard (Killer Bunny): Speed (good), Nausea (bad)
        java.util.Map.entry("greyshard", new ShardEffect(new String[]{"SPEED"}, new String[]{"BLINDNESS"}, 60, 2)),
        // Black Shard (Wither Skeleton King): Resistance (good), Wither (bad)
        java.util.Map.entry("blackshard", new ShardEffect(new String[]{"REGENERATION"}, new String[]{"WITHER"}, 60, 0)),
        // Red Shard (Zombie King): Strength (good), Hunger (bad)
        java.util.Map.entry("redshard", new ShardEffect(new String[]{"INCREASE_DAMAGE", "STRENGTH"}, new String[]{"HUNGER"}, 60, 1)),
        // Brown Shard (Papa Panda): Haste (good), Slowness (bad)
        java.util.Map.entry("brownshard", new ShardEffect(new String[]{"HASTE","FAST_DIGGING"}, new String[]{"SLOW", "SLOWNESS"}, 60, 2)),
        // Emerald Shard (Giant): Absorption (good), Mining Fatigue (bad)
        java.util.Map.entry("emeraldshard", new ShardEffect(new String[]{"ABSORPTION"}, new String[]{"SLOW_DIGGING", "MINING_FATIGUE"}, 60, 1)),
        // Gold Shard (Illager King): Regeneration (good), Blindness (bad)
        java.util.Map.entry("goldshard", new ShardEffect(new String[]{"DAMAGE_RESISTANCE", "RESISTANCE"}, new String[]{"BLINDNESS"}, 60, 1)),
        // Blue Shard (Evil Wizard): Night Vision (good), Poison (bad)
        java.util.Map.entry("blueshard", new ShardEffect(new String[]{"SATURATION"}, new String[]{"POISON"}, 60, 1)),
        // Demonic Shard (Demon): Fire Resistance (good), Weakness (bad)
        java.util.Map.entry("demonicshard", new ShardEffect(new String[]{"FIRE_RESISTANCE"}, new String[]{"WEAKNESS"}, 60, 2))
    );

    private void checkAndApplyHaste(Player player) {
        try {
            ItemStack item = player.getInventory().getItemInMainHand();
            if (item.hasItemMeta()) {
                ItemMeta meta = item.getItemMeta();
                if (meta != null && meta.hasDisplayName()) {
                    String displayName = meta.getDisplayName();
                    String shardKey = null;

                    if (displayName.equals(plugin.getLang("items.whiteshard"))) {
                        shardKey = "whiteshard";
                    } else if (displayName.equals(plugin.getLang("items.greenshard"))) {
                        shardKey = "greenshard";
                    } else if (displayName.equals(plugin.getLang("items.greyshard"))) {
                        shardKey = "greyshard";
                    } else if (displayName.equals(plugin.getLang("items.blackshard"))) {
                        shardKey = "blackshard";
                    } else if (displayName.equals(plugin.getLang("items.redshard"))) {
                        shardKey = "redshard";
                    } else if (displayName.equals(plugin.getLang("items.brownshard"))) {
                        shardKey = "brownshard";
                    } else if (displayName.equals(plugin.getLang("items.emeraldshard"))) {
                        shardKey = "emeraldshard";
                    } else if (displayName.equals(plugin.getLang("items.goldshard"))) {
                        shardKey = "goldshard";
                    } else if (displayName.equals(plugin.getLang("items.blueshard"))) {
                        shardKey = "blueshard";
                    } else if (displayName.equals(plugin.getLang("items.demonicshard"))) {
                        shardKey = "demonicshard";
                    }

                    if (shardKey != null && SHARD_EFFECTS.containsKey(shardKey)) {
                        ShardEffect effect = SHARD_EFFECTS.get(shardKey);

                        // Apply good effects
                        PotionEffectType goodType = CompatibilityResolver.resolvePotionEffect(effect.goodEffectNames);
                        if (goodType != null)
                            player.addPotionEffect(new PotionEffect(goodType, effect.durationTicks, effect.amplifier, true, false, true));
                        // Apply bad effects
                        PotionEffectType badType = CompatibilityResolver.resolvePotionEffect(effect.badEffectNames);
                        if (badType != null)
                            player.addPotionEffect(new PotionEffect(badType, effect.durationTicks, effect.amplifier, true, false, true));

                        pluginEffectPlayers.add(player.getUniqueId());
                        return;
                    }
                }
            }
        } catch (Exception ignored) {}

        // Remove all possible effects from all shards if not holding any
        if (pluginEffectPlayers.contains(player.getUniqueId())) {
            for (ShardEffect effect : SHARD_EFFECTS.values()) {
                for (String[] effectNames : new String[][]{effect.goodEffectNames, effect.badEffectNames}) {
                    PotionEffectType type = CompatibilityResolver.resolvePotionEffect(effectNames);
                    if (type != null) player.removePotionEffect(type);
                }
            }
            pluginEffectPlayers.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        pluginEffectPlayers.remove(playerId);
    }
} 