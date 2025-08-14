package com.twistylime.bossLand.guidebook;

import com.twistylime.bossLand.guidebook.menuutility.PlayerMenuUtility;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class Menu implements InventoryHolder {
    protected Inventory inventory;
    protected PlayerMenuUtility playerMenuUtility;

    public Menu(PlayerMenuUtility playerMenuUtility){
        this.playerMenuUtility = playerMenuUtility;
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent e);

    public abstract void setMenuItems();

    public void open(){
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());
        this.setMenuItems();
        playerMenuUtility.getOwner().playSound(playerMenuUtility.getOwner().getLocation(),Sound.ITEM_BOOK_PAGE_TURN,1,1);
        playerMenuUtility.getOwner().openInventory(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}

/*
* Need to add recipes for both bosses and items
* Need to add lore and description to eveny item
* Need to have creative pick mode
* Need to add proper credits and info screen
* Need to add a guide book on right click opens the menu
*
* To-Do: Add a progress thing in the guide book to have everything explored and obtained
* */