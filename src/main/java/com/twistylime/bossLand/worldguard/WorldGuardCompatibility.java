package com.twistylime.bossLand.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import com.twistylime.bossLand.BossLand;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldGuardCompatibility {

    private final boolean worldGuardPresent;

    public WorldGuardCompatibility(BossLand plugin) {
        this.worldGuardPresent = Bukkit.getPluginManager().isPluginEnabled("WorldGuard");
        if (worldGuardPresent) {
            plugin.getLogger().info("WorldGuard detected. Enabling region checks.");
        } else {
            plugin.getLogger().info("WorldGuard not detected. Skipping region checks.");
        }
    }

    /**
     * Checks if WorldGuard is installed.
     */
    public boolean isEnabled() {
        return worldGuardPresent;
    }

    /**
     * Checks if a player can build at a given location using WG 7.x API.
     */
    public boolean canBuild(Player player, Location location) {
        if (!this.isEnabled()) {
            return true; // If WG is missing, always allow
        }

        RegionQuery query = WorldGuard.getInstance().getPlatform()
                .getRegionContainer()
                .createQuery();

        LocalPlayer wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player); // WG 7.x way
        return query.testState(BukkitAdapter.adapt(location), wgPlayer, Flags.BUILD);
    }
}
