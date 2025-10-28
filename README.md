# AltAccountPlugin v1.1.0

Un plugin Minecraft pour PaperMC 1.20.4 qui permet aux joueurs de créer et gérer des comptes alternatifs avec un système de déguisement.

## Fonctionnalités

### Commandes principales
- **`/alt <nom>`** - Bascule vers un compte alternatif
- **`/main`** - Retourne au compte principal
- **`/random`** - Téléporte aléatoirement dans le monde entier (±29,999,984 blocs)
- **`/skin <nom_fichier>`** - Applique une skin depuis un fichier (nécessite ProtocolLib pour fonctionner complètement)

### Système de déguisement
- **Changement de nom d'affichage** : Votre nom apparaît comme celui de votre alt dans le chat, la liste des joueurs et au-dessus de votre tête
- **Persistance** : Vos données (inventaire, localisation, santé, etc.) sont sauvegardées pour chaque compte
- **Chat personnalisé** : Les messages de chat affichent le nom de votre alt actuel

### Permissions
- `altaccount.alt` - Permet d'utiliser `/alt`
- `altaccount.main` - Permet d'utiliser `/main`
- `altaccount.random` - Permet d'utiliser `/random`
- `altaccount.skin` - Permet d'utiliser `/skin`

Par défaut, tous les joueurs OP ont accès à toutes les commandes.

## Installation

1. Téléchargez le fichier JAR depuis le dossier `target/`
2. Placez-le dans le dossier `plugins/` de votre serveur PaperMC 1.20.4
3. Redémarrez le serveur
4. (Optionnel) Installez ProtocolLib pour les fonctionnalités avancées de skin

## Configuration

Le plugin crée automatiquement les dossiers nécessaires :
- `plugins/AltAccountPlugin/alts/` - Stockage des données des alts
- `plugins/AltAccountPlugin/skins/` - Fichiers de skins (optionnel)

### Format des fichiers de skin
Pour utiliser la commande `/skin`, créez des fichiers `.txt` dans `plugins/AltAccountPlugin/skins/` :
```
ligne 1: données de texture base64
ligne 2: signature base64
```

## Utilisation

### Créer un alt
```
/alt MonNouveauAlt
```
- Sauvegarde automatiquement vos données actuelles
- Vous téléporte dans le monde avec l'identité de votre alt
- Votre nom d'affichage devient "MonNouveauAlt"

### Retourner au principal
```
/main
```
- Sauvegarde les données de l'alt
- Restaure vos données principales
- Remet votre nom d'affichage original

### Téléportation aléatoire
```
/random
```
- Téléporte dans un rayon de ±29,999,984 blocs
- Recherche automatiquement une position sûre
- Fonctionne depuis n'importe quel compte

## Limitations actuelles

- **Changement de skin** : Sans ProtocolLib, les skins ne changent pas visuellement
- **Nom au-dessus de la tête** : Limité aux capacités de Bukkit/Paper
- **Synchronisation** : Les changements de nom peuvent nécessiter une reconnexion pour être totalement visibles

## Versions

### v1.1.0 (Actuelle)
- Version simplifiée sans dépendance ProtocolLib obligatoire
- Changement de nom dans le chat et la liste des joueurs
- Système de déguisement basique fonctionnel
- Compatibilité maximale avec PaperMC 1.20.4
- Suppression des dépendances externes problématiques

### v1.0.1
- Version avec ProtocolLib (problèmes de compilation)
- Correction des bugs de désérialisation

### Versions futures
- Intégration ProtocolLib optionnelle pour changement de skin complet
- Interface graphique pour la gestion des alts
- Système de permissions plus granulaire

## Développement

### Structure du projet
```
src/main/java/com/example/altaccount/
├── AltAccountPlugin.java          # Classe principale
├── commands/                      # Commandes du plugin
│   ├── AltCommand.java
│   ├── MainCommand.java
│   ├── RandomCommand.java
│   └── SkinCommand.java
├── data/                          # Gestion des données
│   ├── AltData.java
│   └── AltDataManager.java
└── disguise/                      # Système de déguisement
    └── DisguiseManager.java
```

### Compilation
```bash
mvn clean package
```

Le JAR final se trouve dans `target/alt-account-plugin-1.1.0.jar`

## Support

Compatible avec :
- PaperMC 1.20.4
- Java 17+
- Bukkit/Spigot API 1.20.4

Optionnel :
- ProtocolLib (pour les fonctionnalités de skin avancées)
- LuckPerms (pour la gestion des permissions)