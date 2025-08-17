package com.twistylime.bossLand.guidebook;

import com.twistylime.bossLand.BossLand;
import com.twistylime.bossLand.guidebook.menuutility.PlayerMenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;
    protected int maxItemsPerPage = 28;
    protected int index = 0;

    public PaginatedMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }

    public int getPage() {
        return page;
    }

    public int getIndex() {
        return index;
    }

    public void addBigMenuBorder(){
        int rows = 6;
        int columns = 9;
        int[] reservedSlots = {2,6,47,49,51};
        ItemStack gray_glass_pane = createItem(Material.GRAY_STAINED_GLASS_PANE," ", false);
        ItemStack black_glass_pane = createItem(Material.BLACK_STAINED_GLASS_PANE," ", false);

        for (int i = 0; i < rows * columns; i++) {
            int finalI = i;
            if(Arrays.stream(reservedSlots).anyMatch(s->s== finalI)) continue;
            boolean isTopRow = i < columns;
            boolean isBottomRow = i >= (rows - 1) * columns;
            boolean isLeftColumn = i % columns == 0;
            boolean isRightColumn = i % columns == columns - 1;

            if (isTopRow || isBottomRow) {
                int relativeIndex = i % columns;
                ItemStack pane = (relativeIndex % 2 == 0) ? gray_glass_pane : black_glass_pane;
                inventory.setItem(i, pane);
            }
            else if (isLeftColumn || isRightColumn) {
                inventory.setItem(i, gray_glass_pane);
            }
        }

        inventory.setItem(2,createItem(Material.NAME_TAG, ChatColor.WHITE + "Search", false));
        inventory.setItem(6,createItem(Material.BARRIER,ChatColor.RED.toString() + ChatColor.BOLD + "Close", false));
        inventory.setItem(47,createItem(Material.ARROW, ChatColor.GOLD + "Previous Page", false));
        inventory.setItem(51,createItem(Material.SPECTRAL_ARROW, ChatColor.GOLD + "Next Page", false));
        inventory.setItem(49,createItem(Material.WRITABLE_BOOK, ChatColor.WHITE + "Previous Menu", false));
    }

    public void addSmallMenuBorder(){
        int rows = 3;
        int columns = 9;
        int[] reservedSlots = {2,6};
        ItemStack gray_glass_pane = createItem(Material.GRAY_STAINED_GLASS_PANE," ", false);
        ItemStack black_glass_pane = createItem(Material.BLACK_STAINED_GLASS_PANE," ", false);

        for (int i = 0; i < rows * columns; i++) {
            int finalI = i;
            if(Arrays.stream(reservedSlots).anyMatch(s->s== finalI)) continue;
            boolean isTopRow = i < columns;
            boolean isBottomRow = i >= (rows - 1) * columns;
            boolean isLeftColumn = i % columns == 0;
            boolean isRightColumn = i % columns == columns - 1;

            if (isTopRow || isBottomRow) {
                int relativeIndex = i % columns;
                ItemStack pane = (relativeIndex % 2 == 0) ? black_glass_pane : gray_glass_pane;
                inventory.setItem(i, pane);
            }
            else if (isLeftColumn || isRightColumn) {
                inventory.setItem(i, black_glass_pane);
            }
        }

        inventory.setItem(2,createItem(Material.NAME_TAG,ChatColor.WHITE + "Search", false));
        inventory.setItem(6,createItem(Material.BARRIER,ChatColor.RED.toString() + ChatColor.BOLD + "Close", false));
    }

    public void addBossRecipeTemplate(){
        int rows = 5;
        int columns = 9;
        int[] reservedSlotsForButtons = {0,4,8,18};
        int[] reservedSlotsForRecipe = {10,11,12,19,20,21,23,25,28,29,30};
        int[] blackPaneSlots = {1,7,37,43};
        ItemStack gray_glass_pane = createItem(Material.GRAY_STAINED_GLASS_PANE," ", false);
        ItemStack black_glass_pane = createItem(Material.BLACK_STAINED_GLASS_PANE," ", false);
        ItemStack bossRecipeInstructor = createItem(Material.BRICKS,"Build this structure in the world to summon the boss", false);

        for (int i = 0; i < rows * columns; i++) {
            int finalI = i;
            if(Arrays.stream(reservedSlotsForButtons).anyMatch(s->s== finalI)) continue;
            if(Arrays.stream(reservedSlotsForRecipe).anyMatch(s->s== finalI)) continue;

            if(Arrays.stream(blackPaneSlots).anyMatch(s->s== finalI)){
                inventory.setItem(i, black_glass_pane);
            }
            else{
                inventory.setItem(i, gray_glass_pane);
            }
        }

        inventory.setItem(0,createItem(Material.NAME_TAG,ChatColor.WHITE + "Search", false));
        inventory.setItem(8,createItem(Material.BARRIER,ChatColor.RED.toString() + ChatColor.BOLD + "Close", false));
        inventory.setItem(4,createItem(Material.WRITABLE_BOOK,ChatColor.WHITE + "Previous Menu", false));
        inventory.setItem(18, bossRecipeInstructor);
    }

    public void addBossRecipeTemplateForTopTier(){
        int rows = 5;
        int columns = 9;
        int[] reservedSlotsForButtons = {1,4,7};
        int[] reservedSlotsForRecipe = {20,22,24};
        int[] blackPaneSlots = {0,8,11,13,15,19,21,23,25,29,31,33,36,44};
        ItemStack gray_glass_pane = createItem(Material.GRAY_STAINED_GLASS_PANE," ", false);
        ItemStack black_glass_pane = createItem(Material.BLACK_STAINED_GLASS_PANE," ", false);

        for (int i = 0; i < rows * columns; i++) {
            int finalI = i;
            if(Arrays.stream(reservedSlotsForButtons).anyMatch(s->s== finalI)) continue;
            if(Arrays.stream(reservedSlotsForRecipe).anyMatch(s->s== finalI)) continue;

            if(Arrays.stream(blackPaneSlots).anyMatch(s->s== finalI)){
                inventory.setItem(i, black_glass_pane);
            }
            else{
                inventory.setItem(i, gray_glass_pane);
            }
        }

        inventory.setItem(1,createItem(Material.NAME_TAG,ChatColor.WHITE + "Search", false));
        inventory.setItem(7,createItem(Material.BARRIER,ChatColor.RED.toString() + ChatColor.BOLD + "Close", false));
        inventory.setItem(4,createItem(Material.WRITABLE_BOOK,ChatColor.WHITE + "Previous Menu", false));
    }

    public void addItemRecipeTemplate(){
        int rows = 5;
        int columns = 9;
        int[] reservedSlotsForButtons = {0,4,8,18};
        int[] reservedSlotsForRecipe = {10,11,12,19,20,21,23,25,28,29,30};
        int[] blackPaneSlots = {1,7,37,43};
        ItemStack gray_glass_pane = createItem(Material.GRAY_STAINED_GLASS_PANE," ", false);
        ItemStack black_glass_pane = createItem(Material.BLACK_STAINED_GLASS_PANE," ", false);
        ItemStack itemRecipeInstructor = createItem(Material.CRAFTING_TABLE,"Craft this recipe in the crafting table.", false);

        for (int i = 0; i < rows * columns; i++) {
            int finalI = i;
            if(Arrays.stream(reservedSlotsForButtons).anyMatch(s->s== finalI)) continue;
            if(Arrays.stream(reservedSlotsForRecipe).anyMatch(s->s== finalI)) continue;

            if(Arrays.stream(blackPaneSlots).anyMatch(s->s== finalI)){
                inventory.setItem(i, black_glass_pane);
            }
            else{
                inventory.setItem(i, gray_glass_pane);
            }
        }

        inventory.setItem(0,createItem(Material.NAME_TAG,ChatColor.WHITE + "Search", false));
        inventory.setItem(8,createItem(Material.BARRIER,ChatColor.RED.toString() + ChatColor.BOLD + "Close", false));
        inventory.setItem(4,createItem(Material.WRITABLE_BOOK,ChatColor.WHITE + "Previous Menu", false));
        inventory.setItem(18, itemRecipeInstructor);
    }

    protected ItemStack createItem(Material item, String name, boolean glint){
        ItemStack newItem = new ItemStack(item,1);
        ItemMeta newItemMeta = newItem.getItemMeta();
        if(newItemMeta != null){
            newItemMeta.setDisplayName(name);
            if (glint) {
                newItemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
                newItemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            }
            newItem.setItemMeta(newItemMeta);
        }
        return  newItem;
    }

    protected ItemStack createItemWithData(Material item, String name, boolean glint, String data){
        ItemStack itemToAddData = createItem(item, name, glint);
        ItemMeta itemToAddDataMeta = itemToAddData.getItemMeta();
        if(itemToAddDataMeta != null){
            NamespacedKey key = new NamespacedKey(BossLand.getPlugin(),"data");
            itemToAddDataMeta.getPersistentDataContainer().set(key,PersistentDataType.STRING, data);
        }
        itemToAddData.setItemMeta(itemToAddDataMeta);
        return itemToAddData;
    }
}
