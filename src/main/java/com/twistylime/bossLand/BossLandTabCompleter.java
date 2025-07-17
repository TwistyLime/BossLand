package com.twistylime.bossLand;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class BossLandTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            suggestions.addAll(Arrays.asList("guide","spawn", "cspawn", "loot", "sloot", "addLoot", "setLoot", "killBosses", "reload"));
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("spawn") || args[0].equalsIgnoreCase("loot") ||
                args[0].equalsIgnoreCase("sloot") || args[0].equalsIgnoreCase("addLoot") ||
                args[0].equalsIgnoreCase("setLoot"))) {
            // Suggest available boss types
            ConfigurationSection bossSection = Objects.requireNonNull(Bukkit.getPluginManager().getPlugin("BossLand"))
                    .getConfig().getConfigurationSection("bosses");

            if (bossSection != null) {
                suggestions.addAll(bossSection.getKeys(false));
            }
        }

        return suggestions;
    }
}
