package com.twistylime.bossLand.guidemenu;

import com.twistylime.bossLand.BossLand;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
//import org.bukkit.event.inventory.InventoryCloseEvent;

public class InvListener implements Listener {

    private final BossLand plugin;
    private final String[] templateItems = {"black_stained","gray_stained"};

    public InvListener(BossLand plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        if(event.getView().getTitle().equals("BossLand Guide")){
            event.setCancelled(true);
            Player player = (Player)event.getWhoClicked();
            ItemStack itemClicked = event.getCurrentItem();
            if(itemClicked == null || !itemClicked.hasItemMeta()) return;
            ItemMeta meta = itemClicked.getItemMeta();
            assert meta != null;
            NamespacedKey key = new NamespacedKey(plugin, "object_tag");
            String value = meta.getPersistentDataContainer().get(key, PersistentDataType.STRING);
            if(Arrays.asList(templateItems).contains(value)) return;
            player.sendMessage("Clicked item is "+value);
            player.closeInventory();
        }
    }

//    @EventHandler
//    public void onClose(InventoryCloseEvent event){
//        if(event.getView().getTitle().equals("BossLand Guide")){
//            Player player = (Player)event.getPlayer();
//        }
//    }
}
