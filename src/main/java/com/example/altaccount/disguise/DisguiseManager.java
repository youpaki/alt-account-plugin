package com.example.altaccount.disguise;

import com.example.altaccount.AltAccountPlugin;
import com.example.altaccount.data.AltData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DisguiseManager implements Listener {
    private final AltAccountPlugin plugin;
    private final Map<UUID, String> disguisedPlayers = new HashMap<>();
    
    public DisguiseManager(AltAccountPlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    public void disguisePlayer(Player player, String altName) {
        disguisedPlayers.put(player.getUniqueId(), altName);
        
        // Update player's display name
        player.setDisplayName(altName);
        player.setPlayerListName(altName);
        
        plugin.getLogger().info("Déguisé " + player.getName() + " en " + altName);
    }
    
    public void undisguisePlayer(Player player) {
        disguisedPlayers.remove(player.getUniqueId());
        
        // Reset player's display name to original
        player.setDisplayName(player.getName());
        player.setPlayerListName(player.getName());
        
        plugin.getLogger().info("Révélé " + player.getName());
    }
    
    public boolean isDisguised(Player player) {
        return disguisedPlayers.containsKey(player.getUniqueId());
    }
    
    public String getDisguiseName(Player player) {
        return disguisedPlayers.get(player.getUniqueId());
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (isDisguised(player)) {
            String disguiseName = getDisguiseName(player);
            event.setFormat("<%s> %s".formatted(disguiseName, event.getMessage()));
        }
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Au join, le joueur est en mode principal par défaut
        // Le déguisement sera appliqué quand ils utilisent /alt
        plugin.getLogger().info(player.getName() + " s'est connecté en mode principal");
    }
    
    public void applySkinFromFile(Player player, String skinFileName) {
        // Note: Cette méthode nécessiterait ProtocolLib pour changer réellement la skin
        // Pour l'instant, on affiche juste un message
        player.sendMessage("§aChangement de skin demandé pour: " + skinFileName);
        player.sendMessage("§cNote: Le changement de skin nécessite ProtocolLib (non installé)");
        player.sendMessage("§ePour changer votre skin, utilisez un client Minecraft avec la skin souhaitée");
    }
}