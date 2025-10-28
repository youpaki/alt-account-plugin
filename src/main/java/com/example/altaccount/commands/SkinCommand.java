package com.example.altaccount.commands;

import com.example.altaccount.AltAccountPlugin;
import com.example.altaccount.data.AltDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkinCommand implements CommandExecutor {
    
    private final AltAccountPlugin plugin;
    private final AltDataManager dataManager;
    
    public SkinCommand(AltAccountPlugin plugin) {
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
        if (!player.hasPermission("altaccount.skin")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to change skins!");
            return true;
        }
        
        // Check if player is on an alt
        if (dataManager.isOnMainAccount(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You must be on an alt account to change skins!");
            player.sendMessage(ChatColor.GRAY + "Use /alt <name> first.");
            return true;
        }
        
        // Check arguments
        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Usage: /skin <player_name>");
            player.sendMessage(ChatColor.GRAY + "This will apply the skin of the specified player to your current alt.");
            return true;
        }
        
        String targetPlayer = args[0];
        
        // Validate target player name
        if (!isValidUsername(targetPlayer)) {
            player.sendMessage(ChatColor.RED + "Invalid username! Must be 3-16 characters, alphanumeric and underscores only.");
            return true;
        }
        
        String currentAlt = dataManager.getCurrentAlt(player.getUniqueId());
        if (currentAlt != null) {
            player.sendMessage(ChatColor.YELLOW + "Loading skin from player: " + ChatColor.BOLD + targetPlayer);
            player.sendMessage(ChatColor.GRAY + "This may take a few seconds...");
            
            // Apply the new skin (this will re-disguise the player with the new skin)
            plugin.getDisguiseManager().disguisePlayer(player, currentAlt);
            
            player.sendMessage(ChatColor.GREEN + "Skin updated successfully!");
        } else {
            player.sendMessage(ChatColor.RED + "Error: Could not determine current alt account.");
        }
        
        return true;
    }
    
    private boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z0-9_]{3,16}$");
    }
}