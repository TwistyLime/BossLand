package com.twistylime.bossLand.guidebook.pages;

import com.twistylime.bossLand.BossLand;
import com.twistylime.bossLand.guidebook.PaginatedMenu;
import com.twistylime.bossLand.guidebook.menuutility.PlayerMenuUtility;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class RecipesPageMenu extends PaginatedMenu {

    public RecipesPageMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "BossLand | Recipes Menu";
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
                case POTION:
                case BELL:
                case ENCHANTED_BOOK:
                case ENCHANTED_GOLDEN_APPLE:
                case APPLE:
                case PLAYER_HEAD:
                case HEART_OF_THE_SEA:
                case COAL:
                case CHARCOAL:
                    ItemMeta clickedItemMeta = e.getCurrentItem().getItemMeta();
                    if (clickedItemMeta != null) {
                        NamespacedKey key = new NamespacedKey(BossLand.getPlugin(),"data");
                        playerMenuUtility.setItemRecipeToShow(clickedItemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING));
                    }
                    new IndividualItemRecipePageMenu(playerMenuUtility).open();
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
                createItemWithData(Material.POTION,"Potion of Giant Growth",true,"potion_of_giant_growth"),
                createItemWithData(Material.BELL,"Bell of Doom",true,"bell_of_doom"),
                createItemWithData(Material.ENCHANTED_BOOK,"Book of Spells",true,"book_of_spells"),
                createItemWithData(Material.ENCHANTED_GOLDEN_APPLE,"Forbidden Fruit",true,"forbidden_fruit"),
                createItemWithData(Material.APPLE,"Abhorrent Fruit",true,"abhorrent_fruit"),
                createItemWithData(Material.PLAYER_HEAD,"Death Note",true,"death_note")
        ));

        if(!items.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= items.size()) break;
                if (items.get(index) != null){
                    ItemStack recipeItem = items.get(index);
                    inventory.addItem(recipeItem);
                }
            }
        }

    }
}
