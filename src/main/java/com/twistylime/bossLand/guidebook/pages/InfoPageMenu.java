package com.twistylime.bossLand.guidebook.pages;

import com.twistylime.bossLand.guidebook.PaginatedMenu;
import com.twistylime.bossLand.guidebook.menuutility.PlayerMenuUtility;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InfoPageMenu extends PaginatedMenu {

    public InfoPageMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "BossLand | Plugin Info";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack itemClicked = e.getCurrentItem();
        if(itemClicked != null){
            switch (itemClicked.getType()){
                case WRITABLE_BOOK:
                    new GuideMainMenu(playerMenuUtility).open();
                    break;
                case BARRIER:
                    e.getWhoClicked().closeInventory();
                case NAME_TAG:
                    e.getWhoClicked().sendMessage("Need to implement search functionality");
                    e.getWhoClicked().closeInventory();
                default:
                    break;
            }
        }
    }

    @Override
    public void setMenuItems() {
        addSmallMenuBorder();
    }
}
