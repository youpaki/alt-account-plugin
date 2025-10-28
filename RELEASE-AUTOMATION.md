# Scripts de Release Automatisés

Ce dossier contient des scripts pour automatiser le processus de release du plugin AltAccount.

## 📋 Scripts disponibles

### 🚀 `release.sh` - Release automatisée complète

Script principal qui automatise tout le processus de création d'une nouvelle version.

**Usage :**
```bash
./release.sh [major|minor|patch] [message]
```

**Exemples :**
```bash
# Release patch (1.2.0 → 1.2.1)
./release.sh patch "Correction de bugs mineurs"

# Release minor (1.2.0 → 1.3.0)  
./release.sh minor "Nouvelles fonctionnalités"

# Release major (1.2.0 → 2.0.0)
./release.sh major "Refactoring complet"

# Release patch avec message par défaut
./release.sh
```

**Ce que fait le script :**
1. ✅ Met à jour la version dans `pom.xml`
2. ✅ Ajoute une entrée dans `CHANGELOG.md`
3. ✅ Compile le projet (`mvn clean package`)
4. ✅ Commit les changements
5. ✅ Crée et push le tag Git
6. ✅ Crée automatiquement la release GitHub avec le JAR

### 🎯 `create-release.sh` - Création de release GitHub

Script pour créer une release GitHub à partir d'un tag existant.

**Usage :**
```bash
./create-release.sh [version]
```

**Exemples :**
```bash
# Créer une release pour la version actuelle
./create-release.sh

# Créer une release pour une version spécifique
./create-release.sh 1.2.0
```

**Ce que fait le script :**
1. ✅ Vérifie que le tag existe
2. ✅ Compile le JAR si nécessaire
3. ✅ Extrait les notes de release depuis `CHANGELOG.md`
4. ✅ Crée la release GitHub
5. ✅ Upload le JAR compilé comme asset

## 🛠️ Prérequis

### GitHub CLI (requis)
```bash
# Installation sur macOS
brew install gh

# Authentification
gh auth login
```

### Permissions Git
Assurez-vous d'avoir les droits de push sur le repository.

## 📦 Exemple de workflow complet

```bash
# 1. Développer vos fonctionnalités
git add .
git commit -m "Ajout de nouvelles fonctionnalités"

# 2. Créer une nouvelle release
./release.sh minor "Ajout de /altlist et support /help"

# 3. Le script fait automatiquement :
#    - Met à jour les versions
#    - Compile le projet  
#    - Crée les commits et tags
#    - Push vers GitHub
#    - Crée la release avec le JAR
```

## 🎯 Avantages

### ✅ **Automatisation complète**
- Plus besoin de modifier manuellement `pom.xml`
- Plus besoin de créer manuellement les tags
- Plus besoin d'uploader manuellement les JARs

### ✅ **Cohérence**
- Versioning sémantique automatique
- Format consistent des commits et tags
- Documentation automatique dans CHANGELOG

### ✅ **Distribution facile**
- JARs automatiquement disponibles sur GitHub Releases
- Utilisateurs peuvent télécharger directement les versions compilées
- Plus besoin de compiler localement pour les utilisateurs

## 🔄 Versions disponibles

Les utilisateurs peuvent télécharger les JARs compilés depuis :
- **Dernière version :** https://github.com/youpaki/alt-account-plugin/releases/latest
- **Toutes les versions :** https://github.com/youpaki/alt-account-plugin/releases

## 🐛 Dépannage

### Erreur "GitHub CLI not found"
```bash
brew install gh
gh auth login
```

### Erreur "Tag doesn't exist"
Le script `create-release.sh` nécessite que le tag existe déjà. Utilisez `release.sh` pour un workflow complet.

### Erreur de compilation
```bash
# Vérifier que Java 17+ est installé
java -version

# Nettoyer et recompiler
mvn clean package
```

## 📝 Notes

- Les JARs sont automatiquement nommés `alt-account-plugin-X.Y.Z.jar`
- Les notes de release sont extraites automatiquement du `CHANGELOG.md`
- Le versioning suit le standard sémantique (major.minor.patch)
- Les releases GitHub sont créées avec le statut "latest" pour la version la plus récente