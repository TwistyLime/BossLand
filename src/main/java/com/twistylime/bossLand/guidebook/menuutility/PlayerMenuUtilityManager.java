package com.twistylime.bossLand.guidebook.menuutility;

import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerMenuUtilityManager {
    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<> ();

    public static PlayerMenuUtility getPlayerMenuUtility(Player p){
        PlayerMenuUtility playerMenuUtility;

        if(playerMenuUtilityMap.containsKey(p)){
            return playerMenuUtilityMap.get(p);
        }else{
            playerMenuUtility = new PlayerMenuUtility(p);
            playerMenuUtilityMap.put(p, playerMenuUtility);
            return playerMenuUtility;
        }
    }
}
