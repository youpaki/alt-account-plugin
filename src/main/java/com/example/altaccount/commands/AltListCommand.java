package com.example.altaccount.commands;

import com.example.altaccount.AltAccountPlugin;
import com.example.altaccount.data.AltDataManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class AltListCommand implements CommandExecutor {
    
    private final AltAccountPlugin plugin;
    private final AltDataManager dataManager;
    
    public AltListCommand(AltAccountPlugin plugin) {
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
        if (!player.hasPermission("altaccount.list")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to list alt accounts!");
            return true;
        }
        
        // Get all alts for this player
        List<String> alts = dataManager.getPlayerAlts(player.getUniqueId());
        String currentAlt = dataManager.getCurrentAlt(player.getUniqueId());
        
        // Display header
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "=== " + ChatColor.YELLOW + "Your Alt Accounts" + ChatColor.GOLD + " ===");
        
        // Show current account status
        if (currentAlt != null) {
            player.sendMessage(ChatColor.GREEN + "Current: " + ChatColor.BOLD + ChatColor.AQUA + currentAlt + ChatColor.RESET + ChatColor.GRAY + " (alt account)");
        } else {
            player.sendMessage(ChatColor.GREEN + "Current: " + ChatColor.BOLD + ChatColor.AQUA + "Main Account" + ChatColor.RESET + ChatColor.GRAY + " (original)");
        }
        
        // Show alts list
        if (alts.isEmpty()) {
            player.sendMessage(ChatColor.GRAY + "You have no alt accounts yet.");
            player.sendMessage(ChatColor.GRAY + "Use " + ChatColor.WHITE + "/alt <username>" + ChatColor.GRAY + " to create one!");
        } else {
            player.sendMessage(ChatColor.YELLOW + "Alt Accounts (" + alts.size() + "):");
            
            for (String altName : alts) {
                String prefix = ChatColor.GRAY + "• ";
                if (altName.equals(currentAlt)) {
                    prefix = ChatColor.GREEN + "► " + ChatColor.BOLD;
                }
                
                player.sendMessage(prefix + ChatColor.AQUA + altName + ChatColor.RESET);
            }
        }
        
        // Show footer with commands
        player.sendMessage("");
        player.sendMessage(ChatColor.GRAY + "Commands: " + ChatColor.WHITE + "/alt <name>" + ChatColor.GRAY + " | " + 
                         ChatColor.WHITE + "/main" + ChatColor.GRAY + " | " + 
                         ChatColor.WHITE + "/skin <player>" + ChatColor.GRAY + " | " + 
                         ChatColor.WHITE + "/altlist help");
        player.sendMessage("");
        
        return true;
    }
    
    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "=== " + ChatColor.YELLOW + "AltList Command Help" + ChatColor.GOLD + " ===");
        player.sendMessage("");
        player.sendMessage(ChatColor.AQUA + "/altlist" + ChatColor.GRAY + " - List all your alt accounts");
        player.sendMessage(ChatColor.GRAY + "  Shows your current account and all created alts");
        player.sendMessage("");
        player.sendMessage(ChatColor.AQUA + "/altlist help" + ChatColor.GRAY + " - Show this help message");
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "Related Commands:");
        player.sendMessage(ChatColor.WHITE + "  /alt <username>" + ChatColor.GRAY + " - Create or switch to an alt");
        player.sendMessage(ChatColor.WHITE + "  /main" + ChatColor.GRAY + " - Switch back to your main account");
        player.sendMessage(ChatColor.WHITE + "  /skin <player>" + ChatColor.GRAY + " - Change your alt's skin");
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "Permission: " + ChatColor.WHITE + "altaccount.list");
        player.sendMessage("");
    }
}