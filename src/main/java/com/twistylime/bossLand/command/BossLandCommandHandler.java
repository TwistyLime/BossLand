package com.twistylime.bossLand.command;

import com.twistylime.bossLand.guidebook.menuutility.PlayerMenuUtilityManager;
import com.twistylime.bossLand.guidebook.pages.GuideMainMenu;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BossLandCommandHandler implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if(commandSender instanceof Player){
            GuideMainMenu menu = new GuideMainMenu(PlayerMenuUtilityManager.getPlayerMenuUtility((Player) commandSender));
            menu.open();
        }
        return true;
    }
}
