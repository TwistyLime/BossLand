package com.twistylime.bossLand.guidebook.pages;

import com.twistylime.bossLand.guidebook.PaginatedMenu;
import com.twistylime.bossLand.guidebook.menuutility.PlayerMenuUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class IndividualBossRecipePageMenu extends PaginatedMenu {
    public IndividualBossRecipePageMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "BossLand | "+super.playerMenuUtility.getBossRecipeToShow().toUpperCase().replace('_',' ');
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack itemClicked = e.getCurrentItem();
        Player playerWhoClicked = (Player) e.getWhoClicked();
        if(itemClicked != null){
            switch (itemClicked.getType()){
                case BARRIER:
                    playerWhoClicked.closeInventory();
                    break;
                case NAME_TAG:
                    playerWhoClicked.sendMessage("Need to implement search functionality");
                    playerWhoClicked.closeInventory();
                    break;
                case WRITABLE_BOOK:
                    new BossesPageMenu(playerMenuUtility).open();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setMenuItems() {
        addBossRecipeTemplate();
        String recipeKey = super.playerMenuUtility.getBossRecipeToShow();
        super.playerMenuUtility.getOwner().sendMessage("Recipe selected is "+recipeKey); // Need to add recipe here
    }
}
