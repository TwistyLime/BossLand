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
                    e.getWhoClicked().sendMessage("Need to implement search functionality");
                    e.getWhoClicked().closeInventory();
                    break;
                case BAMBOO:
                case SLIME_BLOCK:
                case RABBIT_FOOT:
                case ROTTEN_FLESH:
                case GHAST_TEAR:
                case WITHER_SKELETON_SKULL:
                case POTION:
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
        ArrayList<ItemStack> bosses = new ArrayList<>(List.of(
                createItemWithData(Material.BAMBOO,"Papa Panda",true,"papa_panda"),
                createItemWithData(Material.SLIME_BLOCK,"Slime King",true,"slime_king"),
                createItemWithData(Material.RABBIT_FOOT,"Killer Bunny",true,"killer_bunny"),
                createItemWithData(Material.ROTTEN_FLESH,"Zombie King",true,"zombie_king"),
                createItemWithData(Material.GHAST_TEAR,"Ghast Lord",true,"ghast_lord"),
                createItemWithData(Material.WITHER_SKELETON_SKULL,"Wither Skeleton King",true,"wither_skeleton_king"),
                createItemWithData(Material.POTION,"Giant",true,"giant"),
                createItemWithData(Material.BELL,"Illager King",true,"illager_king"),
                createItemWithData(Material.ENCHANTED_BOOK,"Evil Wizard",false,"evil_wizard"),
                createItemWithData(Material.NETHER_STAR,"Demon",false,"demon"),
                createItemWithData(Material.FEATHER,"Aether God",true,"aether_god"),
                createItemWithData(Material.BLAZE_POWDER,"Pharaoh God",true,"pharoah_god"),
                createItemWithData(Material.HEART_OF_THE_SEA,"Drowned God",true,"drowned_god"),
                createItemWithData(Material.COAL,"The Devil",true,"the_devil"),
                createItemWithData(Material.CHARCOAL,"Death",true,"death")
        ));

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
}
