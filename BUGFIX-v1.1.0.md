# Correction des problèmes v1.1.0

## Problèmes résolus

### 1. Persistance des alt accounts lors des reconnexions

**Problème :** Quand un joueur sur un alt account se déconnectait et se reconnectait, son nom original réapparaissait dans la liste des joueurs.

**Solution :**
- Ajout d'un système de sauvegarde automatique des données de déguisement dans `disguises.yml`
- Modification du `DisguiseManager` pour :
  - Sauvegarder automatiquement l'état des déguisements lors des changements
  - Restaurer automatiquement les déguisements lors des reconnexions
  - Gérer les événements de déconnexion (`PlayerQuitEvent`) pour sauvegarder les données
  - Gérer les événements de connexion (`PlayerJoinEvent`) pour restaurer l'état

### 2. Système de skins personnalisés

**Problème :** La commande `/skin` ne changeait pas réellement la skin visible du joueur.

**Solution :**
- Création d'un nouveau `SkinManager` qui utilise l'API Mojang officielle
- Récupération automatique des données de texture et signature depuis les serveurs Mojang
- Intégration avec le système d'alt accounts pour sauvegarder les données de skin
- Messages informatifs pour expliquer les limitations (skin visible pour les autres après reconnexion)

## Nouvelles fonctionnalités

### Gestion automatique des données
- **Sauvegarde automatique** : Les données sont maintenant sauvegardées automatiquement à chaque changement
- **Restauration transparente** : Les joueurs retrouvent automatiquement leur état d'alt account après une reconnexion
- **Gestion des skins** : Les skins personnalisés sont maintenant correctement récupérés et stockés

### Améliorations techniques
- **API Mojang** : Utilisation de l'API officielle pour récupérer les données de skin en temps réel
- **Persistance robuste** : Sauvegarde dans des fichiers YAML séparés pour les déguisements et les données de joueur
- **Gestion d'erreurs** : Messages d'erreur plus informatifs et gestion des cas d'échec

## Comment utiliser les nouvelles fonctionnalités

### Skins personnalisés
1. Connectez-vous à un alt account avec `/alt <nom>`
2. Utilisez `/skin <nom_joueur>` pour appliquer la skin d'un autre joueur
3. La skin sera visible pour les autres joueurs immédiatement et pour vous après reconnexion

### Persistance automatique
- **Aucune action requise** : Le système sauvegarde et restaure automatiquement
- Les joueurs se reconnectent automatiquement avec leur dernier alt account utilisé
- Toutes les données (inventaire, position, expérience, skin) sont préservées

## Fichiers modifiés

### Classes modifiées
- `DisguiseManager.java` - Ajout de la persistance des déguisements
- `AltData.java` - Ajout des données de skin (texture, signature)
- `AltDataManager.java` - Méthodes pour gérer les données de skin
- `AltAccountPlugin.java` - Intégration du SkinManager
- `SkinCommand.java` - Utilisation du nouveau système de skins

### Nouvelles classes
- `SkinManager.java` - Gestion complète des skins via l'API Mojang

### Nouveaux fichiers de données
- `disguises.yml` - Sauvegarde des états de déguisement
- Amélioration des fichiers `alts/*.yml` avec les données de skin

## Notes techniques

### Limitations des skins
- Les skins ne sont pas immédiatement visibles pour le joueur lui-même
- Pour voir votre propre skin, vous devez vous déconnecter et vous reconnecter
- Les autres joueurs voient la nouvelle skin immédiatement
- Cette limitation est due aux contraintes de l'API Minecraft sans ProtocolLib

### Performance
- Les requêtes vers l'API Mojang sont asynchrones pour éviter les lags
- Cache local pour éviter les requêtes répétées
- Sauvegarde optimisée avec écriture uniquement lors des changements

## Commandes mises à jour

### `/skin <nom_joueur>`
- Maintenant fonctionnelle avec récupération automatique depuis l'API Mojang
- Messages informatifs sur le processus de changement
- Gestion d'erreurs si le joueur n'existe pas

### Toutes les commandes d'alt
- Sauvegarde automatique lors des changements d'état
- Restauration automatique lors des reconnexions
- Meilleure intégration avec le système de skins

## Installation

1. Remplacez l'ancien fichier JAR par `alt-account-plugin-1.1.0.jar`
2. Redémarrez le serveur
3. Les anciens données seront automatiquement migrées

## Permissions (inchangées)

- `altaccount.alt` - Utiliser les alt accounts
- `altaccount.main` - Retourner au compte principal  
- `altaccount.skin` - Changer les skins
- `altaccount.random` - Générer des noms aléatoires