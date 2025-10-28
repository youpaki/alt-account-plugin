# 🎮 Installation du Plugin AltAccount

## ✅ Compilation Réussie !

Le plugin a été compilé avec succès. Vous trouverez le fichier JAR prêt à l'emploi dans :
```
target/alt-account-plugin-1.0.0.jar
```

## 🚀 Installation sur votre serveur

### 1. **Copier le plugin**
```bash
cp target/alt-account-plugin-1.0.0.jar /path/to/your/server/plugins/
```

### 2. **Redémarrer le serveur**
Redémarrez votre serveur PaperMC pour charger le plugin.

### 3. **Vérifier l'installation**
Dans la console du serveur, vous devriez voir :
```
[INFO] AltAccountPlugin has been enabled!
```

### 4. **Test immédiat pour les OPs**
Les joueurs OP peuvent utiliser immédiatement toutes les commandes :
```
/alt MonPremierAlt
/main
/random
```

## 🔐 Configuration des Permissions (LuckPerms)

> **Note** : Les joueurs OP ont automatiquement accès à toutes les commandes !

### Pour donner les permissions aux joueurs non-OP :

```bash
# Créer un groupe pour les utilisateurs d'alts
/lp creategroup altusers

# Donner toutes les permissions du plugin à ce groupe
/lp group altusers permission set altaccount.* true

# Ajouter un joueur au groupe
/lp user <pseudo_joueur> parent add altusers
```

### Permissions disponibles :
- `altaccount.alt` - Utiliser `/alt <username>` (défaut: OP)
- `altaccount.main` - Utiliser `/main` (défaut: OP)
- `altaccount.random` - Utiliser `/random` (défaut: OP)
- `altaccount.*` - Toutes les permissions (défaut: OP)

## 🎯 Test en jeu

1. **Connectez-vous** au serveur
2. **Testez les commandes** :
   ```
   /alt MonPremierAlt
   /main
   /random
   ```

## 📁 Structure des données

Les données des alts sont sauvegardées dans :
```
plugins/AltAccountPlugin/alts/
```

Chaque fichier `.yml` contient toutes les données d'un alt (inventaire, position, XP, etc.).

## 🔧 Dépannage

### Le plugin ne se charge pas :
- Vérifiez que vous utilisez **PaperMC 1.20.4**
- Vérifiez les logs pour des erreurs
- Assurez-vous que **Java 17+** est utilisé

### Les permissions ne fonctionnent pas :
- Vérifiez que **LuckPerms** est installé
- Rechargez les permissions : `/lp reload`
- Testez avec un admin : `/op <pseudo>`

### Les commandes ne marchent pas :
- Vérifiez que le joueur a les bonnes permissions
- Consultez les logs pour les erreurs

## 📞 Support

En cas de problème :
1. Vérifiez les logs du serveur
2. Testez avec les permissions admin
3. Vérifiez la version de PaperMC et Java

---

**Bon jeu avec vos comptes alts ! 🎮**