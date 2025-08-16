package com.twistylime.bossLand.core;

import org.bukkit.enchantments.Enchantment;

class LevelledEnchantment {
    public Enchantment getEnchantment;
    public int getLevel;

    LevelledEnchantment(Enchantment enchantment, int level) {
        getEnchantment = enchantment;
        getLevel = level;
    }
}
