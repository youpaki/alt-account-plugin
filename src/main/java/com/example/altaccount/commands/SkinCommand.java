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
        
        // Check for help argument
        if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
            showHelp(player);
            return true;
        }
        
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
            // Utiliser le nouveau SkinManager pour appliquer la skin
            plugin.getSkinManager().applySkinToPlayer(player, targetPlayer);
        } else {
            player.sendMessage(ChatColor.RED + "Error: Could not determine current alt account.");
        }
        
        return true;
    }
    
    private boolean isValidUsername(String username) {
        return username.matches("^[a-zA-Z0-9_]{3,16}$");
    }
    
    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "=== " + ChatColor.YELLOW + "Skin Command Help" + ChatColor.GOLD + " ===");
        player.sendMessage("");
        player.sendMessage(ChatColor.AQUA + "/skin <player_name>" + ChatColor.GRAY + " - Change your alt's skin");
        player.sendMessage(ChatColor.GRAY + "  Downloads and applies the skin of the specified player");
        player.sendMessage(ChatColor.GRAY + "  You must be on an alt account to use this command");
        player.sendMessage("");
        player.sendMessage(ChatColor.AQUA + "/skin help" + ChatColor.GRAY + " - Show this help message");
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "Examples:");
        player.sendMessage(ChatColor.WHITE + "  /skin Notch" + ChatColor.GRAY + " - Apply Notch's skin to your alt");
        player.sendMessage(ChatColor.WHITE + "  /skin Steve" + ChatColor.GRAY + " - Apply Steve's skin");
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "Important Notes:");
        player.sendMessage(ChatColor.GRAY + "  • Other players see your new skin immediately");
        player.sendMessage(ChatColor.GRAY + "  • You see your new skin after reconnecting");
        player.sendMessage(ChatColor.GRAY + "  • Skin persists through server restarts");
        player.sendMessage(ChatColor.GRAY + "  • Player name must be valid (3-16 characters)");
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "Related Commands:");
        player.sendMessage(ChatColor.WHITE + "  /alt <username>" + ChatColor.GRAY + " - Create or switch to an alt");
        player.sendMessage(ChatColor.WHITE + "  /main" + ChatColor.GRAY + " - Switch back to main account");
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "Permission: " + ChatColor.WHITE + "altaccount.skin");
        player.sendMessage("");
    }
}