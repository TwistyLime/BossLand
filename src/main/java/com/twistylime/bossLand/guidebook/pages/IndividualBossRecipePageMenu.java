package com.twistylime.bossLand.guidebook.pages;

import com.twistylime.bossLand.BossLand;
import com.twistylime.bossLand.core.BossLandShrines;
import com.twistylime.bossLand.guidebook.PaginatedMenu;
import com.twistylime.bossLand.guidebook.menuutility.PlayerMenuUtility;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public class IndividualBossRecipePageMenu extends PaginatedMenu {
    public IndividualBossRecipePageMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "BossLand | "+super.playerMenuUtility.getBossRecipeToShow().toUpperCase().replace('_',' ');
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
                case WRITABLE_BOOK:
                    new BossesPageMenu(playerMenuUtility).open();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setMenuItems() {
        String recipeKey = super.playerMenuUtility.getBossRecipeToShow();
        BossLandShrines shrinesManager = BossLand.getShrinesManager();
        Map<String, Object> bossRecipe = shrinesManager.getShrineRecipe(recipeKey);

        if(recipeKey.contains("god") || recipeKey.contains("devil") || recipeKey.contains("death")){
            addBossRecipeTemplateForTopTier();

            ItemStack resultBoss = (ItemStack) bossRecipe.get("result");
            ItemStack recipeItem = (ItemStack) bossRecipe.get("recipe");
            ItemStack instructionsItem = (ItemStack) bossRecipe.get("instructions");


            inventory.setItem(20,recipeItem);
            inventory.setItem(22,instructionsItem);
            inventory.setItem(24,resultBoss);

        }else{
            addBossRecipeTemplate();

            int[] reservedSlotsForRecipe = {10,11,12,19,20,21,28,29,30};

            @SuppressWarnings("unchecked")
            Map<String, Object> recipeMap = (Map<String, Object>) bossRecipe.get("recipe");
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

            @SuppressWarnings("unchecked")
            Map<String, Object> activatorMap = (Map<String, Object>) bossRecipe.get("activator");
            if (activatorMap != null) {
                ItemStack activatorItem = (ItemStack) activatorMap.get("item");
                if (activatorItem != null) {
                    inventory.setItem(23, activatorItem);
                }
            }

            @SuppressWarnings("unchecked")
            Map<String, Object> resultMap = (Map<String, Object>) bossRecipe.get("result");
            if (resultMap != null) {
                ItemStack resultItem = (ItemStack) resultMap.get("item");
                if (resultItem != null) {
                    inventory.setItem(25, resultItem);
                }
            }
        }

        @SuppressWarnings("unchecked")
        List<ItemStack> loot = (List<ItemStack>) bossRecipe.get("loot");

        for (int i = 0; i < loot.size(); i++) {
            if(i>8) break;
            ItemStack lootItem = loot.get(i);
            if(lootItem != null){
                inventory.setItem(45+i,loot.get(i));
            }
        }
    }
}
