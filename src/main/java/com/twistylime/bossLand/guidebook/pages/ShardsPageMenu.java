package com.twistylime.bossLand.guidebook.pages;

import com.twistylime.bossLand.guidebook.PaginatedMenu;
import com.twistylime.bossLand.guidebook.menuutility.PlayerMenuUtility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ShardsPageMenu extends PaginatedMenu {

    private ArrayList<ItemStack> items = new ArrayList<>();

    public ShardsPageMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "BossLand | Shards Menu";
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
                createItem(Material.BROWN_DYE,"Brown Boss Shard",true),
                createItem(Material.RED_DYE,"Red Boss Shard",true),
                createItem(Material.CLAY_BALL,"Gray Boss Shard",true),
                createItem(Material.QUARTZ,"White Boss Shard",true),
                createItem(Material.COAL,"Black Boss Shard",true),
                createItem(Material.GREEN_DYE,"Green Boss Shard",true),
                createItem(Material.LAPIS_LAZULI,"Blue Boss Shard",true),
                createItem(Material.EMERALD,"Emerald Boss Shard",true),
                createItem(Material.YELLOW_DYE,"Gold Boss Shard",false),
                createItem(Material.FIRE_CORAL,"Demonic Shard",true)
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
