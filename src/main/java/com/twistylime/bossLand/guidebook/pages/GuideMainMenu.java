package com.twistylime.bossLand.guidebook.pages;

import com.twistylime.bossLand.guidebook.PaginatedMenu;
import com.twistylime.bossLand.guidebook.menuutility.PlayerMenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;


public class GuideMainMenu extends PaginatedMenu {

    public GuideMainMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "BossLand Guide Book";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack itemClicked = e.getCurrentItem();
        Player p = (Player) e.getWhoClicked();
        if(itemClicked == null) return;
        switch (itemClicked.getType()){
            case BARRIER:
                p.closeInventory();
                break;
            case NAME_TAG:
                p.sendMessage(ChatColor.GOLD+"Enter the items/bosses to search:");
                p.closeInventory();
                break;
            case FIRE_CORAL:
                new BossesPageMenu(playerMenuUtility).open();
                break;
            case BLAZE_ROD:
                new ItemsPageMenu(playerMenuUtility).open();
                break;
            case LAPIS_LAZULI:
                new ShardsPageMenu(playerMenuUtility).open();
                break;
            case ENCHANTED_GOLDEN_APPLE:
                new RecipesPageMenu(playerMenuUtility).open();
                break;
            case NETHERITE_CHESTPLATE:
                new ArmorsPageMenu(playerMenuUtility).open();
                break;
            case PAPER:
                new CreditsPageMenu(playerMenuUtility).open();
                break;
            case NETHER_STAR:
                new InfoPageMenu(playerMenuUtility).open();
                break;
            default:
                break;
        }
    }

    @Override
    public void setMenuItems() {
        addSmallMenuBorder();

        inventory.setItem(10, decorateItem(Material.FIRE_CORAL, "Bosses", true, "Summon and fight", "Powerful custom bosses"));
        inventory.setItem(11, decorateItem(Material.BLAZE_ROD, "Items", false, "Unique drops", "Craft legendary gear"));
        inventory.setItem(12, decorateItem(Material.LAPIS_LAZULI, "Shards", true, "Boss shards", "Used for crafting recipes"));
        inventory.setItem(13, decorateItem(Material.ENCHANTED_GOLDEN_APPLE, "Recipes", false, "Learn how to craft", "Powerful items"));
        inventory.setItem(14, decorateItem(Material.NETHERITE_CHESTPLATE, "Armors", true, "Custom armor sets", "With special abilities"));
        inventory.setItem(15, decorateItem(Material.PAPER, "Credits", false, "Developed by", "§eTwistyLime"));
        inventory.setItem(16, decorateItem(Material.NETHER_STAR, "About Plugin", false, "§7BossLand plugin", "§7Version 1.0"));
    }


    private ItemStack decorateItem(Material material, String displayName, boolean glint, String... lore) {
        ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName("§6§l" + displayName);

            List<String> loreList = new ArrayList<>();
            for (String line : lore) {
                loreList.add("§7" + line);
            }
            meta.setLore(loreList);

            if (glint) {
                meta.addEnchant(Enchantment.MENDING, 1, true);
                meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }

            item.setItemMeta(meta);
        }
        return item;
    }

}
