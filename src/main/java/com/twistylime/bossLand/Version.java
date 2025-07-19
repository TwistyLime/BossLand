package com.twistylime.bossLand;

import org.bukkit.Bukkit;

public class Version {
    private static final String VERSION = Bukkit.getBukkitVersion();
    private static final String NMS_VERSION = VERSION.substring(0,VERSION.indexOf('-'));

    public static boolean isVersion(String version) {
        return NMS_VERSION.equals(version);
    }

    public static boolean isVersionOrNewer(String version) {
        return compareVersions(NMS_VERSION, version) >= 0;
    }

    public static boolean isVersionRange(String minVersion, String maxVersion) {
        return compareVersions(NMS_VERSION, minVersion) >= 0 &&
                compareVersions(NMS_VERSION, maxVersion) <= 0;
    }

    public static String getServerVersion() {
        return NMS_VERSION;
    }

    // Simple version comparison for MC versions
    private static int compareVersions(String v1, String v2) {
        String[] parts1 = v1.replace("v", "").replace("_R", ".").split("\\.");
        String[] parts2 = v2.replace("v", "").replace("_R", ".").split("\\.");

        for (int i = 0; i < Math.max(parts1.length, parts2.length); i++) {
            int p1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
            int p2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;

            if (p1 != p2) return Integer.compare(p1, p2);
        }
        return 0;
    }
}