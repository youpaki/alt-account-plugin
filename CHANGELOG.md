# Changelog - AltAccount Plugin

## Version 1.1.0 - 28 octobre 2025

### 🔄 Corrections majeures - Persistance et Skins
- **Persistance des alt accounts** : Les joueurs gardent maintenant leur alt account lors des reconnexions
- **Système de skins fonctionnel** : La commande `/skin` récupère maintenant les vraies skins via l'API Mojang
- **Sauvegarde automatique** : Les données de déguisement sont automatiquement sauvegardées dans `disguises.yml`
- **Restauration transparente** : Reconnexion automatique avec le dernier alt account utilisé

### ✨ Nouvelles fonctionnalités
- **SkinManager** : Nouveau système utilisant l'API Mojang officielle pour les skins
- **Données de skin persistantes** : Les skins personnalisés sont maintenant sauvegardés avec les alts
- **Messages informatifs** : Meilleurs retours utilisateur lors des changements de skin
- **Gestion d'erreurs améliorée** : Messages d'erreur plus clairs pour les problèmes de réseau ou de joueurs inexistants

### 🛠️ Améliorations techniques
- **API asynchrone** : Récupération des skins sans lag du serveur
- **Cache intelligent** : Évite les requêtes répétées vers l'API Mojang
- **Intégration Gson** : Parsing JSON natif pour les réponses de l'API
- **Events optimisés** : Gestion robuste des connexions/déconnexions

### 📝 Nouvelles classes
- `SkinManager.java` - Gestion complète des skins
- `SkinData.java` - Structure de données pour les textures et signatures

### 🐛 Problèmes résolus
- **Noms originaux réapparaissant** : Les noms d'alt persistent maintenant lors des reconnexions
- **Skins non visibles** : Les skins personnalisés fonctionnent maintenant (visibles pour les autres)
- **Perte de déguisement** : Les déguisements sont automatiquement restaurés

## Version 1.0.1 - 28 octobre 2025

### 🐛 Corrections critiques
- **Erreur de désérialisation corrigée** : Fix du ClassCastException lors du chargement des données d'alt
- **Gestion des ArrayList** : Conversion automatique ArrayList → ItemStack[] pour l'inventaire et l'armure
- **Valeurs null sécurisées** : Protection contre les valeurs manquantes dans les fichiers de sauvegarde
- **Robustesse améliorée** : Le plugin ne crash plus lors du chargement d'anciens fichiers de données

## Version 1.0.0 - 28 octobre 2025

### ✨ Fonctionnalités principales
- **Système de comptes alts** : Créer et gérer plusieurs comptes alternatifs
- **Commande `/alt <username>`** : Créer ou basculer vers un compte alt
- **Commande `/main`** : Retourner au compte principal
- **Commande `/random`** : Téléportation aléatoire dans le monde entier
- **Sauvegarde complète** : Inventaire, XP, position, santé, faim, etc.
- **Permissions LuckPerms** : Système de permissions granulaire

### 🌍 Amélioration `/random`
- **Portée mondiale complète** : Téléportation dans tout le monde Minecraft
- **Coordonnées** : De -29,999,984 à +29,999,984 (limites du monde)
- **Sécurité renforcée** : Vérification de 15 emplacements avant fallback
- **Fallback intelligent** : Zone de spawn élargie si aucun emplacement sûr trouvé

### 🔐 Permissions disponibles
- `altaccount.alt` - Créer et utiliser des comptes alts (défaut: OP)
- `altaccount.main` - Retourner au compte principal (défaut: OP)
- `altaccount.random` - Téléportation aléatoire mondiale (défaut: OP)
- `altaccount.*` - Toutes les permissions (défaut: OP)

### 📋 Compatibilité
- **Minecraft** : 1.20.4
- **Serveur** : PaperMC (recommandé)
- **Java** : 17+
- **Permissions** : LuckPerms, PermissionsEx, GroupManager

### ⚡ Accès immédiat pour OPs
- **Permissions par défaut** : Tous les joueurs OP ont accès aux commandes
- **Configuration optionnelle** : LuckPerms pour les joueurs non-OP
- **Test instantané** : Aucune configuration requise pour commencer
- Validation des noms d'utilisateur (3-16 caractères)
- Vérification des emplacements de téléportation
- Protection contre la lave, chutes dangereuses
- Sauvegarde automatique des données

### 📁 Stockage
- Format YAML pour la persistance
- Dossier : `plugins/AltAccountPlugin/alts/`
- Un fichier par compte alt avec toutes les données

---

**Installation** : Copiez le JAR dans `plugins/` et redémarrez le serveur.
**Support** : Compatible avec tous les gestionnaires de permissions modernes.