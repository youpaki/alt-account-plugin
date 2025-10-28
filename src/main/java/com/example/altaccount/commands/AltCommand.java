package com.example.altaccount.commands;

import com.example.altaccount.AltAccountPlugin;
import com.example.altaccount.data.AltDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AltCommand implements CommandExecutor {
    
    private final AltAccountPlugin plugin;
    private final AltDataManager dataManager;
    
    public AltCommand(AltAccountPlugin plugin) {
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
        if (!player.hasPermission("altaccount.alt")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use alt accounts!");
            return true;
        }
        
        // Check arguments
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /alt <username>");
            return true;
        }
        
        String altName = args[0];
        
        // Validate alt name
        if (!isValidUsername(altName)) {
            player.sendMessage(ChatColor.RED + "Invalid username! Must be 3-16 characters, alphanumeric and underscores only.");
            return true;
        }
        
        // Check if already on this alt
        String currentAlt = dataManager.getCurrentAlt(player.getUniqueId());
        if (altName.equalsIgnoreCase(currentAlt)) {
            player.sendMessage(ChatColor.YELLOW + "You are already using the alt account: " + altName);
            return true;
        }
        
        // Save current data (either main account or current alt)
        if (currentAlt != null) {
            // Currently on an alt, save alt data
            dataManager.savePlayerData(player, currentAlt);
            player.sendMessage(ChatColor.GREEN + "Saved data for alt account: " + currentAlt);
        } else {
            // Currently on main account, save main data
            dataManager.savePlayerData(player, "main");
            player.sendMessage(ChatColor.GREEN + "Saved data for main account.");
        }
        
        // Check if alt account exists
        if (dataManager.hasAltData(player.getUniqueId(), altName)) {
            // Load existing alt
            dataManager.loadPlayerData(player, altName);
            plugin.getDisguiseManager().disguisePlayer(player, altName);
            player.sendMessage(ChatColor.GREEN + "Switched to alt account: " + ChatColor.BOLD + altName);
            player.sendMessage(ChatColor.GRAY + "Welcome back to your alt account!");
        } else {
            // Create new alt
            clearPlayerData(player);
            dataManager.setCurrentAlt(player.getUniqueId(), altName);
            plugin.getDisguiseManager().disguisePlayer(player, altName);
            player.sendMessage(ChatColor.GREEN + "Created new alt account: " + ChatColor.BOLD + altName);
            player.sendMessage(ChatColor.GRAY + "This is a fresh start! Your main account data is safely stored.");
        }
        
        return true;
    }
    
    private boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z0-9_]{3,16}$");
    }
    
    private void clearPlayerData(Player player) {
        // Clear inventory
        player.getInventory().clear();
        
        // Reset experience
        player.setTotalExperience(0);
        player.setLevel(0);
        player.setExp(0);
        
        // Reset health and hunger
        player.setHealth(player.getMaxHealth());
        player.setFoodLevel(20);
        player.setSaturation(5.0f);
        player.setExhaustion(0.0f);
        
        // Reset other stats
        player.setFireTicks(0);
        player.setRemainingAir(player.getMaximumAir());
    }
}