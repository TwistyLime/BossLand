package com.twistylime.bossLand.command;

import com.twistylime.bossLand.BossLand;
import com.twistylime.bossLand.config.BossLandConfiguration;
import com.twistylime.bossLand.core.BossLandBosses;
import com.twistylime.bossLand.core.BossLandItems;
import com.twistylime.bossLand.core.BossLandLoot;
import com.twistylime.bossLand.guidebook.menuutility.PlayerMenuUtilityManager;
import com.twistylime.bossLand.guidebook.pages.GuideMainMenu;
import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;

public class BossLandCommandHandler implements CommandExecutor {

    private final BossLandConfiguration config;
    private final BossLand plugin;
    private final BossLandBosses bossManager;
    private final BossLandLoot lootManager;

    public BossLandCommandHandler(BossLandConfiguration config, BossLand plugin, BossLandBosses bossManager, BossLandLoot lootManager) {
        this.config = config;
        this.plugin = plugin;
        this.bossManager = bossManager;
        this.lootManager = lootManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        String version = Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("BossLand")).getDescription().getVersion();
        if ((cmd.getName().equals("bosslandadmin")) || (cmd.getName().equals("bl-admin"))) {
            try {
                if (args[0].equals("reload")) {
                    config.reloadConfigs();
                    sender.sendMessage("§eBossLand: Reloaded config!");
                    return true;
                }
                else if (args[0].equals("guide")) {
                    GuideMainMenu menu = new GuideMainMenu(PlayerMenuUtilityManager.getPlayerMenuUtility((Player) sender));
                    menu.open();
                    return true;
                }else if (args[0].equals("spawn") && args.length == 2) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (plugin.getConfig().getString("bosses." + args[1]) != null) {
                            bossManager.spawnBoss(p, p.getLocation(), args[1]);
                            sender.sendMessage("§eBossLand: Spawned a " + args[1] + " boss!");
                        } else
                            plugin.bossError(sender);
                    }
                    return true;
                } else if (args[0].equals("cspawn") && args.length == 6) {
                    if (plugin.getConfig().getString("bosses." + args[1]) != null) {
                        World w = plugin.getServer().getWorld(args[5]);
                        if (w == null) {
                            sender.sendMessage("§cWorld not found!");
                            return true;
                        }
                        Location l = new Location(w, Integer.parseInt(args[2]), Integer.parseInt(args[3]),
                                Integer.parseInt(args[4]));
                        bossManager.spawnBoss(null, l, args[1]);
                        sender.sendMessage("§eBossLand: Spawned a " + args[1] + " boss at the coords!");
                    } else
                        plugin.bossError(sender);
                    return true;
                } else if (args[0].equals("loot") && args.length == 2) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (plugin.getConfig().getString("bosses." + args[1]) != null) {
                            String bossType = args[1];
                            ItemStack s = plugin.getLoot(p, bossType);
                            if (s != null && (!s.getType().equals(Material.AIR))) {
                                p.getInventory().addItem(s);
                                sender.sendMessage("§eBossLand: Dropped " + bossType + " boss loot!");
                            } else
                                sender.sendMessage("§eBossLand: Loot Error!");
                        } else
                            plugin.bossError(sender);
                    }
                    return true;
                } else if (args[0].equals("sloot") && args.length == 3) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (plugin.getConfig().getString("bosses." + args[1]) != null) {
                            String bossType = args[1];
                            try {
                                ItemStack s = lootManager.getLootFromName(args[2],bossType);
                                if (s != null && (!s.getType().equals(Material.AIR))) {
                                    p.getInventory().addItem(s);
                                    sender.sendMessage("§eBossLand: Dropped " + bossType + " boss loot!");
                                } else
                                    sender.sendMessage("§eBossLand: A drop does not exsist for that ID.");
                            } catch (Exception x) {
                                sender.sendMessage("§eBossLand: Loot Error!");
                            }
                        } else
                            plugin.bossError(sender);
                        return true;
                    }
                } else if ((args[0].equalsIgnoreCase("setLoot") || args[0].equalsIgnoreCase("addLoot"))
                        && args.length >= 2) {
                    if (sender instanceof Player) {
                        Player p = (Player) sender;
                        if (plugin.getConfig().getString("bosses." + args[1]) != null) {
                            String bossType = args[1];
                            int id;
                            if (args[0].equalsIgnoreCase("addLoot")) {
                                id = 0;
                                while (plugin.getConfig().getString("bosses." + bossType + ".loot." + id) != null) {
                                    id = id + 1;
                                    if (id > 999) {
                                        plugin.getLogger().log(Level.SEVERE,
                                                "Add loot count for " + bossType + " exceded max!");
                                        return true;
                                    }
                                }
                            } else
                                id = Integer.parseInt(args[2]);
                            ItemStack s = p.getInventory().getItemInMainHand();
                            if (!s.getType().equals(Material.AIR)) {
                                plugin.setItem(s, "bosses." + bossType + ".loot." + id, plugin.getConfig());
                                sender.sendMessage("§eBossLand: Loot at " + id + " for boss " + bossType + " set!");
                            } else
                                sender.sendMessage("§eBossLand: No item is in your hand!");
                        } else
                            plugin.bossError(sender);
                    }
                    return true;
                } else if (args[0].equals("killBosses") && args.length == 2) {
                    World w = plugin.getServer().getWorld(args[1]);
                    if (w == null) {
                        sender.sendMessage("§cWorld not found!");
                        return true;
                    }
                    HashMap<Entity, BossBar> pm = (HashMap<Entity, BossBar>) bossManager.getBossMapClone();
                    for (Map.Entry<Entity, BossBar> i : pm.entrySet())
                        if (Objects.equals(i.getKey().getLocation().getWorld(), w)) {
                            ((LivingEntity) i.getKey()).damage(999 * 999);
                        }
                    sender.sendMessage("§eBossLand: Removed all bosses from the world.");
                    return true;
                } else if (args[0].equals("help") && args.length == 1){
                    sender.sendMessage("§6§lBoss Land §r§bv"+version);
                    sender.sendMessage("§3===============");
                    sender.sendMessage("§8■ §e/bl-admin help §7→ §fShows list of available commands");
                    sender.sendMessage("§8■ §e/bl-admin spawn <boss> §7→ §fSpawns a Boss");
                    sender.sendMessage("§8■ §e/bl-admin cspawn <boss> <x> <y> <z> <world> §7→ §fSpawns a Boss at coords in a world.");
                    sender.sendMessage("§8■ §e/bl-admin loot <boss> §7→ §fDrops a random loot");
                    sender.sendMessage("§8■ §e/bl-admin sloot <boss> <id> §7→ §fDrops specific loot");
                    sender.sendMessage("§8■ §e/bl-admin setLoot <boss> <id> §7→ §fSet loot for boss");
                    sender.sendMessage("§8■ §e/bl-admin addLoot <boss> §7→ §fAdd loot for boss");
                    sender.sendMessage("§8■ §e/bl-admin killBosses <world> §7→ §fRemove bosses");
                    sender.sendMessage("§8■ §e/bl-admin reload §7→ §fRe-loads the config");
                    return true;
                }
            } catch (Exception e) {
            }
            sender.sendMessage("§cInvalid Command! Use /bl-admin help to see the list of available commands.");
        }
        if ((cmd.getName().equals("bossland")) || (cmd.getName().equals("bl"))){
            if (args[0].equals("guide") && args.length == 1) {
                if (sender instanceof Player) {
                    Player p = (Player)sender;
                    ItemStack guideBook = new ItemStack(Material.WRITTEN_BOOK,1);
                    ItemMeta guideBookMeta = guideBook.getItemMeta();
                    if(guideBookMeta != null){
                        guideBookMeta.setDisplayName(config.getLang("items.guidebook"));
                        guideBookMeta.setLore(List.of(ChatColor.GRAY+"by TwistyLime, plugin by Eliminator"));
                    }
                    guideBook.setItemMeta(guideBookMeta);
                    p.getInventory().addItem(guideBook);
                }
                return true;
            } else if (args[0].equals("help") && args.length == 1) {
                if(sender instanceof Player){
                    sender.sendMessage("§6§lBoss Land §r§bv"+version);
                    sender.sendMessage("§3===============");
                    sender.sendMessage("§8■ §e/bl guide §7→ §fProvides Guide book");
                    sender.sendMessage("§8■ §e/bl help §7→ §fShows list of available commands");
                    sender.sendMessage("§8■ §e/bl info §7→ §fShows information about plugin.");
                }
                return true;
            } else if (args[0].equals("info") && args.length == 1) {
                if(sender instanceof Player){
                    String desc = Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("BossLand")).getDescription().getDescription();
                    sender.sendMessage("§6§lBoss Land §r§bv"+version);
                    sender.sendMessage("§3===============");
                    sender.sendMessage("§e"+desc);
                    sender.sendMessage("§8■ §3Former Author §7→ §fEliminator");
                    sender.sendMessage("§8■ §3Current Author §7→ §fTwistyLime");
                }
                return true;
            }
            sender.sendMessage("§cInvalid Command! Use /bl help to see the list of available commands.");
        }
        return true;
    }
}
