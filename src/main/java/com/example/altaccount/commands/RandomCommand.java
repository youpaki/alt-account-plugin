package com.example.altaccount.commands;

import com.example.altaccount.AltAccountPlugin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Random;

public class RandomCommand implements CommandExecutor {
    
    private final AltAccountPlugin plugin;
    private final Random random;
    
    public RandomCommand(AltAccountPlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
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
        if (!player.hasPermission("altaccount.random")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use random teleport!");
            return true;
        }
        
        World world = player.getWorld();
        Location randomLocation = findSafeRandomLocation(world);
        
        if (randomLocation != null) {
            player.teleport(randomLocation);
            player.sendMessage(ChatColor.GREEN + "Teleported to a random location!");
            player.sendMessage(ChatColor.GRAY + "Coordinates: " + 
                ChatColor.YELLOW + "X: " + randomLocation.getBlockX() + 
                ChatColor.GRAY + ", " + 
                ChatColor.YELLOW + "Y: " + randomLocation.getBlockY() + 
                ChatColor.GRAY + ", " + 
                ChatColor.YELLOW + "Z: " + randomLocation.getBlockZ());
        } else {
            player.sendMessage(ChatColor.RED + "Could not find a safe random location. Please try again.");
        }
        
        return true;
    }
    
    private Location findSafeRandomLocation(World world) {
        // Try to find a safe location up to 15 times with full world range
        for (int attempts = 0; attempts < 15; attempts++) {
            // Generate random coordinates within the full Minecraft world bounds
            // Range: -29,999,984 to +29,999,984 (full world border)
            int x = random.nextInt(59999968) - 29999984; // Full X range
            int z = random.nextInt(59999968) - 29999984; // Full Z range
            
            // Get the highest block at this location
            int y = world.getHighestBlockYAt(x, z);
            
            // Create the location
            Location location = new Location(world, x + 0.5, y + 1, z + 0.5);
            
            // Check if the location is safe
            if (isSafeLocation(location)) {
                return location;
            }
        }
        
        // If no safe location found, try spawn area as fallback
        Location spawn = world.getSpawnLocation();
        return findSafeLocationNearby(spawn, 1000);
    }
    
    private boolean isSafeLocation(Location location) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        
        // Check if Y is within reasonable bounds
        if (y < 1 || y > world.getMaxHeight() - 2) {
            return false;
        }
        
        // Check the block below (should be solid)
        Material blockBelow = world.getBlockAt(x, y - 1, z).getType();
        if (!blockBelow.isSolid()) {
            return false;
        }
        
        // Check for dangerous blocks below
        if (isHazardousBlock(blockBelow)) {
            return false;
        }
        
        // Check the two blocks above (should be air or passable)
        Material blockAtFeet = world.getBlockAt(x, y, z).getType();
        Material blockAtHead = world.getBlockAt(x, y + 1, z).getType();
        
        if (!isPassableBlock(blockAtFeet) || !isPassableBlock(blockAtHead)) {
            return false;
        }
        
        // Check for lava or water nearby
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                Material nearbyBlock = world.getBlockAt(x + dx, y - 1, z + dz).getType();
                if (nearbyBlock == Material.LAVA || nearbyBlock == Material.WATER) {
                    // Allow water but not lava
                    if (nearbyBlock == Material.LAVA) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    private boolean isPassableBlock(Material material) {
        return material == Material.AIR || 
               material == Material.SHORT_GRASS || 
               material == Material.TALL_GRASS ||
               material == Material.FERN ||
               material == Material.LARGE_FERN ||
               material == Material.POPPY ||
               material == Material.DANDELION ||
               !material.isSolid();
    }
    
    private boolean isHazardousBlock(Material material) {
        return material == Material.LAVA ||
               material == Material.MAGMA_BLOCK ||
               material == Material.FIRE ||
               material == Material.CACTUS;
    }
    
    private Location findSafeLocationNearby(Location center, int radius) {
        World world = center.getWorld();
        
        for (int attempts = 0; attempts < 30; attempts++) {
            int x = center.getBlockX() + random.nextInt(radius * 2) - radius;
            int z = center.getBlockZ() + random.nextInt(radius * 2) - radius;
            int y = world.getHighestBlockYAt(x, z);
            
            Location location = new Location(world, x + 0.5, y + 1, z + 0.5);
            
            if (isSafeLocation(location)) {
                return location;
            }
        }
        
        return null;
    }
    
    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GOLD + "=== " + ChatColor.YELLOW + "Random Command Help" + ChatColor.GOLD + " ===");
        player.sendMessage("");
        player.sendMessage(ChatColor.AQUA + "/random" + ChatColor.GRAY + " - Teleport to a random location");
        player.sendMessage(ChatColor.GRAY + "  Finds a safe location anywhere in the world and teleports you there");
        player.sendMessage(ChatColor.GRAY + "  Coordinates range from -29,999,984 to +29,999,984");
        player.sendMessage("");
        player.sendMessage(ChatColor.AQUA + "/random help" + ChatColor.GRAY + " - Show this help message");
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "Safety Features:");
        player.sendMessage(ChatColor.GRAY + "  • Avoids lava, fire, and other hazards");
        player.sendMessage(ChatColor.GRAY + "  • Finds solid ground with air above");
        player.sendMessage(ChatColor.GRAY + "  • Falls back to spawn area if no safe location found");
        player.sendMessage(ChatColor.GRAY + "  • Checks multiple locations before giving up");
        player.sendMessage("");
        player.sendMessage(ChatColor.YELLOW + "Usage Tips:");
        player.sendMessage(ChatColor.GRAY + "  • Great for exploration and finding new areas");
        player.sendMessage(ChatColor.GRAY + "  • Works in any world dimension");
        player.sendMessage(ChatColor.GRAY + "  • Can be used repeatedly for different locations");
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "Permission: " + ChatColor.WHITE + "altaccount.random");
        player.sendMessage("");
    }
}