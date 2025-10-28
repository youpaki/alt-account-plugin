package com.example.altaccount;

import com.example.altaccount.commands.AltCommand;
import com.example.altaccount.commands.MainCommand;
import com.example.altaccount.commands.RandomCommand;
import com.example.altaccount.commands.SkinCommand;
import com.example.altaccount.data.AltDataManager;
import com.example.altaccount.disguise.DisguiseManager;
import org.bukkit.plugin.java.JavaPlugin;

public class AltAccountPlugin extends JavaPlugin {
    
    private AltDataManager dataManager;
    private DisguiseManager disguiseManager;
    
    @Override
    public void onEnable() {
        // Initialize data manager
        dataManager = new AltDataManager(this);
        
        // Initialize disguise manager
        disguiseManager = new DisguiseManager(this);
        
        // Register commands
        getCommand("alt").setExecutor(new AltCommand(this));
        getCommand("main").setExecutor(new MainCommand(this));
        getCommand("random").setExecutor(new RandomCommand(this));
        getCommand("skin").setExecutor(new SkinCommand(this));
        
        // Log plugin enabled
        getLogger().info("AltAccountPlugin has been enabled!");
    }
    
    @Override
    public void onDisable() {
        // Save all data before shutdown
        if (dataManager != null) {
            dataManager.saveAllData();
        }
        
        getLogger().info("AltAccountPlugin has been disabled!");
    }
    
    public AltDataManager getDataManager() {
        return dataManager;
    }
    
    public DisguiseManager getDisguiseManager() {
        return disguiseManager;
    }
}