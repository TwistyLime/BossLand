package com.twistylime.bossLand.guidebook.pages;

import com.twistylime.bossLand.guidebook.PaginatedMenu;
import com.twistylime.bossLand.guidebook.menuutility.PlayerMenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CreditsPageMenu extends PaginatedMenu {
    public CreditsPageMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "BossLand | Credits";
    }

    @Override
    public int getSlots() {
        return 27;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack itemClicked = e.getCurrentItem();
        if(itemClicked != null){
            switch (itemClicked.getType()){
                case WRITABLE_BOOK:
                    new GuideMainMenu(playerMenuUtility).open();
                    break;
                case BARRIER:
                    e.getWhoClicked().closeInventory();
                case NAME_TAG:
                    e.getWhoClicked().sendMessage("This feature is work in progress and will be available in the upcoming updates.");
                    e.getWhoClicked().closeInventory();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void setMenuItems() {
        addSmallCreditMenuBorder();

        List<ItemStack> credits = new ArrayList<>();

        Map<String, List<String>> creditData = new LinkedHashMap<>();
        creditData.put(ChatColor.GOLD + "" + ChatColor.BOLD + "Original Developer",
                List.of(ChatColor.YELLOW + "Eliminator"));
        creditData.put(ChatColor.AQUA + "" + ChatColor.BOLD + "Current Developer",
                List.of(ChatColor.GREEN + "TwistyLime"));
        creditData.put(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "License",
                List.of(ChatColor.WHITE + "MIT License"));

        int[] slots = {11, 13, 15};
        int index = 0;

        for (Map.Entry<String, List<String>> entry : creditData.entrySet()) {
            ItemStack newCredit = new ItemStack(Material.NETHER_STAR, 1);
            ItemMeta meta = newCredit.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(entry.getKey());
                meta.setLore(entry.getValue());
                newCredit.setItemMeta(meta);
            }
            credits.add(newCredit);
            inventory.setItem(slots[index++], newCredit);
        }
    }
}
