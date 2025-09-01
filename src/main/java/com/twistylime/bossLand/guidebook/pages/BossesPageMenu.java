package com.twistylime.bossLand.guidebook.pages;

import com.twistylime.bossLand.BossLand;
import com.twistylime.bossLand.core.BossLandShrines;
import com.twistylime.bossLand.guidebook.PaginatedMenu;
import com.twistylime.bossLand.guidebook.menuutility.PlayerMenuUtility;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class BossesPageMenu extends PaginatedMenu {
    public BossesPageMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "BossLand | Boss Menu";
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
                    e.getWhoClicked().sendMessage("This feature is work in progress and will be available in the upcoming updates.");
                    e.getWhoClicked().closeInventory();
                    break;
                case BAMBOO:
                case SLIME_BLOCK:
                case RABBIT_FOOT:
                case ROTTEN_FLESH:
                case GHAST_TEAR:
                case WITHER_SKELETON_SKULL:
                case ZOMBIE_HEAD:
                case BELL:
                case ENCHANTED_BOOK:
                case NETHER_STAR:
                case FEATHER:
                case BLAZE_POWDER:
                case HEART_OF_THE_SEA:
                case COAL:
                case CHARCOAL:
                    ItemMeta clickedItemMeta = e.getCurrentItem().getItemMeta();
                    if (clickedItemMeta != null) {
                        NamespacedKey key = new NamespacedKey(BossLand.getPlugin(),"data");
                        playerMenuUtility.setBossRecipeToShow(clickedItemMeta.getPersistentDataContainer().get(key, PersistentDataType.STRING));
                    }
                    new IndividualBossRecipePageMenu(playerMenuUtility).open();
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

        BossLandShrines shrinesManager = BossLand.getShrinesManager();
        ArrayList<String> bossData = new ArrayList<>(List.of(
                "papa_panda",
                "slime_king",
                "killer_bunny",
                "zombie_king",
                "ghast_lord",
                "wither_skeleton_king",
                "giant",
                "illager_king",
                "evil_wizard",
                "demon",
                "aether_god",
                "pharaoh_god",
                "drowned_god",
                "the_devil",
                "death"
        ));
        ArrayList<ItemStack> bosses = new ArrayList<>();

        for(String boss: bossData){
            Map<String, Object> bossRecipe = shrinesManager.getShrineRecipe(boss);
            if(boss.contains("god") || boss.contains("devil") || boss.contains("death")){
                ItemStack resultBoss = (ItemStack) bossRecipe.get("result");
                bosses.add(addDataToItem(resultBoss,boss));
            }
            else{
                @SuppressWarnings("unchecked")
                Map<String, Object> resultMap = (Map<String, Object>) bossRecipe.get("result");
                if (resultMap != null) {
                    ItemStack resultBoss = (ItemStack) resultMap.get("item");
                    bosses.add(addDataToItem(resultBoss,boss));
                }
            }
        }

        if(!bosses.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= bosses.size()) break;
                if (bosses.get(index) != null){
                    ItemStack bossItem = bosses.get(index);
                    inventory.addItem(bossItem);
                }
            }
        }

    }

    private ItemStack addDataToItem(ItemStack item, String data){
        ItemMeta itemToAddDataMeta = item.getItemMeta();
        if(itemToAddDataMeta != null){
            NamespacedKey key = new NamespacedKey(BossLand.getPlugin(),"data");
            itemToAddDataMeta.getPersistentDataContainer().set(key,PersistentDataType.STRING, data);
        }
        item.setItemMeta(itemToAddDataMeta);
        return item;
    }
}
