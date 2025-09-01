package com.twistylime.bossLand.guidebook.pages;

import com.twistylime.bossLand.BossLand;
import com.twistylime.bossLand.core.BossLandItems;
import com.twistylime.bossLand.guidebook.PaginatedMenu;
import com.twistylime.bossLand.guidebook.menuutility.PlayerMenuUtility;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
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

        BossLandItems itemManager = BossLand.getItemsManager();

        addBigMenuBorder();
        ArrayList<ItemStack> items = new ArrayList<>(List.of(
                addDataToItem(itemManager.getGiantIem(),"giant_potion"),
                addDataToItem(itemManager.getIllagerItem(),"illager_bell"),
                addDataToItem(itemManager.getWizardItem(),"wizard_book"),
                addDataToItem(itemManager.getGodItem(),"god_fruit"),
                addDataToItem(itemManager.getDevilItem(),"devil_fruit"),
                addDataToItem(itemManager.getDeathItem(),"death_note")
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

    private ItemStack addDataToItem(ItemStack itemToAddData, String data){
        ItemMeta itemToAddDataMeta = itemToAddData.getItemMeta();
        if(itemToAddDataMeta != null){
            NamespacedKey key = new NamespacedKey(BossLand.getPlugin(),"data");
            itemToAddDataMeta.getPersistentDataContainer().set(key,PersistentDataType.STRING, data);
        }
        itemToAddData.setItemMeta(itemToAddDataMeta);
        return itemToAddData;
    }
}
