package com.twistylime.bossLand.guidemenu;

import com.twistylime.bossLand.BossLand;
import com.twistylime.bossLand.utility.CompatibilityResolver;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class GuideItem {
    private final BossLand plugin;

    public GuideItem(BossLand plugin) {
        this.plugin = plugin;
    }

    public ItemStack createItem(Map<String, Object> item, String tag){
        String name = item.get("text").toString();
        boolean glint = item.get("glint").toString().equals("true");
        List<String> iconList = (List<String>) item.get("icon");
        String[] iconArray = iconList.toArray(new String[0]);
        List<String> loreList = (List<String>) item.get("lore");
        NamespacedKey key = new NamespacedKey(plugin,"object_tag");

        ItemStack guideItem = new ItemStack(CompatibilityResolver.resolveMaterial(iconArray));
        ItemMeta guideItemMeta = guideItem.getItemMeta();
        assert guideItemMeta != null;
        guideItemMeta.setDisplayName(name);
        guideItemMeta.setLore(loreList);
        if(glint) guideItemMeta.addEnchant(Enchantment.LUCK,1,true);
        guideItemMeta.addItemFlags(ItemFlag.values());
        guideItemMeta.getPersistentDataContainer().set(key, PersistentDataType.STRING,tag);
        guideItem.setItemMeta(guideItemMeta);

        return guideItem;
    }
}
