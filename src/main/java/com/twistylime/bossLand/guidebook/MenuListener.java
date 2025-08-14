package com.twistylime.bossLand.guidebook;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class MenuListener implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent e){
        Inventory topInventory = e.getView().getTopInventory();
        InventoryHolder holder = topInventory.getHolder();
        if(holder instanceof Menu){
            e.setCancelled(true);
            Menu menu = (Menu) holder;
            if(e.getCurrentItem() == null || e.getClickedInventory() == null){
                return;
            }
            if(e.getClickedInventory().equals(topInventory)) menu.handleMenu(e);
        }
    }
}
