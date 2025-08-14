package com.twistylime.bossLand.guidebook.pages;

import com.twistylime.bossLand.guidebook.PaginatedMenu;
import com.twistylime.bossLand.guidebook.menuutility.PlayerMenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;


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

        inventory.setItem(10,createItem(Material.FIRE_CORAL,"Bosses", true));
        inventory.setItem(11,createItem(Material.BLAZE_ROD,"Items", false));
        inventory.setItem(12,createItem(Material.LAPIS_LAZULI,"Shards", true));
        inventory.setItem(13,createItem(Material.ENCHANTED_GOLDEN_APPLE,"Recipes", false));
        inventory.setItem(14,createItem(Material.NETHERITE_CHESTPLATE,"Armors", true));
        inventory.setItem(15,createItem(Material.PAPER,"Credits", false));
        inventory.setItem(16,createItem(Material.NETHER_STAR,"About Plugin", false));
    }
}
