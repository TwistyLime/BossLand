package com.twistylime.bossLand.utility;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public class SkullCreator {

    private static final boolean HAS_PLAYER_PROFILE = hasPlayerProfile();

    public static ItemStack getSkull(String url) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if (url == null || url.isEmpty()) return head;

        SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
        assert skullMeta != null;

        if (HAS_PLAYER_PROFILE) {
            try {
                // Reflectively get PlayerProfile and PlayerTextures classes
                Class<?> playerProfileClass = Class.forName("org.bukkit.profile.PlayerProfile");
                Class<?> playerTexturesClass = Class.forName("org.bukkit.profile.PlayerTextures");

                // Create PlayerProfile: Bukkit.createPlayerProfile(UUID)
                Method createProfile = Bukkit.class.getMethod("createPlayerProfile", UUID.class);
                Object profile = createProfile.invoke(null, UUID.randomUUID());

                // PlayerTextures textures = profile.getTextures();
                Method getTextures = playerProfileClass.getMethod("getTextures");
                Object textures = getTextures.invoke(profile);

                // Call textures.setSkin(new URL(url))
                Method setSkin = playerTexturesClass.getMethod("setSkin", URL.class);
                setSkin.invoke(textures, new URL(url));

                // profile.setTextures(textures)
                Method setTextures = playerProfileClass.getMethod("setTextures", playerTexturesClass);
                setTextures.invoke(profile, textures);

                // skullMeta.setOwnerProfile(profile)
                Method setOwnerProfile = SkullMeta.class.getMethod("setOwnerProfile", playerProfileClass);
                setOwnerProfile.invoke(skullMeta, profile);

            } catch (MalformedURLException e) {
                System.err.println("Invalid URL for skin: " + url);
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("Error applying skin via PlayerProfile.");
                e.printStackTrace();
            }

        } else {
            // 1.16–1.18 fallback via GameProfile
            try {
                String base64 = Base64.getEncoder().encodeToString((
                        "{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}"
                ).getBytes());

                Class<?> gameProfileClass = Class.forName("com.mojang.authlib.GameProfile");
                Class<?> propertyClass = Class.forName("com.mojang.authlib.properties.Property");

                Object profile = gameProfileClass
                        .getConstructor(UUID.class, String.class)
                        .newInstance(UUID.randomUUID(), null);

                Object property = propertyClass
                        .getConstructor(String.class, String.class)
                        .newInstance("textures", base64);

                Object properties = gameProfileClass.getMethod("getProperties").invoke(profile);
                properties.getClass().getMethod("put", Object.class, Object.class)
                        .invoke(properties, "textures", property);

                Field profileField = skullMeta.getClass().getDeclaredField("profile");
                profileField.setAccessible(true);
                profileField.set(skullMeta, profile);
            } catch (Exception e) {
                System.err.println("Error applying skin via GameProfile.");
                e.printStackTrace();
            }
        }

        head.setItemMeta(skullMeta);
        return head;
    }

    private static boolean hasPlayerProfile() {
        try {
            Class.forName("org.bukkit.profile.PlayerProfile");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
