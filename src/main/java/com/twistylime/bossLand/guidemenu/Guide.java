package com.twistylime.bossLand.guidemenu;

import com.twistylime.bossLand.BossLand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class Guide implements CommandExecutor {

    private final BossLand plugin;

    public Guide(BossLand plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("Only players can use this command!");
            return true;
        }
        Player player = (Player) sender;
        if(args.length >= 1){
            player.sendMessage(ChatColor.RED+"Invalid Command! Use: /blguide");
            return true;
        }
        Inventory inventory = Bukkit.createInventory(player,9*6, "BossLand Guide");

        Map<String, Object> guide = plugin.getGuide();
        GuideItem menuItem = new GuideItem(plugin);

        Map<String, Object> buttons = (Map<String, Object>) guide.get("buttons");
        Map<String, Object> content = (Map<String, Object>) guide.get("content");

        //Template
        for(Map.Entry<String, Object> button: buttons.entrySet()){
            String buttonkey = button.getKey();
            Map<String, Object> buttonObject = (Map<String, Object>) button.getValue();
            ItemStack buttonItem = menuItem.createItem(buttonObject, buttonkey);
            List<Integer> buttonPosList = (List<Integer>) buttonObject.get("pos");
            int[] buttonPos = buttonPosList.stream().mapToInt(Integer::intValue).toArray();
            for(int pos : buttonPos) inventory.setItem(pos, buttonItem);
        }

        //Content
        for(Map.Entry<String, Object> con: content.entrySet()){
            String contentkey = con.getKey();
            Map<String, Object> contentObject = (Map<String, Object>) con.getValue();
            ItemStack contentItem = menuItem.createItem(contentObject, contentkey);
            List<Integer> contentPosList = (List<Integer>) contentObject.get("pos");
            int[] contentPos = contentPosList.stream().mapToInt(Integer::intValue).toArray();
            inventory.setItem(contentPos[0], contentItem);
        }

        player.openInventory(inventory);
        return true;
    }
}
