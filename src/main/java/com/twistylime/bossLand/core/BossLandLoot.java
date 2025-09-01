package com.twistylime.bossLand.core;

import com.twistylime.bossLand.config.BossLandConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BossLandLoot {
    private final BossLandItems itemManager;
    private final Map<String, String> bossLootMap;

    public BossLandLoot(BossLandItems itemManager, BossLandConfiguration config) {
        this.itemManager = itemManager;
        bossLootMap = config.getBossLootIdMap();
    }

    public List<ItemStack> getArmors(){
        List<ItemStack> armors = new ArrayList<>();
        Map<String, String> bossWithArmorIds = new HashMap<>();
        bossWithArmorIds.put("KingSlime","0");
        bossWithArmorIds.put("KillerBunny","2");
        bossWithArmorIds.put("PapaPanda","1");
        bossWithArmorIds.put("WitherSkeletonKing","2");
        bossWithArmorIds.put("IllagerKing","3");
        bossWithArmorIds.put("DrownedGod","2");
        bossWithArmorIds.put("PharaohGod","2");
        bossWithArmorIds.put("AetherGod","0");
        bossWithArmorIds.put("Devil","0");

        for (Map.Entry<String, String> entry : bossWithArmorIds.entrySet()) {
            String boss = entry.getKey();
            String armorId = entry.getValue();
            armors.add(getLoot(boss,armorId));
        }
        return armors;
    }

    public List<ItemStack> getWeaponsAndItems(){
        List<ItemStack> itemsAndWeapons = new ArrayList<>();
        Map<String, List<String>> bossWithItemAndWeaponIds = new HashMap<>();
        bossWithItemAndWeaponIds.put("GhastLord",List.of("2","3"));
        bossWithItemAndWeaponIds.put("WitherSkeletonKing",List.of("1"));
        bossWithItemAndWeaponIds.put("ZombieKing",List.of("1","2","3"));
        bossWithItemAndWeaponIds.put("IllagerKing",List.of("4"));
        bossWithItemAndWeaponIds.put("EvilWizard",List.of("3"));
        bossWithItemAndWeaponIds.put("Giant",List.of("1","2"));
        bossWithItemAndWeaponIds.put("DrownedGod",List.of("0","1"));
        bossWithItemAndWeaponIds.put("PharaohGod",List.of("0","1"));
        bossWithItemAndWeaponIds.put("AetherGod",List.of("1","2"));
        bossWithItemAndWeaponIds.put("Demon",List.of("1"));
        bossWithItemAndWeaponIds.put("Devil",List.of("1"));
        bossWithItemAndWeaponIds.put("Death",List.of("0","1"));

        for (Map.Entry<String, List<String>> entry : bossWithItemAndWeaponIds.entrySet()) {
            String boss = entry.getKey();
            List<String> weaponAndItemIds = entry.getValue();
            for(String itemId: weaponAndItemIds){
                itemsAndWeapons.add(getLoot(boss,itemId));
            }
        }
        return itemsAndWeapons;
    }

    public List<ItemStack> getShards(){
        List<ItemStack> shards = new ArrayList<>();
        Map<String, String> bossWithShardIds = new HashMap<>();
        bossWithShardIds.put("GhastLord","1");
        bossWithShardIds.put("KingSlime","2");
        bossWithShardIds.put("KillerBunny","1");
        bossWithShardIds.put("PapaPanda","0");
        bossWithShardIds.put("WitherSkeletonKing","0");
        bossWithShardIds.put("ZombieKing","0");
        bossWithShardIds.put("EvilWizard","0");
        bossWithShardIds.put("IllagerKing","0");
        bossWithShardIds.put("Giant","0");
        bossWithShardIds.put("Demon","0");

        for (Map.Entry<String, String> entry : bossWithShardIds.entrySet()) {
            String boss = entry.getKey();
            String shardId = entry.getValue();
            shards.add(getLoot(boss, shardId));
        }
        return shards;
    }

    public List<String> getLootNames(String bossName){
        List<String> lootNames = new ArrayList<>();
        String prefix = bossName + ":";
        for (Map.Entry<String, String> entry : bossLootMap.entrySet()) {
            if (entry.getKey().startsWith(prefix)) {
                lootNames.add(entry.getValue());
            }
        }
        return lootNames;
    } // for displaying in command rather than using ids - Need to implement in commands section autotab

    public ItemStack getLootFromName(String name, String bossType){
        String id = getLootIdFromName(name.toUpperCase().replace(" ","_"));
        System.out.println(id+" "+bossType);
        return getLoot(bossType,id);
    }

    private String getLootIdFromName(String lootName){
        for (Map.Entry<String, String> entry : bossLootMap.entrySet()) {
            if (entry.getValue().replace("_"," ").equalsIgnoreCase(lootName.replace("_"," "))) {
                String[] parts = entry.getKey().split(":");
                if (parts.length == 2) {
                    return parts[1];
                }
            }
        }
        return null;
    }

    private ItemStack getLoot(String bossType, String lootId){
        return itemManager.getItem(bossType, lootId);
    }
}
