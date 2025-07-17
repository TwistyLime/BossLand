package com.twistylime.bossLand;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BossLandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        List<String> bossNames = new ArrayList<>();
        Plugin plugin = Bukkit.getPluginManager().getPlugin("BossLand");
        if (plugin != null && plugin.getConfig().contains("bosses")) {
            ConfigurationSection section = plugin.getConfig().getConfigurationSection("bosses");
            if (section != null) {
                bossNames.addAll(section.getKeys(false));
            }
        }

        if (args.length == 1) {
            suggestions.addAll(Arrays.asList("guide","spawn", "cspawn", "loot", "sloot", "addLoot", "setLoot", "killBosses", "reload"));
        } else if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "spawn":
                case "loot":
                case "sloot":
                case "setloot":
                case "addloot":
                case "cspawn":
                    suggestions.addAll(bossNames);
                    break;
                case "killbosses":
                    for (World w : Bukkit.getWorlds()) {
                        suggestions.add(w.getName());
                    }
                    break;
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("setLoot") || args[0].equalsIgnoreCase("sloot")) {
                suggestions.add("<id>");
            } else if (args[0].equalsIgnoreCase("cspawn")) {
                suggestions.add("<x>");
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("cspawn")) {
            suggestions.add("<y>");
        } else if (args.length == 5 && args[0].equalsIgnoreCase("cspawn")) {
            suggestions.add("<z>");
        } else if (args.length == 6 && args[0].equalsIgnoreCase("cspawn")) {
            for (World w : Bukkit.getWorlds()) {
                suggestions.add(w.getName());
            }
        }

        return suggestions;
    }
}
