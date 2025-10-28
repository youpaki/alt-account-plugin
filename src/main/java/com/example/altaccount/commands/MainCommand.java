package com.example.altaccount.commands;

import com.example.altaccount.AltAccountPlugin;
import com.example.altaccount.data.AltDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MainCommand implements CommandExecutor {
    
    private final AltAccountPlugin plugin;
    private final AltDataManager dataManager;
    
    public MainCommand(AltAccountPlugin plugin) {
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check permission
        if (!player.hasPermission("altaccount.main")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to switch to main account!");
            return true;
        }
        
        // Check if already on main account
        if (dataManager.isOnMainAccount(player.getUniqueId())) {
            player.sendMessage(ChatColor.YELLOW + "You are already on your main account!");
            return true;
        }
        
        // Get current alt name
        String currentAlt = dataManager.getCurrentAlt(player.getUniqueId());
        
        // Save current alt data
        if (currentAlt != null) {
            dataManager.savePlayerData(player, currentAlt);
            player.sendMessage(ChatColor.GREEN + "Saved data for alt account: " + currentAlt);
        }
        
        // Check if main account data exists
        if (dataManager.hasAltData(player.getUniqueId(), "main")) {
            // Load main account data
            dataManager.loadPlayerData(player, "main");
            dataManager.setCurrentAlt(player.getUniqueId(), null); // Set to main (null = main)
            plugin.getDisguiseManager().undisguisePlayer(player);
            player.sendMessage(ChatColor.GREEN + "Switched back to your " + ChatColor.BOLD + "main account" + ChatColor.GREEN + "!");
            player.sendMessage(ChatColor.GRAY + "Welcome back to your main account!");
        } else {
            // No main account data exists (shouldn't happen normally)
            dataManager.setCurrentAlt(player.getUniqueId(), null);
            plugin.getDisguiseManager().undisguisePlayer(player);
            player.sendMessage(ChatColor.YELLOW + "Switched to main account, but no saved data was found.");
            player.sendMessage(ChatColor.GRAY + "You may need to gather your items again.");
        }
        
        return true;
    }
}