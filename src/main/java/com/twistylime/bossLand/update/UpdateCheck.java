package com.twistylime.bossLand.update;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Level;

public class UpdateCheck {

    private final Plugin plugin;
    private final String versionUrl;

    public UpdateCheck(Plugin plugin) {
        this.plugin = plugin;
        this.versionUrl = plugin.getConfig().getString("update-check.version-url");
    }

    public void checkForUpdates() {
        if (!plugin.getConfig().getBoolean("update-check.enabled", true)) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                JSONObject data = getJsonObject();
                String latest = data.getString("full_version").trim();
                String current = plugin.getDescription().getVersion().trim();

                if (!latest.equalsIgnoreCase(current)) {
                    Bukkit.getConsoleSender().sendMessage("§e[BossLand] New version available: §a" + latest + "§e (current: §c" + current + "§e)");
                    if (data.has("changelog")) {
                        Bukkit.getConsoleSender().sendMessage("§6Changelog: " + data.getString("changelog"));
                    }
                } else {
                    Bukkit.getConsoleSender().sendMessage("§a[BossLand] You are using the latest version of the plugin (" + current + ").");
                }

            } catch (Exception e) {
                Bukkit.getConsoleSender().sendMessage("§c[BossLand] Failed to check for updates: " + e.getMessage());
            }
        });
    }

    private JSONObject getJsonObject() throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(versionUrl).openConnection();
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3000);

        InputStream inputStream = connection.getInputStream();
        String json;
        try (Scanner scanner = new Scanner(inputStream)) {
            scanner.useDelimiter("\\A");
            json = scanner.hasNext() ? scanner.next() : "";
        }

        return new JSONObject(json);
    }
}
