package com.example.altaccount.disguise;

import com.example.altaccount.AltAccountPlugin;
import com.example.altaccount.data.AltData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class DisguiseManager implements Listener {
    private final AltAccountPlugin plugin;
    private final Map<UUID, String> disguisedPlayers = new HashMap<>();
    private final File disguiseDataFile;
    
    public DisguiseManager(AltAccountPlugin plugin) {
        this.plugin = plugin;
        this.disguiseDataFile = new File(plugin.getDataFolder(), "disguises.yml");
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        loadDisguiseData();
    }
    
    public void disguisePlayer(Player player, String altName) {
        disguisedPlayers.put(player.getUniqueId(), altName);
        
        // Update player's display name
        player.setDisplayName(altName);
        player.setPlayerListName(altName);
        
        // Sauvegarder immédiatement les données de déguisement
        saveDisguiseData();
        
        plugin.getLogger().info("Déguisé " + player.getName() + " en " + altName);
    }
    
    public void undisguisePlayer(Player player) {
        disguisedPlayers.remove(player.getUniqueId());
        
        // Reset player's display name to original
        player.setDisplayName(player.getName());
        player.setPlayerListName(player.getName());
        
        // Sauvegarder immédiatement les données de déguisement
        saveDisguiseData();
        
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
        
        // Vérifier si le joueur était sur un alt account avant de se déconnecter
        String lastAlt = plugin.getDataManager().getCurrentAlt(player.getUniqueId());
        
        if (lastAlt != null) {
            // Le joueur était sur un alt account, le restaurer
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnline()) {
                        disguisePlayer(player, lastAlt);
                        player.sendMessage("§aVous êtes automatiquement reconnecté en tant que: §e" + lastAlt);
                        plugin.getLogger().info(player.getName() + " s'est reconnecté sur son alt: " + lastAlt);
                    }
                }
            }.runTaskLater(plugin, 20L); // Attendre 1 seconde pour que le joueur soit complètement chargé
        } else {
            // Le joueur était sur son compte principal
            plugin.getLogger().info(player.getName() + " s'est connecté en mode principal");
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        
        // Sauvegarder l'état actuel du joueur avant qu'il se déconnecte
        String currentAlt = plugin.getDataManager().getCurrentAlt(player.getUniqueId());
        
        if (currentAlt != null) {
            // Le joueur est sur un alt, sauvegarder ses données
            plugin.getDataManager().savePlayerData(player, currentAlt);
            plugin.getLogger().info("Données sauvegardées pour " + player.getName() + " (alt: " + currentAlt + ")");
        } else {
            // Le joueur est sur son compte principal, sauvegarder ses données principales
            plugin.getDataManager().savePlayerData(player, "main");
            plugin.getLogger().info("Données principales sauvegardées pour " + player.getName());
        }
        
        // Note: On ne supprime PAS le joueur de disguisedPlayers car on veut
        // qu'il se reconnecte avec le même déguisement
    }
    
    public void applySkinFromFile(Player player, String skinFileName) {
        // Note: Cette méthode nécessiterait ProtocolLib pour changer réellement la skin
        // Pour l'instant, on affiche juste un message
        player.sendMessage("§aChangement de skin demandé pour: " + skinFileName);
        player.sendMessage("§cNote: Le changement de skin nécessite ProtocolLib (non installé)");
        player.sendMessage("§ePour changer votre skin, utilisez un client Minecraft avec la skin souhaitée");
    }
    
    private void loadDisguiseData() {
        if (!disguiseDataFile.exists()) {
            return;
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(disguiseDataFile);
        
        for (String key : config.getKeys(false)) {
            try {
                UUID uuid = UUID.fromString(key);
                String disguiseName = config.getString(key);
                if (disguiseName != null) {
                    disguisedPlayers.put(uuid, disguiseName);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid UUID in disguise data: " + key);
            }
        }
        
        plugin.getLogger().info("Loaded " + disguisedPlayers.size() + " disguise entries");
    }
    
    private void saveDisguiseData() {
        FileConfiguration config = new YamlConfiguration();
        
        for (Map.Entry<UUID, String> entry : disguisedPlayers.entrySet()) {
            config.set(entry.getKey().toString(), entry.getValue());
        }
        
        try {
            config.save(disguiseDataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save disguise data: " + e.getMessage());
        }
    }
    
    public void saveData() {
        saveDisguiseData();
    }
}