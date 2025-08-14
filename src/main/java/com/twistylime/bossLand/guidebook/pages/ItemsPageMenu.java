package com.twistylime.bossLand.guidebook.pages;

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
                    e.getWhoClicked().sendMessage("Need to implement search functionality");
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
        ArrayList<ItemStack> items = new ArrayList<>(List.of(
                createItem(Material.DIAMOND_PICKAXE,"Pick of Undead",true),
                createItem(Material.DIAMOND_SHOVEL,"Grave Digger",true),
                createItem(Material.DIAMOND_SWORD,"Flame Sword",true),
                createItem(Material.BOW,"Flame Bow",true),
                createItem(Material.NETHERITE_SWORD,"Wither Sword",true),
                createItem(Material.DIAMOND_PICKAXE,"Giant's Pick",true),
                createItem(Material.DIAMOND_SHOVEL,"Giant's Spade",true),
                createItem(Material.CROSSBOW,"Witch Hunter",true),
                createItem(Material.ENCHANTED_BOOK,"Magic Book",false),
                createItem(Material.NETHERITE_AXE,"Demonic Axe",true),
                createItem(Material.END_ROD,"Staff of Control",true),
                createItem(Material.BOW,"Zephyr Bow",true),
                createItem(Material.PLAYER_HEAD,"The Cursed Skull",true),
                createItem(Material.BLAZE_ROD,"Staff of Brimstone",true),
                createItem(Material.TRIDENT,"Trident of Ultimate Power",true),
                createItem(Material.TRIDENT,"Trident of Ultimate Speed",true),
                createItem(Material.ENCHANTED_BOOK,"Book of Knowledge",true),
                createItem(Material.PLAYER_HEAD,"Death Note",true),
                createItem(Material.NETHERITE_SWORD,"End Blade",true),
                createItem(Material.NETHERITE_HOE,"Scythe of Death",true)
        ));

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
