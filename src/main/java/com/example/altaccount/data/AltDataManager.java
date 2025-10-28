package com.example.altaccount.data;

import com.example.altaccount.AltAccountPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AltDataManager {
    
    private final AltAccountPlugin plugin;
    private final File dataFolder;
    private final Map<UUID, String> currentAltMap; // Player UUID -> Current Alt Name
    private final Map<String, AltData> altDataCache; // Alt Key -> Alt Data
    
    public AltDataManager(AltAccountPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "alts");
        this.currentAltMap = new HashMap<>();
        this.altDataCache = new HashMap<>();
        
        // Register serialization
        ConfigurationSerialization.registerClass(AltData.class);
        
        // Create data folder if it doesn't exist
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
        
        loadAllData();
    }
    
    public void savePlayerData(Player player, String altName) {
        AltData data = createAltDataFromPlayer(player);
        String key = getAltKey(player.getUniqueId(), altName);
        altDataCache.put(key, data);
        saveAltDataToFile(key, data);
    }
    
    public void loadPlayerData(Player player, String altName) {
        String key = getAltKey(player.getUniqueId(), altName);
        AltData data = altDataCache.get(key);
        
        if (data == null) {
            data = loadAltDataFromFile(key);
        }
        
        if (data != null) {
            applyAltDataToPlayer(player, data);
            currentAltMap.put(player.getUniqueId(), altName);
        }
    }
    
    public boolean hasAltData(UUID playerUuid, String altName) {
        String key = getAltKey(playerUuid, altName);
        return altDataCache.containsKey(key) || getAltDataFile(key).exists();
    }
    
    public String getCurrentAlt(UUID playerUuid) {
        return currentAltMap.get(playerUuid);
    }
    
    public void setCurrentAlt(UUID playerUuid, String altName) {
        if (altName == null) {
            currentAltMap.remove(playerUuid);
        } else {
            currentAltMap.put(playerUuid, altName);
        }
    }
    
    public boolean isOnMainAccount(UUID playerUuid) {
        return !currentAltMap.containsKey(playerUuid);
    }
    
    private AltData createAltDataFromPlayer(Player player) {
        // Récupérer les données de skin actuelles du joueur
        String skinTexture = null;
        String skinSignature = null;
        
        // Note: Pour récupérer les vraies données de skin, nous aurions besoin de ProtocolLib
        // Pour l'instant, nous stockons null et gérerons cela différemment
        
        return new AltData(
            player.getInventory().getContents(),
            player.getInventory().getArmorContents(),
            player.getInventory().getItemInOffHand(),
            player.getLocation(),
            player.getTotalExperience(),
            player.getLevel(),
            player.getExhaustion(),
            player.getFoodLevel(),
            player.getSaturation(),
            player.getHealth(),
            player.getFireTicks(),
            player.getRemainingAir(),
            skinTexture,
            skinSignature
        );
    }
    
    private void applyAltDataToPlayer(Player player, AltData data) {
        // Clear current inventory
        player.getInventory().clear();
        
        // Apply inventory and armor
        if (data.getInventory() != null) {
            player.getInventory().setContents(data.getInventory());
        }
        if (data.getArmor() != null) {
            player.getInventory().setArmorContents(data.getArmor());
        }
        if (data.getOffHand() != null) {
            player.getInventory().setItemInOffHand(data.getOffHand());
        }
        
        // Apply location
        if (data.getLocation() != null) {
            player.teleport(data.getLocation());
        }
        
        // Apply experience and level
        player.setTotalExperience(data.getExperience());
        player.setLevel(data.getLevel());
        
        // Apply health and hunger
        player.setHealth(Math.min(data.getHealth(), player.getMaxHealth()));
        player.setExhaustion(data.getExhaustion());
        player.setFoodLevel(data.getFoodLevel());
        player.setSaturation(data.getSaturation());
        
        // Apply other stats
        player.setFireTicks(data.getFireTicks());
        player.setRemainingAir(data.getAirTicks());
    }
    
    private String getAltKey(UUID playerUuid, String altName) {
        return playerUuid.toString() + "_" + altName.toLowerCase();
    }
    
    private File getAltDataFile(String key) {
        return new File(dataFolder, key + ".yml");
    }
    
    private void saveAltDataToFile(String key, AltData data) {
        File file = getAltDataFile(key);
        FileConfiguration config = new YamlConfiguration();
        config.set("data", data);
        
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save alt data for " + key + ": " + e.getMessage());
        }
    }
    
    private AltData loadAltDataFromFile(String key) {
        File file = getAltDataFile(key);
        if (!file.exists()) {
            return null;
        }
        
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        AltData data = (AltData) config.get("data");
        
        if (data != null) {
            altDataCache.put(key, data);
        }
        
        return data;
    }
    
    private void loadAllData() {
        if (!dataFolder.exists()) {
            return;
        }
        
        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String key = file.getName().replace(".yml", "");
                loadAltDataFromFile(key);
            }
        }
    }
    
    public void saveAllData() {
        for (Map.Entry<String, AltData> entry : altDataCache.entrySet()) {
            saveAltDataToFile(entry.getKey(), entry.getValue());
        }
    }
    
    public void updateAltSkinData(UUID playerUuid, String altName, String skinTexture, String skinSignature) {
        String key = getAltKey(playerUuid, altName);
        AltData data = altDataCache.get(key);
        
        if (data != null) {
            data.setSkinTexture(skinTexture);
            data.setSkinSignature(skinSignature);
            saveAltDataToFile(key, data);
            plugin.getLogger().info("Données de skin mises à jour pour l'alt " + altName + " du joueur " + playerUuid);
        }
    }
    
    public java.util.List<String> getPlayerAlts(UUID playerUuid) {
        java.util.List<String> alts = new java.util.ArrayList<>();
        String playerUuidStr = playerUuid.toString();
        
        // Parcourir tous les fichiers d'alts pour ce joueur
        if (dataFolder.exists()) {
            File[] files = dataFolder.listFiles((dir, name) -> 
                name.startsWith(playerUuidStr + "_") && name.endsWith(".yml"));
            
            if (files != null) {
                for (File file : files) {
                    String fileName = file.getName().replace(".yml", "");
                    String altName = fileName.substring(playerUuidStr.length() + 1);
                    
                    // Ne pas inclure "main" dans la liste des alts
                    if (!"main".equals(altName)) {
                        alts.add(altName);
                    }
                }
            }
        }
        
        // Trier par ordre alphabétique
        java.util.Collections.sort(alts);
        return alts;
    }
}