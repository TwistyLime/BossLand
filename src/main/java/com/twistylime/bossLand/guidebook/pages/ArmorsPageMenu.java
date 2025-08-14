package com.twistylime.bossLand.guidebook.pages;

import com.twistylime.bossLand.guidebook.PaginatedMenu;
import com.twistylime.bossLand.guidebook.menuutility.PlayerMenuUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ArmorsPageMenu extends PaginatedMenu {

    private ArrayList<ItemStack> items = new ArrayList<>();

    public ArmorsPageMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "BossLand | Armors Menu";
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
                case ARROW:
                    if (page > 0){
                        page = page - 1;
                        super.open();
                    }
                    break;
                case SPECTRAL_ARROW:
                    if (!((index + 1) >= items.size())){
                        page = page + 1;
                        super.open();
                    }
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
        items = new ArrayList<>(List.of(
                createItem(Material.DIAMOND_HELMET,"Death Mask",true),
                createItem(Material.GOLDEN_CHESTPLATE,"Plate of Death",true),
                createItem(Material.CHAINMAIL_LEGGINGS,"Panda Pants",true),
                createItem(Material.LEATHER_LEGGINGS,"Bunny Pants",true),
                createItem(Material.LEATHER_BOOTS,"Slime Boots",true),
                createItem(Material.NETHERITE_HELMET,"Drowned Helmet",true),
                createItem(Material.NETHERITE_CHESTPLATE,"Zephyr Wings",true),
                createItem(Material.NETHERITE_LEGGINGS,"Devil's Greaves",true),
                createItem(Material.NETHERITE_BOOTS,"Swiftfoot Boots",true)
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
