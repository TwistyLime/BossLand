package com.twistylime.bossLand.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Level;

public class BossLandConfiguration {
    private Plugin plugin;
    private File saveYML;
    private File langYML;
    private File recipesYML;
    private YamlConfiguration saveFile;
    private YamlConfiguration langFile;
    private YamlConfiguration recipesFile;

    public void loadConfig(Plugin plugin){
        this.plugin = plugin;
        saveYML = new File(plugin.getDataFolder(), "save.yml");
        saveFile = YamlConfiguration.loadConfiguration(saveYML);

        langYML = new File(plugin.getDataFolder(), "lang.yml");
        langFile = YamlConfiguration.loadConfiguration(langYML);

        recipesYML = new File(plugin.getDataFolder(), "recipes.yml");
        recipesFile = YamlConfiguration.loadConfiguration(recipesYML);

        saveConfig();
    }

    private void saveConfig(){
        if (!new File(plugin.getDataFolder(), "config.yml").exists()) {
            plugin.saveDefaultConfig();
        }

        if (!this.saveYML.exists()) {
            try {
                if(saveYML.createNewFile()){
                    plugin.getLogger().log(Level.INFO, "New save.yml generated.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!langYML.exists()) {
            plugin.getLogger().log(Level.INFO, "No lang.yml found, generating...");
            plugin.saveResource("lang.yml", false);
            plugin.getLogger().log(Level.INFO, Bukkit.getVersion() + " Lang successfully generated!");
            reloadLang();
        }

        if (!recipesYML.exists()) {
            plugin.getLogger().log(Level.INFO, "No recipes.yml found, generating...");
            plugin.saveResource("recipes.yml", false);
            plugin.getLogger().log(Level.INFO, Bukkit.getVersion() + " Recipes successfully generated!");
            reloadRecipes();
        }
    }

    public void reloadConfigs(){
        plugin.reloadConfig();
        reloadLang();
        reloadRecipes();
    }

    private void reloadLang() {
        if (this.langYML == null) {
            this.langYML = new File(plugin.getDataFolder(), "lang.yml");
        }
        this.langFile = YamlConfiguration.loadConfiguration(this.langYML);

        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(langYML);
        this.langFile.setDefaults(defConfig);
    }

    private void reloadRecipes() {
        if (this.recipesYML == null) {
            this.recipesYML = new File(plugin.getDataFolder(), "recipes.yml");
        }
        this.recipesFile = YamlConfiguration.loadConfiguration(this.recipesYML);

        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(recipesYML);
        this.recipesFile.setDefaults(defConfig);
    }

    public String getLang(String s) {
        if (langFile.getString(s) == null) {
            plugin.getLogger().log(Level.SEVERE, "Error with Lang file!");
            System.out.print("Looking for path: " + s);
            System.out.print("Found: " + langFile.getString(s));
            langFile.set(s, "Missing!");
            try {
                langFile.save(langYML);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Objects.requireNonNull(langFile.getString(s)).replace("&", "§");
    }

    public ConfigurationSection getRecipeConfiguration(String section){
        return recipesFile.getConfigurationSection(section);
    }

    @SuppressWarnings("unchecked")
    public ArrayList<String> getLangList(String s) {
        ArrayList<String> list = (ArrayList<String>) langFile.getList(s);
        if (list == null || list.isEmpty()) {
            plugin.getLogger().log(Level.SEVERE, "Error with Lang file!");
            System.out.print("Looking for list path: " + s);
            System.out.print("Found: " + langFile.getString(s));
        }
        ArrayList<String> list2 = new ArrayList<>();
        assert list != null;
        for (String l : list)
            list2.add(l.replace("&", "§"));
        return list2;
    }

    public void saveBossData() {
        try {
            this.saveFile.save(this.saveYML);
        } catch (IOException localIOException) {
            plugin.getLogger().log(Level.WARNING,"Error while saving save.yml file.");
        }
    }

    public void setSaveData(String path, Object value){
        this.saveFile.set(path, value);
    }

    public boolean godsDead() {
        return saveFile.getInt("drownedGodDeaths") > 0 && saveFile.getInt("pharaohGodDeaths") > 0
                && saveFile.getInt("aetherGodDeaths") > 0;
    }

    public String getDataFrom(String file, String key){
        if(file.equals("save")){
            return saveFile.getString(key);
        }
        else{
            return langFile.getString(key);
        }
    }

    public int getIntDataFrom(String file, String key){
        if(file.equals("save")){
            return saveFile.getInt(key);
        }
        else{
            return langFile.getInt(key);
        }
    }
}
