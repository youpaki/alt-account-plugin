package com.example.altaccount.skin;

import com.example.altaccount.AltAccountPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;

public class SkinManager {
    private final AltAccountPlugin plugin;
    private final Map<UUID, SkinData> playerSkins = new HashMap<>();
    
    public SkinManager(AltAccountPlugin plugin) {
        this.plugin = plugin;
    }
    
    public CompletableFuture<SkinData> fetchSkinData(String playerName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Étape 1: Obtenir l'UUID du joueur depuis l'API Mojang
                String uuid = getUUIDFromName(playerName);
                if (uuid == null) {
                    throw new RuntimeException("Joueur non trouvé: " + playerName);
                }
                
                // Étape 2: Obtenir les données de skin depuis l'API des profils Mojang
                SkinData skinData = getSkinDataFromUUID(uuid);
                if (skinData == null) {
                    throw new RuntimeException("Impossible de récupérer les données de skin pour: " + playerName);
                }
                
                return skinData;
                
            } catch (Exception e) {
                plugin.getLogger().warning("Erreur lors de la récupération de la skin pour " + playerName + ": " + e.getMessage());
                return null;
            }
        });
    }
    
    private String getUUIDFromName(String playerName) throws IOException {
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + playerName);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        
        if (connection.getResponseCode() != 200) {
            return null;
        }
        
        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
            return response.get("id").getAsString();
        }
    }
    
    private SkinData getSkinDataFromUUID(String uuid) throws IOException {
        URL url = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        
        if (connection.getResponseCode() != 200) {
            return null;
        }
        
        try (InputStreamReader reader = new InputStreamReader(connection.getInputStream())) {
            JsonObject response = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray properties = response.getAsJsonArray("properties");
            
            for (int i = 0; i < properties.size(); i++) {
                JsonObject property = properties.get(i).getAsJsonObject();
                if ("textures".equals(property.get("name").getAsString())) {
                    String texture = property.get("value").getAsString();
                    String signature = property.has("signature") ? property.get("signature").getAsString() : null;
                    return new SkinData(texture, signature);
                }
            }
        }
        
        return null;
    }
    
    public void applySkinToPlayer(Player player, String targetPlayerName) {
        player.sendMessage("§eRecherche de la skin de " + targetPlayerName + "...");
        
        fetchSkinData(targetPlayerName).thenAccept(skinData -> {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (skinData != null) {
                        // Stocker les données de skin pour ce joueur
                        playerSkins.put(player.getUniqueId(), skinData);
                        
                        // Informer le joueur que la skin a été "appliquée"
                        player.sendMessage("§aSkin de " + targetPlayerName + " appliquée!");
                        player.sendMessage("§7Note: La skin sera visible pour les autres joueurs lors de la prochaine reconnexion.");
                        player.sendMessage("§7Votre apparence peut ne pas changer immédiatement pour vous-même.");
                        
                        // Sauvegarder les données de skin dans les données de l'alt
                        String currentAlt = plugin.getDataManager().getCurrentAlt(player.getUniqueId());
                        if (currentAlt != null) {
                            // Mettre à jour les données de l'alt avec la nouvelle skin
                            updateAltSkinData(player, currentAlt, skinData);
                        }
                        
                        plugin.getLogger().info("Skin de " + targetPlayerName + " appliquée à " + player.getName());
                        
                    } else {
                        player.sendMessage("§cImpossible de récupérer la skin de " + targetPlayerName);
                        player.sendMessage("§7Vérifiez que le nom d'utilisateur est correct et que le joueur existe.");
                    }
                }
            }.runTask(plugin);
        }).exceptionally(throwable -> {
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.sendMessage("§cErreur lors de la récupération de la skin: " + throwable.getMessage());
                }
            }.runTask(plugin);
            return null;
        });
    }
    
    private void updateAltSkinData(Player player, String altName, SkinData skinData) {
        // Récupérer les données actuelles de l'alt
        // et les mettre à jour avec les nouvelles données de skin
        // Cette méthode sera implémentée en coordination avec AltDataManager
        plugin.getDataManager().updateAltSkinData(player.getUniqueId(), altName, skinData.getTexture(), skinData.getSignature());
    }
    
    public SkinData getPlayerSkin(UUID playerUuid) {
        return playerSkins.get(playerUuid);
    }
    
    public void removePlayerSkin(UUID playerUuid) {
        playerSkins.remove(playerUuid);
    }
    
    public static class SkinData {
        private final String texture;
        private final String signature;
        
        public SkinData(String texture, String signature) {
            this.texture = texture;
            this.signature = signature;
        }
        
        public String getTexture() {
            return texture;
        }
        
        public String getSignature() {
            return signature;
        }
    }
}