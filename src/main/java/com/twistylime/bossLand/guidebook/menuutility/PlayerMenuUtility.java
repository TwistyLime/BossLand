package com.twistylime.bossLand.guidebook.menuutility;

import org.bukkit.entity.Player;

public class PlayerMenuUtility {
    private Player owner;
    private String bossRecipeToShow;
    private String itemRecipeToShow;

    public String getBossRecipeToShow() {
        return bossRecipeToShow;
    }

    public void setBossRecipeToShow(String bossRecipeToShow) {
        this.bossRecipeToShow = bossRecipeToShow;
    }

    public String getItemRecipeToShow() {
        return itemRecipeToShow;
    }

    public void setItemRecipeToShow(String itemRecipeToShow) {
        this.itemRecipeToShow = itemRecipeToShow;
    }

    public PlayerMenuUtility(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
