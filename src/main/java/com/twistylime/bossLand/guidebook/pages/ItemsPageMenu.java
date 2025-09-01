package com.twistylime.bossLand.guidebook.pages;

import com.twistylime.bossLand.BossLand;
import com.twistylime.bossLand.core.BossLandLoot;
import com.twistylime.bossLand.guidebook.PaginatedMenu;
import com.twistylime.bossLand.guidebook.menuutility.PlayerMenuUtility;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemsPageMenu extends PaginatedMenu {

    public ItemsPageMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "BossLand | Items Menu";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack itemClicked = e.getCurrentItem();
        if(itemClicked != null){
            switch(e.getCurrentItem().getType()){
                case BARRIER:
                    e.getWhoClicked().closeInventory();
                    break;
                case NAME_TAG:
                    e.getWhoClicked().sendMessage("This feature is work in progress and will be available in the upcoming updates.");
                    e.getWhoClicked().closeInventory();
                    break;
                case WRITABLE_BOOK:
                    new GuideMainMenu(playerMenuUtility).open();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setMenuItems() {
        addBigMenuBorder();

        BossLandLoot lootManager = BossLand.getLootManager();
        ArrayList<ItemStack> items = new ArrayList<>(lootManager.getWeaponsAndItems());

        if(!items.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= items.size()) break;
                if (items.get(index) != null){
                    ItemStack bossItem = items.get(index);
                    inventory.addItem(bossItem);
                }
            }
        }

    }
}
