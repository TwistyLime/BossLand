package com.twistylime.bossLand.guidebook.pages;

import com.twistylime.bossLand.BossLand;
import com.twistylime.bossLand.core.BossLandRecipes;
import com.twistylime.bossLand.guidebook.PaginatedMenu;
import com.twistylime.bossLand.guidebook.menuutility.PlayerMenuUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class IndividualItemRecipePageMenu extends PaginatedMenu {
    public IndividualItemRecipePageMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "BossLand | "+super.playerMenuUtility.getItemRecipeToShow().toUpperCase().replace('_',' ');
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
                    playerWhoClicked.sendMessage("This feature is work in progress and will be available in the upcoming updates.");
                    playerWhoClicked.closeInventory();
                    break;
                case WRITABLE_BOOK:
                    new RecipesPageMenu(playerMenuUtility).open();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setMenuItems() {

        String recipeKey = super.playerMenuUtility.getItemRecipeToShow();
        BossLandRecipes recipesManager = BossLand.getRecipeManager();
        Map<String, Object> itemRecipe = recipesManager.getRecipes(recipeKey);

        addItemRecipeTemplate();

        int[] reservedSlotsForRecipe = {12,13,14,21,22,23,30,31,32};

        @SuppressWarnings("unchecked")
        Map<String, Object> recipeMap = (Map<String, Object>) itemRecipe.get("recipe");
        @SuppressWarnings("unchecked")
        List<List<String>> shape = (List<List<String>>) recipeMap.get("shape");
        @SuppressWarnings("unchecked")
        Map<String, ItemStack> ingredients = (Map<String, ItemStack>) recipeMap.get("items");

        for (int row = 0; row < shape.size(); row++) {
            for (int col = 0; col < shape.get(row).size(); col++) {
                int slotIndex = reservedSlotsForRecipe[row * 3 + col]; // map shape[row][col] → slot
                String key = shape.get(row).get(col);

                if (key.equalsIgnoreCase(" ")) continue; // skip empty spaces

                ItemStack ingItem = ingredients.get(key.toLowerCase());
                if (ingItem != null) {
                    inventory.setItem(slotIndex, ingItem);
                }
            }
        }

        ItemStack resultItem = (ItemStack) itemRecipe.get("result");
        inventory.setItem(25,resultItem);

    }
}
